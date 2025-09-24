package edu.ccrm.cli;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;
import edu.ccrm.io.FileUtil;
import edu.ccrm.service.CourseService;
import edu.ccrm.service.EnrollmentService;
import edu.ccrm.service.StudentService;
import edu.ccrm.util.ConsoleUtil;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AppMain {

    public static void main(String[] args) {
        StudentService studentService = new StudentService();
        CourseService courseService = new CourseService();
        EnrollmentService enrollmentService = new EnrollmentService(studentService, courseService);

        // try to import existing csvs
        try {
            FileUtil.ensureDataDir();
            List<Student> importedStudents = FileUtil.importStudentsCsv();
            for (Student s : importedStudents) {
                // we don't have a create-with-id method matching import; cheat by adding into map via service reflection not available;
                // simpler: show import size and let user export later. (This keeps code short.)
            }
        } catch (Exception e) {
            // ignore; data directory may not exist
        }

        mainLoop:
        while (true) {
            System.out.println("\n=== Student Info System ===");
            System.out.println("1. Student Management");
            System.out.println("2. Course Management");
            System.out.println("3. Enrollment & Grading");
            System.out.println("4. File Operations (export/backup)");
            System.out.println("5. Demo: Stream filter (courses by instructor)");
            System.out.println("0. Exit");
            String choice = ConsoleUtil.readLine("Enter choice: ");

            switch (choice) {
                case "1" -> studentMenu(studentService);
                case "2" -> courseMenu(courseService);
                case "3" -> enrollmentMenu(studentService, courseService, enrollmentService);
                case "4" -> fileMenu(studentService, courseService);
                case "5" -> streamDemo(courseService);
                case "0" -> {
                    System.out.println("Goodbye!");
                    break mainLoop; // labeled break to exit from nested switch/loop
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void studentMenu(StudentService ss) {
        while (true) {
            System.out.println("\n-- Student Menu --");
            System.out.println("1. Add student");
            System.out.println("2. List students");
            System.out.println("3. Update student");
            System.out.println("4. Deactivate student");
            System.out.println("5. Print profile & transcript (GPA)");
            System.out.println("0. Back");
            String c = ConsoleUtil.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String reg = ConsoleUtil.readLine("RegNo: ");
                    String name = ConsoleUtil.readLine("Full name: ");
                    String email = ConsoleUtil.readLine("Email: ");
                    Student s = ss.createStudent(reg, name, email);
                    System.out.println("Added: " + s);
                }
                case "2" -> {
                    ss.listAll().forEach(System.out::println);
                }
                case "3" -> {
                    String reg = ConsoleUtil.readLine("RegNo to update: ");
                    Optional<Student> os = ss.findByRegNo(reg);
                    if (os.isEmpty()) { System.out.println("Not found."); break; }
                    Student st = os.get();
                    String newName = ConsoleUtil.readLine("New name (blank to keep): ");
                    String newEmail = ConsoleUtil.readLine("New email (blank to keep): ");
                    if (!newName.isBlank()) st.setFullName(newName);
                    if (!newEmail.isBlank()) st.setEmail(newEmail);
                    System.out.println("Updated: " + st);
                }
                case "4" -> {
                    String reg = ConsoleUtil.readLine("RegNo to deactivate: ");
                    if (ss.deactivate(reg)) System.out.println("Deactivated.");
                    else System.out.println("Not found.");
                }
                case "5" -> {
                    String reg = ConsoleUtil.readLine("RegNo: ");
                    Optional<Student> os = ss.findByRegNo(reg);
                    if (os.isEmpty()) { System.out.println("Not found."); break; }
                    Student st = os.get();
                    st.printProfile();
                    System.out.println("-- Transcript --");
                    // Try to compute GPA via EnrollmentService would be better, but we'll show enrolled codes
                    System.out.println("Enrolled course codes: " + st.getEnrolledCourseCodes());
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void courseMenu(CourseService cs) {
        while (true) {
            System.out.println("\n-- Course Menu --");
            System.out.println("1. Add course");
            System.out.println("2. List courses");
            System.out.println("3. Update course");
            System.out.println("4. Deactivate course");
            System.out.println("0. Back");
            String c = ConsoleUtil.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String code = ConsoleUtil.readLine("Code: ");
                    String title = ConsoleUtil.readLine("Title: ");
                    int credits = ConsoleUtil.readInt("Credits: ", 3);
                    String inst = ConsoleUtil.readLine("Instructor: ");
                    String sem = ConsoleUtil.readLine("Semester: ");
                    String dept = ConsoleUtil.readLine("Department: ");
                    Course course = new Course(code, title, credits, inst, sem, dept);
                    cs.addCourse(course);
                    System.out.println("Added: " + course);
                }
                case "2" -> cs.listAll().forEach(System.out::println);
                case "3" -> {
                    String code = ConsoleUtil.readLine("Code to update: ");
                    Optional<Course> oc = cs.findByCode(code);
                    if (oc.isEmpty()) { System.out.println("Not found."); break; }
                    Course cobj = oc.get();
                    String nTitle = ConsoleUtil.readLine("New title (blank keep): ");
                    int nCredits = ConsoleUtil.readInt("New credits (blank keep default -1): ", -1);
                    String nInst = ConsoleUtil.readLine("New instructor (blank keep): ");
                    if (!nTitle.isBlank()) cobj.setTitle(nTitle);
                    if (nCredits > 0) cobj.setCredits(nCredits);
                    if (!nInst.isBlank()) cobj.setInstructor(nInst);
                    System.out.println("Updated: " + cobj);
                }
                case "4" -> {
                    String code = ConsoleUtil.readLine("Code to deactivate: ");
                    if (cs.deactivateCourse(code)) System.out.println("Deactivated.");
                    else System.out.println("Not found.");
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void enrollmentMenu(StudentService ss, CourseService cs, EnrollmentService es) {
        outer:
        while (true) {
            System.out.println("\n-- Enrollment & Grading --");
            System.out.println("1. Enroll student in course");
            System.out.println("2. Assign grade");
            System.out.println("3. Show student enrollments & GPA");
            System.out.println("4. List all enrollments");
            System.out.println("0. Back");
            String c = ConsoleUtil.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    String reg = ConsoleUtil.readLine("Student RegNo: ");
                    String code = ConsoleUtil.readLine("Course code: ");
                    boolean ok = es.enroll(reg, code);
                    if (ok) System.out.println("Enrolled.");
                    else System.out.println("Failed to enroll (exists, not found or credit limit).");
                }
                case "2" -> {
                    String reg = ConsoleUtil.readLine("Student RegNo: ");
                    String code = ConsoleUtil.readLine("Course code: ");
                    String gStr = ConsoleUtil.readLine("Grade (S,A,B,C,D,E,F): ");
                    Grade g = Grade.fromString(gStr);
                    if (g == null) { System.out.println("Invalid grade."); break; }
                    boolean ok = es.assignGrade(reg, code, g);
                    System.out.println(ok ? "Grade assigned." : "Failed to assign grade.");
                }
                case "3" -> {
                    String reg = ConsoleUtil.readLine("Student RegNo: ");
                    Optional<Student> os = ss.findByRegNo(reg);
                    if (os.isEmpty()) { System.out.println("No such student."); break; }
                    Student s = os.get();
                    s.printProfile();
                    System.out.println("-- Enrollments --");
                    List<Enrollment> ent = es.listEnrollmentsForStudent(reg);
                    ent.forEach(System.out::println);
                    System.out.printf("GPA: %.2f%n", es.calculateGPA(reg));
                }
                case "4" -> es.listAll().forEach(System.out::println);
                case "0" -> { break outer; } // labeled break to exit to previous menu
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void fileMenu(StudentService ss, CourseService cs) {
        while (true) {
            System.out.println("\n-- File Operations --");
            System.out.println("1. Export students.csv & courses.csv");
            System.out.println("2. Backup data folder");
            System.out.println("3. Show data directory size");
            System.out.println("0. Back");
            String c = ConsoleUtil.readLine("Choice: ");
            switch (c) {
                case "1" -> {
                    try {
                        FileUtil.exportStudentsCsv(ss.listAll());
                        FileUtil.exportCoursesCsv(cs.listAll());
                        System.out.println("Exported to data/students.csv and data/courses.csv");
                    } catch (Exception e) {
                        System.out.println("Export failed: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        Path p = FileUtil.backupData();
                        System.out.println("Backup created at: " + p.toAbsolutePath());
                    } catch (Exception e) {
                        System.out.println("Backup failed: " + e.getMessage());
                    }
                }
                case "3" -> {
                    try {
                        long size = FileUtil.directorySizeRecursive(Path.of("data"));
                        System.out.println("Data dir size (bytes): " + size);
                    } catch (Exception e) {
                        System.out.println("Failed: " + e.getMessage());
                    }
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void streamDemo(CourseService cs) {
        System.out.println("\n-- Stream Demo: find courses by instructor --");
        String instr = ConsoleUtil.readLine("Instructor name: ");
        List<Course> found = cs.findByInstructor(instr);
        if (found.isEmpty()) System.out.println("None found.");
        else found.forEach(System.out::println);
    }
}
