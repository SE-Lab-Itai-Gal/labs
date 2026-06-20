package il.cshaifasweng.coursemgmt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A student in the university. A student can be enrolled in many courses, and a
 * course can hold many students, so this is a many-to-many relationship. This
 * side owns the join table ({@code student_course}).
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int studentNumber;
    private String firstName;
    private String lastName;
    private String email;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    public Student() {
    }

    public Student(int studentNumber, String firstName, String lastName, String email) {
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /** Registers this student to a course, keeping both sides of the relation in sync. */
    public void enroll(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    public int getId() {
        return id;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<Course> getCourses() {
        return courses;
    }
}
