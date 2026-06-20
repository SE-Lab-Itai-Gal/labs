package il.cshaifasweng.coursemgmt;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A lecturer. Each lecturer can be responsible for several courses (one-to-many),
 * while every course has exactly one responsible lecturer.
 */
@Entity
@Table(name = "lecturers")
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String specialization;
    private String email;

    @OneToMany(mappedBy = "lecturer")
    private Set<Course> courses = new HashSet<>();

    public Lecturer() {
    }

    public Lecturer(String firstName, String lastName, String specialization, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getEmail() {
        return email;
    }

    public Set<Course> getCourses() {
        return courses;
    }
}
