# Campus Course & Records Manager (CCRM)

## Overview
A console-based Java application to manage students, instructors, courses, enrollments, grading, and reports.

---

## Table of Contents

1. Evolution of Java (short timeline)
2. Java Editions: ME vs SE vs EE (comparison)
3. Java architecture: JDK, JRE, JVM
4. Install & configure Java on Windows (step-by-step + screenshot placeholders)
5. Using Eclipse IDE: new project creation + run configs (screenshots placeholders)
6. Project structure & suggested packages
7. Coding requirements checklist (items from the assignment)
8. Key design decisions (interfaces vs inheritance, use of patterns)
9. Code snippets (main class, domain examples, singleton, builder, immutable value class)
10. Exceptions, assertions, and testing notes
11. File I/O (NIO.2) & Streams example
12. Date/Time usage
13. How to run, compile, and enable assertions
14. Next steps & tasks

---

## 1) Evolution of Java (short timeline)

* **1995** — Java 1.0 (Oak → Java): "Write once, run anywhere".
* **1997** — Java 1.1: inner classes, JDBC, reflection improvements.
* **1998–1999** — J2SE 1.2–1.3: Collections framework added, Swing matured.
* **2004** — Java 5: generics, annotations, enumerations, enhanced for-loop, autoboxing.
* **2006–2011** — Java 6 & 7: NIO.2 (Paths/Files) in 7, better concurrency utilities.
* **2014** — Java 8: Streams API, lambdas, default methods (interfaces).
* **2017** — Java 9: Module system (JPMS).
* **2018–present** — Rapid release cadence (every 6 months). LTS: Java 8, 11, 17, 21.

---

## 2) Java ME vs Java SE vs Java EE (Jakarta EE)

| Edition                  |                                                    Purpose | Typical Use-cases                                              |
| ------------------------ | ---------------------------------------------------------: | -------------------------------------------------------------- |
| **Java ME**              |             Micro Edition — small devices, limited runtime | Embedded / IoT / feature phones (constrained devices)          |
| **Java SE**              |                    Standard Edition — core language & APIs | Desktop apps, CLI tools, libraries (JDK, Collections, Streams) |
| **Java EE (Jakarta EE)** | Enterprise APIs on top of SE — web, services, transactions | Large web applications, REST, EJB, JPA, CDI                    |

**Note:** For this project use **Java SE** (JDK 17 or 21 recommended) unless you plan to add server-side features.

---

## 3) Java architecture: JDK, JRE, JVM

* **JVM (Java Virtual Machine)**: executes compiled bytecode (.class). Provides runtime services (garbage collection, JIT).
* **JRE (Java Runtime Environment)**: JVM + standard class libraries needed to run Java apps.
* **JDK (Java Development Kit)**: JRE + compilers (`javac`), tools (javadoc, jlink), and headers — required to build.

**Interaction:** `javac` (from JDK) compiles `.java` → `.class` bytecode → JVM (in JRE) executes bytecode.

---

## 4) Install & configure Java on Windows (steps)

