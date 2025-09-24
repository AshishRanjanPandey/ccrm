package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String regNo;
    private String fullName;
    private String email;
    private boolean active = true;
    private final List<String> enrolledCourseCodes = new ArrayList<>();
    private LocalDate enrollmentDate;

    public Student(int id, String regNo, String fullName, String email, LocalDate enrollmentDate) {
        this.id = id;
        this.regNo = regNo;
        this.fullName = fullName;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
    }

    // getters / setters
    public int getId() { return id; }
    public String getRegNo() { return regNo; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public List<String> getEnrolledCourseCodes() { return enrolledCourseCodes; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void deactivate() { this.active = false; }

    public void enrollCourse(String courseCode) {
        if (!enrolledCourseCodes.contains(courseCode)) enrolledCourseCodes.add(courseCode);
    }

    public void unenrollCourse(String courseCode) {
        enrolledCourseCodes.remove(courseCode);
    }

    public void printProfile() {
        System.out.println("----- PROFILE -----");
        System.out.println("ID: " + id);
        System.out.println("RegNo: " + regNo);
        System.out.println("Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Enrolled courses: " + enrolledCourseCodes);
        System.out.println("Enrollment date: " + enrollmentDate);
        System.out.println("Status: " + (active ? "ACTIVE" : "INACTIVE"));
    }

    @Override
    public String toString() {
        return String.format("%d | %s | %s | %s | %s", id, regNo, fullName, email, active ? "ACTIVE" : "INACTIVE");
    }
}
