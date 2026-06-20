package il.cshaifasweng.coursemgmt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A course. It is led by a single {@link Lecturer} (many courses to one lecturer),
 * holds many {@link Student}s (many-to-many), and is taught over several
 * {@link ClassSession}s (one course to many classes).
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int code;
    private int credits;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<ClassSession> classes = new HashSet<>();

    public Course() {
    }

    public Course(String name, int code, int credits, Lecturer lecturer) {
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.lecturer = lecturer;
        lecturer.getCourses().add(this);
    }

    /** Adds a class (lesson) to this course, keeping both sides of the relation in sync. */
    public void addClass(ClassSession classSession) {
        classes.add(classSession);
        classSession.setCourse(this);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public int getCredits() {
        return credits;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Set<ClassSession> getClasses() {
        return classes;
    }
}
