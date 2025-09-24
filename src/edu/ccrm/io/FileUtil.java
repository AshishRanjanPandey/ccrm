package edu.ccrm.io;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Student;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    private static final Path DATA_DIR = Paths.get("data");

    public static void ensureDataDir() throws IOException {
        if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
    }

    public static void exportStudentsCsv(Collection<Student> students) throws IOException {
        ensureDataDir();
        Path file = DATA_DIR.resolve("students.csv");
        List<String> lines = new ArrayList<>();
        lines.add("id,regNo,fullName,email,active,enrollmentDate,enrolledCourses");
        for (Student s : students) {
            String enrolled = String.join(";", s.getEnrolledCourseCodes());
            lines.add(String.format("%d,%s,%s,%s,%s,%s,%s",
                    s.getId(), s.getRegNo(), escape(s.getFullName()), s.getEmail(),
                    s.isActive(), s.getEnrollmentDate(), escape(enrolled)));
        }
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void exportCoursesCsv(Collection<Course> courses) throws IOException {
        ensureDataDir();
        Path file = DATA_DIR.resolve("courses.csv");
        List<String> lines = new ArrayList<>();
        lines.add("code,title,credits,instructor,semester,department,active");
        for (Course c : courses) {
            lines.add(String.format("%s,%s,%d,%s,%s,%s,%s",
                    c.getCode(), escape(c.getTitle()), c.getCredits(), c.getInstructor(), c.getSemester(), c.getDepartment(), c.isActive()));
        }
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace(",", "\\,");
    }

    public static List<Student> importStudentsCsv() throws IOException {
        Path file = DATA_DIR.resolve("students.csv");
        if (!Files.exists(file)) return Collections.emptyList();
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.skip(1).map(line -> {
                String[] parts = splitCsv(line);
                try {
                    int id = Integer.parseInt(parts[0]);
                    String regNo = parts[1];
                    String name = unescape(parts[2]);
                    String email = parts[3];
                    boolean active = Boolean.parseBoolean(parts[4]);
                    String date = parts[5];
                    String enrolled = parts.length > 6 ? unescape(parts[6]) : "";
                    Student s = new Student(id, regNo, name, email, java.time.LocalDate.parse(date));
                    if (!active) s.deactivate();
                    if (!enrolled.isBlank()) {
                        for (String code : enrolled.split(";")) {
                            if (!code.isBlank()) s.enrollCourse(code);
                        }
                    }
                    return s;
                } catch (Exception ex) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    public static List<Course> importCoursesCsv() throws IOException {
        Path file = DATA_DIR.resolve("courses.csv");
        if (!Files.exists(file)) return Collections.emptyList();
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.skip(1).map(line -> {
                String[] p = splitCsv(line);
                try {
                    String code = p[0];
                    String title = unescape(p[1]);
                    int credits = Integer.parseInt(p[2]);
                    String inst = p[3];
                    String sem = p[4];
                    String dept = p[5];
                    boolean active = Boolean.parseBoolean(p[6]);
                    Course c = new Course(code, title, credits, inst, sem, dept);
                    if (!active) c.deactivate();
                    return c;
                } catch (Exception ex) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    private static String[] splitCsv(String line) {
        // simple split that treats escaped commas \, (sufficient for basic csv here)
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (esc) {
                sb.append(ch);
                esc = false;
            } else if (ch == '\\') {
                esc = true;
            } else if (ch == ',') {
                out.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(ch);
            }
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }

    private static String unescape(String s) {
        return s == null ? "" : s.replace("\\,", ",");
    }

    public static Path backupData() throws IOException {
        ensureDataDir();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path backup = DATA_DIR.resolve("backup_" + ts);
        Files.createDirectories(backup);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(DATA_DIR)) {
            for (Path p : ds) {
                if (Files.isDirectory(p) && p.getFileName().toString().startsWith("backup_")) continue;
                if (Files.isRegularFile(p)) {
                    Files.copy(p, backup.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return backup;
    }

    public static long directorySizeRecursive(Path p) throws IOException {
        if (!Files.exists(p)) return 0L;
        try (Stream<Path> s = Files.walk(p)) {
            return s.filter(Files::isRegularFile).mapToLong(pp -> {
                try { return Files.size(pp); } catch (IOException e) { return 0L; }
            }).sum();
        }
    }
}
