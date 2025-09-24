package edu.ccrm.domain;

public class Enrollment {
    private final String regNo; // student regNo
    private final String courseCode;
    private Grade grade;

    public Enrollment(String regNo, String courseCode, Grade grade) {
        this.regNo = regNo;
        this.courseCode = courseCode;
        this.grade = grade;
    }

    public String getRegNo() { return regNo; }
    public String getCourseCode() { return courseCode; }
    public Grade getGrade() { return grade; }
    public void setGrade(Grade g) { this.grade = g; }

    @Override
    public String toString() {
        return String.format("%s - %s : %s", courseCode, regNo, grade == null ? "N/A" : grade.name());
    }
}
