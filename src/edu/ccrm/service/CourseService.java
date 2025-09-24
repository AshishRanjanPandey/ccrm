package edu.ccrm.service;

import edu.ccrm.domain.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseService {
    private final List<Course> courses = new ArrayList<>();

    public void addCourse(Course c) { courses.add(c); }
    public List<Course> listAll() { return new ArrayList<>(courses); }

    public Optional<Course> findByCode(String code) {
        return courses.stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

    public List<Course> findByInstructor(String instructor) {
        return courses.stream()
                .filter(c -> c.getInstructor().equalsIgnoreCase(instructor))
                .collect(Collectors.toList());
    }

    public boolean deactivateCourse(String code) {
        Optional<Course> oc = findByCode(code);
        oc.ifPresent(Course::deactivate);
        return oc.isPresent();
    }
}
