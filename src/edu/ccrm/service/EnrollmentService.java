package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Grade;
import edu.ccrm.domain.Student;

import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final List<Enrollment> enrollments = new ArrayList<>();
    private static final int MAX_CREDITS = 24;

    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentService(StudentService ss, CourseService cs) {
        this.studentService = ss;
        this.courseService = cs;
    }

    public boolean enroll(String regNo, String courseCode) {
        Optional<Student> os = studentService.findByRegNo(regNo);
        Optional<Course> oc = courseService.findByCode(courseCode);
        if (os.isEmpty() || oc.isEmpty()) return false;
        Student s = os.get();
        Course c = oc.get();
        int currentCredits = studentTotalCredits(regNo);
        if (currentCredits + c.getCredits() > MAX_CREDITS) {
            return false;
        }
        // prevent duplicate
        boolean exists = enrollments.stream().anyMatch(e -> e.getRegNo().equals(regNo) && e.getCourseCode().equalsIgnoreCase(courseCode));
        if (exists) return false;
        enrollments.add(new Enrollment(regNo, courseCode, null));
        s.enrollCourse(courseCode);
        return true;
    }

    public boolean assignGrade(String regNo, String courseCode, Grade grade) {
        Optional<Enrollment> oe = enrollments.stream()
                .filter(e -> e.getRegNo().equals(regNo) && e.getCourseCode().equalsIgnoreCase(courseCode))
                .findFirst();
        if (oe.isEmpty()) return false;
        oe.get().setGrade(grade);
        return true;
    }

    public int studentTotalCredits(String regNo) {
        // sum credits for courses the student is enrolled in
        List<String> codes = enrollments.stream()
                .filter(e -> e.getRegNo().equals(regNo))
                .map(Enrollment::getCourseCode)
                .collect(Collectors.toList());
        int sum = 0;
        for (String code : codes) {
            courseService.findByCode(code).ifPresent(c -> sum += c.getCredits());
        }
        return sum;
    }

    public double calculateGPA(String regNo) {
        List<Enrollment> list = enrollments.stream()
                .filter(e -> e.getRegNo().equals(regNo) && e.getGrade() != null)
                .collect(Collectors.toList());
        int totalCredits = 0;
        int totalPoints = 0;
        for (Enrollment e : list) {
            Optional<Course> oc = courseService.findByCode(e.getCourseCode());
            if (oc.isEmpty()) continue;
            int credits = oc.get().getCredits();
            totalCredits += credits;
            totalPoints += e.getGrade().getPoints() * credits;
        }
        if (totalCredits == 0) return 0.0;
        return (double) totalPoints / totalCredits;
    }

    public List<Enrollment> listEnrollmentsForStudent(String regNo) {
        return enrollments.stream().filter(e -> e.getRegNo().equals(regNo)).collect(Collectors.toList());
    }

    public List<Enrollment> listAll() {
        return new ArrayList<>(enrollments);
    }
}
