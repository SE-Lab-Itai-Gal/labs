package il.cshaifasweng.coursemgmt;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * University course-management system on top of Hibernate (ORM).
 *
 * The program creates the schema, fills it with sample data (3 lecturers,
 * 4 courses, 8 students and 12 classes) and then prints the four required
 * reports. The MySQL user/password can be overridden at runtime so every grader
 * can connect to their own database without changing the code.
 */
public class App {

    private static Session session;

    private static SessionFactory getSessionFactory(String[] args) throws HibernateException {
        Configuration configuration = new Configuration();

        // Register every entity that Hibernate should map to a table.
        configuration.addAnnotatedClass(Student.class);
        configuration.addAnnotatedClass(Lecturer.class);
        configuration.addAnnotatedClass(Course.class);
        configuration.addAnnotatedClass(ClassSession.class);

        applyCredentials(configuration, args);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    /**
     * Lets each user point the program at their own MySQL without editing any file.
     * The username/password may be passed on the command line
     * ({@code java -jar coursemgmt.jar <username> <password>}); otherwise we ask for
     * the password on the console (Enter keeps the default from hibernate.properties).
     */
    private static void applyCredentials(Configuration configuration, String[] args) {
        String username = configuration.getProperty("hibernate.connection.username");
        String password = configuration.getProperty("hibernate.connection.password");

        if (args.length >= 1) {
            username = args[0];
        }
        if (args.length >= 2) {
            password = args[1];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("MySQL password for user '" + username + "' (Enter to keep the default): ");
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                password = line;
            }
        }

        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);
    }

    /** Builds and saves all the sample data, wiring up the relations between the entities. */
    private static void generateData() {
        // 3 lecturers
        Lecturer cohen = new Lecturer("David", "Cohen", "Algorithms", "cohen@univ.ac.il");
        Lecturer levi = new Lecturer("Sara", "Levi", "Databases", "levi@univ.ac.il");
        Lecturer mizrahi = new Lecturer("Noam", "Mizrahi", "Networks", "mizrahi@univ.ac.il");
        session.save(cohen);
        session.save(levi);
        session.save(mizrahi);

        // 4 courses, each led by one lecturer (Cohen leads two)
        Course algorithms = new Course("Algorithms", 101, 4, cohen);
        Course dataStructures = new Course("Data Structures", 102, 4, cohen);
        Course databases = new Course("Databases", 201, 3, levi);
        Course networks = new Course("Computer Networks", 301, 3, mizrahi);
        session.save(algorithms);
        session.save(dataStructures);
        session.save(databases);
        session.save(networks);

        // 8 students, each enrolled in at least 2 courses
        Course[] courses = {algorithms, dataStructures, databases, networks};
        String[][] names = {
                {"Itai", "Aviad"}, {"Gal", "Bareket"}, {"Maya", "Friedman"}, {"Omer", "Katz"},
                {"Tamar", "Shapira"}, {"Yossi", "Peretz"}, {"Dana", "Golan"}, {"Eyal", "Barak"}
        };
        for (int i = 0; i < names.length; i++) {
            Student student = new Student(20250 + i, names[i][0], names[i][1],
                    names[i][0].toLowerCase() + "@univ.ac.il");
            // Enroll each student in two different courses.
            student.enroll(courses[i % courses.length]);
            student.enroll(courses[(i + 1) % courses.length]);
            session.save(student);
        }

        // 12 classes, 3 per course (so every course has at least 2 classes)
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0);
        String[] rooms = {"Building A - 101", "Building A - 102", "Building B - 205", "Building C - 310"};
        for (int c = 0; c < courses.length; c++) {
            for (int week = 0; week < 3; week++) {
                ClassSession classSession = new ClassSession(
                        start.plusWeeks(week).plusDays(c), 90, rooms[c]);
                courses[c].addClass(classSession);
                session.save(classSession);
            }
        }
    }

    /** Report 1: every course with all its fields, including the responsible lecturer. */
    private static void printCourses() {
        List<Course> courses = session.createQuery("from Course", Course.class).getResultList();
        System.out.println("\n===== Courses =====");
        for (Course course : courses) {
            Lecturer lecturer = course.getLecturer();
            System.out.println("Course #" + course.getId()
                    + " | name: " + course.getName()
                    + " | code: " + course.getCode()
                    + " | credits: " + course.getCredits()
                    + " | lecturer: " + lecturer.getFirstName() + " " + lecturer.getLastName());
        }
    }

    /** Report 2: every student with all its fields, including the courses they are enrolled in. */
    private static void printStudents() {
        List<Student> students = session.createQuery("from Student", Student.class).getResultList();
        System.out.println("\n===== Students =====");
        for (Student student : students) {
            StringBuilder courses = new StringBuilder();
            for (Course course : student.getCourses()) {
                if (courses.length() > 0) {
                    courses.append(", ");
                }
                courses.append(course.getName());
            }
            System.out.println("Student #" + student.getId()
                    + " | number: " + student.getStudentNumber()
                    + " | name: " + student.getFirstName() + " " + student.getLastName()
                    + " | email: " + student.getEmail()
                    + " | courses: [" + courses + "]");
        }
    }

    /** Report 3: the list of classes of each course, with date, time and location. */
    private static void printClasses() {
        List<Course> courses = session.createQuery("from Course", Course.class).getResultList();
        System.out.println("\n===== Classes per course =====");
        for (Course course : courses) {
            System.out.println(course.getName() + ":");
            for (ClassSession classSession : course.getClasses()) {
                System.out.println("    date: " + classSession.getDateTime()
                        + " | duration: " + classSession.getDurationMinutes() + " min"
                        + " | room: " + classSession.getClassroom());
            }
        }
    }

    /** Report 4: every lecturer with all its fields, including how many courses they lead. */
    private static void printLecturers() {
        List<Lecturer> lecturers = session.createQuery("from Lecturer", Lecturer.class).getResultList();
        System.out.println("\n===== Lecturers =====");
        for (Lecturer lecturer : lecturers) {
            System.out.println("Lecturer #" + lecturer.getId()
                    + " | name: " + lecturer.getFirstName() + " " + lecturer.getLastName()
                    + " | specialization: " + lecturer.getSpecialization()
                    + " | email: " + lecturer.getEmail()
                    + " | courses responsible for: " + lecturer.getCourses().size());
        }
    }

    public static void main(String[] args) {
        SessionFactory sessionFactory = null;
        try {
            sessionFactory = getSessionFactory(args);
            session = sessionFactory.openSession();
            session.beginTransaction();

            generateData();
            session.flush();

            printCourses();
            printStudents();
            printClasses();
            printLecturers();

            session.getTransaction().commit();
        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
