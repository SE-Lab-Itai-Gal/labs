# Lab 8 — University Course Management (Hibernate / ORM)

A small university course-management system stored in a **MySQL** database through
the **Hibernate** ORM (JPA annotations). The program creates the schema from the
entity classes, fills it with sample data, and prints the four required reports.

## Domain model

| Entity | Fields | Relations |
| --- | --- | --- |
| `Student` | id, studentNumber, firstName, lastName, email | many-to-many with `Course` (owns the `student_course` join table) |
| `Lecturer` | id, firstName, lastName, specialization, email | one-to-many to `Course` |
| `Course` | id, name, code, credits | many-to-one to `Lecturer`; many-to-many with `Student`; one-to-many to `ClassSession` |
| `ClassSession` | id, dateTime (`LocalDateTime`), durationMinutes, classroom | many-to-one to `Course` |

`ClassSession` is the *Class* (lesson) entity — it is named `ClassSession` because
`Class` is a reserved word in Java; its table is called `classes`.

### Relations (as required)

* A **student** can take many **courses**, and a course can hold many students →
  **many-to-many**, mapped with a `@JoinTable` (`student_course`).
* A **course** is led by exactly one **lecturer** → **many-to-one** (and the
  lecturer side is a `@OneToMany`, used to count how many courses each lecturer leads).
* A **class** belongs to exactly one **course** → **many-to-one**.

Hibernate creates all the tables and the foreign/join keys automatically from these
annotations (`hibernate.hbm2ddl.auto=create`).

## Sample data

3 lecturers, 4 courses, 8 students and 12 classes (3 per course). Every student is
enrolled in 2 courses, and every course has 3 classes — satisfying the
"at least 2" requirements.

## Reports printed

1. All courses with every field, including the responsible lecturer's name.
2. All students with every field, including the list of courses they are enrolled in.
3. The classes of each course, with date, time, duration and location.
4. All lecturers with every field, including how many courses each one is responsible for.

## Database configuration

Settings live in `src/main/resources/hibernate.properties`:

* The database is named **`myFirstDataBase`** and is created automatically if it does
  not exist (`createDatabaseIfNotExist=true` in the JDBC URL).
* **Updating the password** — every user can connect to their own MySQL **without
  editing any file**. Pass the username/password on the command line, or just run the
  jar and type the password when prompted (Enter keeps the default):

  ```bash
  java -jar coursemgmt.jar                 # asks for the password on the console
  java -jar coursemgmt.jar <user> <pass>   # or pass them as arguments
  ```

## Building

Built with a JDK 17+ (tested on JDK 21). Hibernate is kept on the stable **5.x**
line (`javax.persistence`), as taught in the lab.

```bash
cd lab8/coursemgmt
mvn clean package            # produces a runnable fat jar
```

This creates `target/coursemgmt.jar` (also copied to `dist/coursemgmt.jar`), a single
jar that bundles Hibernate and the MySQL driver and includes the source files.

## Running

```bash
# Make sure a MySQL server is running on localhost:3306, then:
java -jar dist/coursemgmt.jar
```

> On the **first** run against an empty database you may see a few red
> `drop table` / `alter table ... drop foreign key` messages — these are harmless
> (Hibernate tries to drop tables before creating them, and there is nothing to drop
> yet). The reports are printed right after.

## Running from the IDE (IntelliJ / Maven)

Maven goal `clean package exec:java` with working directory `coursemgmt`.