1. Download JDK (choose LTS e.g., 17 or 21) from AdoptOpenJDK / Oracle / Temurin.
2. Run installer and note installation directory (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17`).
3. Set `JAVA_HOME` system variable to that path.
4. Add `%JAVA_HOME%\bin` to `Path` environment variable.
5. Verify in Command Prompt:

   * `java -version`
   * `javac -version`

> **Screenshot:** Add a screenshot of `java -version` output and of Environment Variables window.

---

## 5) Using Eclipse IDE: new project + run configs (short)

1. File → New → Java Project. Name: `student-info-system`.
2. Select JDK (Execution environment) or attach a JRE/JDK in Project settings.
3. Create packages: `edu.ccrm.cli`, `edu.ccrm.domain`, `edu.ccrm.service`, `edu.ccrm.io`, `edu.ccrm.config`, `edu.ccrm.util`.
4. Run configurations: Run As → Java Application. Create a configuration that specifies main class `edu.ccrm.cli.AppMain`.

> **Screenshot placeholders:** New Project dialog, Package Explorer with packages, Run Config dialog.

---

## 6) Project structure (suggested)

```
student-info-system/
├─ README.md
├─ build.gradle or pom.xml (optional)
├─ src/main/java/
│  └─ edu/ccrm/
│     ├─ cli/
│     ├─ domain/
│     ├─ service/
│     ├─ io/
│     ├─ util/
│     └─ config/
└─ out/ (or target/)
```

---

## 7) Coding requirements checklist

* [ ] Primitive types, operator examples & precedence comment
* [ ] Decision structures & switch menu
* [ ] Loops: while, do-while, for, enhanced for + break/continue
* [ ] Arrays + `java.util.Arrays` ops
* [ ] Strings and common APIs
* [ ] Encapsulation, Inheritance, Abstraction, Polymorphism
* [ ] Access modifiers usage
* [ ] Immutable value class
* [ ] Top-level, inner & static nested classes
* [ ] Interfaces (including default methods) and diamond problem demo
* [ ] Functional interfaces, lambdas, predicates
* [ ] Anonymous inner class use
* [ ] Enums with fields/constructors
* [ ] Upcast/downcast with `instanceof`
* [ ] Overriding & overloading
* [ ] Singleton & Builder patterns
* [ ] Exceptions (checked/unchecked), custom exceptions, assertions
* [ ] File I/O with NIO.2 + Streams pipelines
* [ ] Date/Time API usage

---

## 8) Key design decisions (short)

* Use **interfaces** for service contracts (e.g., `StudentService`) to allow multiple implementations (in-memory / file-based).
* Use **class inheritance** only for domain modelling (Person → Student/Instructor), because inheritance models *is-a*.
* Prefer composition for services (e.g., `TranscriptService` uses `EnrollmentService`).

---

## 9) Code snippets (templates)

### `edu.ccrm.cli.AppMain` (main class template)

```java
package edu.ccrm.cli;

public class AppMain {
    public static void main(String[] args) {
        System.out.println("Student Info System — App started");
        // Simple menu loop placeholder
        CLIApp app = new CLIApp();
        app.run();
    }
}
```

### Abstract `Person` and `Student` (OOP pillars)

```java
package edu.ccrm.domain;

import java.time.LocalDate;

public abstract class Person {
    protected final String id;
    protected String fullName;

    protected Person(String id, String fullName) {
        assert id != null : "id must not be null";
        this.id = id;
        this.fullName = fullName;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s[id=%s,name=%s]", getClass().getSimpleName(), id, fullName);
    }
}

// Student
package edu.ccrm.domain;

public class Student extends Person {
    private String regNo;

    public Student(String id, String fullName, String regNo) {
        super(id, fullName);
        this.regNo = regNo;
    }

    @Override
    public String getRole() { return "STUDENT"; }

    public String getRegNo() { return regNo; }
}
```

### Immutable value class example: `CourseCode`

```java
package edu.ccrm.domain;

public final class CourseCode {
    private final String code;

    public CourseCode(String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("code required");
        this.code = code.trim().toUpperCase();
    }

    public String getCode() { return code; }

    @Override
    public String toString() { return code; }
}
```

### Singleton: `AppConfig`

```java
package edu.ccrm.config;

import java.util.Properties;

public enum AppConfig {
    INSTANCE;
    private final Properties props = new Properties();
    AppConfig() { props.setProperty("app.name","StudentInfoSystem"); }
    public String get(String k){ return props.getProperty(k); }
}
```

### Builder pattern: `Course.Builder`

```java
package edu.ccrm.domain;

public class Course {
    private final CourseCode code;
    private final String title;
    private final int credits;

    private Course(Builder b){ this.code=b.code; this.title=b.title; this.credits=b.credits; }

    public static class Builder {
        private CourseCode code;
        private String title;
        private int credits;
        public Builder code(String c){ this.code=new CourseCode(c); return this; }
        public Builder title(String t){ this.title=t; return this; }
        public Builder credits(int c){ this.credits=c; return this; }
        public Course build(){ return new Course(this); }
    }
}
```

---

## 10) Exceptions & assertions

* Document Errors vs Exceptions: Errors are serious (OutOfMemoryError) — don't catch. Exceptions split into checked (e.g., `IOException`) and unchecked (`RuntimeException`).
* Create custom exceptions e.g., `DuplicateEnrollmentException extends Exception`, `MaxCreditLimitExceededException extends RuntimeException`.
* Example try/catch:

```java
try {
  enrollmentService.enroll(student, course);
} catch (DuplicateEnrollmentException | IllegalArgumentException ex) {
  System.err.println("Enroll failed: " + ex.getMessage());
} finally {
  // cleanup
}
```

* **Assertions:** Add `assert student.getId() != null;` where invariants are expected. Note: run `java -ea` to enable assertions.

---

## 11) File I/O (NIO.2) & Streams example

```java
// read student CSV and compute GPA distribution
Path p = Paths.get("data/students.csv");
try (Stream<String> lines = Files.lines(p)) {
    Map<Integer, Long> dist = lines
       .skip(1)
       .map(Student::fromCsv)
       .map(Student::getGpaBucket)
       .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
}
```

Also demonstrate `Files.copy`, `Files.move`, `Files.deleteIfExists`.

---

## 12) Date/Time API

* Use `java.time.LocalDate`, `Instant`, and `DateTimeFormatter`.
* Example: `String backupName = "backup-" + LocalDate.now().format(DateTimeFormatter.ISO_DATE);`

---

## 13) How to compile and run

* Command line compile (from project root):

```
javac -d out $(find src -name "*.java")
java -cp out edu.ccrm.cli.AppMain
```

* Enable assertions: `java -ea -cp out edu.ccrm.cli.AppMain`

---

*END OF README*
