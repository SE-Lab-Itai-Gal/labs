package il.cshaifasweng.coursemgmt;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * A single class (lesson) of a course. Each class belongs to exactly one
 * {@link Course}. Named {@code ClassSession} because {@code Class} is reserved
 * in Java; the table itself is called {@code classes}.
 */
@Entity
@Table(name = "classes")
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "class_date")
    private LocalDateTime dateTime;

    /** Length of the class in minutes. */
    private int durationMinutes;

    private String classroom;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public ClassSession() {
    }

    public ClassSession(LocalDateTime dateTime, int durationMinutes, String classroom) {
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.classroom = classroom;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getClassroom() {
        return classroom;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
