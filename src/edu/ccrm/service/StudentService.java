package edu.ccrm.service;

import edu.ccrm.domain.Student;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StudentService {
    private final Map<String, Student> studentsByReg = new HashMap<>();
    private int nextId = 1;

    public Student createStudent(String regNo, String name, String email) {
        Student s = new Student(nextId++, regNo, name, email, LocalDate.now());
        studentsByReg.put(regNo, s);
        return s;
    }

    public List<Student> listAll() {
        return studentsByReg.values().stream().sorted(Comparator.comparing(Student::getId)).collect(Collectors.toList());
    }

    public Optional<Student> findByRegNo(String regNo) {
        return Optional.ofNullable(studentsByReg.get(regNo));
    }

    public boolean deactivate(String regNo) {
        Optional<Student> s = findByRegNo(regNo);
        s.ifPresent(Student::deactivate);
        return s.isPresent();
    }
}
