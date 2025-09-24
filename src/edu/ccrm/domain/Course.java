package edu.ccrm.domain;

public class Course {
    private String code;
    private String title;
    private int credits;
    private String instructor;
    private String semester;
    private String department;
    private boolean active = true;

    public Course(String code, String title, int credits, String instructor, String semester, String department) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.instructor = instructor;
        this.semester = semester;
        this.department = department;
    }

    // getters / setters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public String getSemester() { return semester; }
    public String getDepartment() { return department; }
    public boolean isActive() { return active; }

    public void setTitle(String title) { this.title = title; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setDepartment(String department) { this.department = department; }
    public void deactivate() { this.active = false; }

    @Override
    public String toString() {
        return String.format("%s | %s | %dcr | %s | %s | %s | %s",
                code, title, credits, instructor, semester, department, active ? "ACTIVE" : "INACTIVE");
    }
}
