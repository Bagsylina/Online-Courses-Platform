package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.nio.file.StandardOpenOption.APPEND;

public class AppService implements AppServiceInterface{
    private Map<String, User> userList = new HashMap<>();
    private List<Course> courseList = new ArrayList<>();
    private User loggedUser = null;
    private final StringBuilder AUDIT_REPORT = new StringBuilder();
    private final StringBuilder USER_REPORT = new StringBuilder();
    private final StringBuilder COURSE_REPORT = new StringBuilder();

    private static final class SINGLETON {
        private static AppService INSTANCE = new AppService();
    }

    private AppService() {
        USER_REPORT.append("USERNAME,").append("FIRST NAME,").append("LAST NAME,").append("EMAIL,").append("\n");
        COURSE_REPORT.append("COURSE ID,").append("COURSE TITLE,").append("COURSE DESCRIPTION,").append("COURSE TAG,").append("\n");
        AUDIT_REPORT.append("ACTION,").append("USERNAME,").append("COURSE ID,").append("\n");
    }

    public static AppService getInstance() {
        return SINGLETON.INSTANCE;
    }

    @Override
    public void addUser(User user) {
        userList.put(user.getUsername(), user);
        AUDIT_REPORT.append("Added a new user,").append(user.getUsername()).append("\n");
        USER_REPORT.append(user.getUsername()).append(",").append(user.getFirstName()).append(",").append(user.getLastName()).append(",")
                .append(user.getEmail()).append("\n");
    }

    @Override
    public void addCourse(Course course) {
        courseList.add(course);
        AUDIT_REPORT.append("Added a new course,,").append(course.getCourseId()).append("\n");
        COURSE_REPORT.append(course.getCourseId()).append(",").append(course.getCourseTitle()).append(",").append(course.getCourseDescription())
                .append(",").append(course.getCourseTag()).append("\n");
    }

    @Override
    public void createUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username: ");
        String username = scanner.nextLine();
        System.out.println("First name: ");
        String firstName = scanner.nextLine();
        System.out.println("Last name: ");
        String lastName = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();

        List<Tag> tags = new ArrayList<>();
        for(Tag tag: Tag.values()) {
            System.out.println(tag);
            String answer = scanner.nextLine();
            if(answer.compareTo("yes") == 0)
                tags.add(tag);
        }

        User user = new User(username, firstName, lastName, email, password, tags);
        userList.put(user.getUsername(), user);
        loggedUser = user;
        AUDIT_REPORT.append("Created a new user,").append(user.getUsername()).append("\n");
        USER_REPORT.append(user.getUsername()).append(",").append(user.getFirstName()).append(",").append(user.getLastName()).append(",")
                .append(user.getEmail()).append("\n");
    }

    @Override
    public void login(String username, String password) {
        User user = userList.get(username);
        if(user != null) {
            if(user.verifyCredentials(password)) {
                loggedUser = user;
                AUDIT_REPORT.append("Logged in,").append(username).append("\n");
            }
            else {
                System.out.println("Wrong password!");
                AUDIT_REPORT.append("Failed to login,").append(username).append("\n");
            }
        }
        else {
            System.out.println("User doesn't exist!");
            AUDIT_REPORT.append("Invalid login,").append(username).append("\n");
        }
    }

    @Override
    public List<Course> suggestCourses() {
        if(loggedUser == null) {
            System.out.println("Not logged in!");
            AUDIT_REPORT.append("Not logged in when getting recommended courses\n");
            return null;
        }
        List<Course> userCourses = loggedUser.getCourses();
        List<Course> suggestableCourses = courseList.stream()
                .filter(course -> !userCourses.contains(course)).collect(Collectors.toList());
        suggestableCourses.forEach(course -> {
                    if(loggedUser.getFavoritedTags().contains(course.getCourseTag()))
                        course.setSuggestRating(((5 + (float) course.getCompletions() / 1000) * (course.getCourseRating() * course.getCourseRating())));
                    else
                        course.setSuggestRating((((float) course.getCompletions() / 1000) * (course.getCourseRating() * course.getCourseRating())));
                });
        Comparator<Course> comparator = new Comparator<Course>() {
            @Override
            public int compare(final Course course1, final Course course2) {
                return Float.compare(course2.getSuggestRating(), course1.getSuggestRating());
            }
        };
        suggestableCourses.sort(comparator);
        AUDIT_REPORT.append("Got recommended courses,").append(loggedUser.getUsername()).append("\n");
        return suggestableCourses.stream().limit(10).collect(Collectors.toList());
    }

    @Override
    public List<User> topUsers() {
        List<User> classableUsers = userList.values().stream()
                .filter(user -> user.calculateScore() > 0).collect(Collectors.toList());
        Comparator<User> comparator = new Comparator<User>() {
            @Override
            public int compare(final User user1, final User user2) {
                return Float.compare(user2.calculateScore(), user1.calculateScore());
            }
        };
        classableUsers.sort(comparator);
        AUDIT_REPORT.append("Got top users\n");
        return classableUsers.stream().limit(5).collect(Collectors.toList());
    }

    //TO DO: eroare pentru nelogare
    @Override
    public void enrollInCourse(int courseId) {
        Course course = getCourse(courseId);
        if(loggedUser.getCourses().contains(course)) {
            System.out.println("User already enrolled in this course.");
            AUDIT_REPORT.append("Tried to enroll in already enrolled course,").append(loggedUser.getUsername()).append(",").append(course.getCourseId()).append("\n");
        }
        else {
            loggedUser.addCourse(course);
            AUDIT_REPORT.append("User enrolled in course,").append(loggedUser.getUsername()).append(",").append(course.getCourseId()).append("\n");
        }
    }

    @Override
    public Course getCourse(int courseId) {
        for(Course course: courseList) {
            if(course.getCourseId() == courseId) {
                AUDIT_REPORT.append("Got course,,").append(courseId).append("\n");
                return course;
            }
        }
        AUDIT_REPORT.append("Course not found,,").append(courseId).append("\n");
        return null;
    }

    @Override
    public User getUser(String username) {
        AUDIT_REPORT.append("Got user,").append(username).append("\n");
        return userList.get(username);
    }

    @Override
    public void displayUserProfile(String username) {
        User user = getUser(username);
        user.userProfile();
        AUDIT_REPORT.append("Displayed user profile,").append(username).append("\n");
    }

    @Override
    public List<Course> searchTag(Tag tag) {
        List<Course> tagCourses = courseList.stream()
                .filter(course -> course.getCourseTag() == tag).collect(Collectors.toList());
        Comparator<Course> comparator = new Comparator<Course>() {
            @Override
            public int compare(final Course course1, final Course course2) {
                return Float.compare(course2.getSuggestRating(), course1.getSuggestRating());
            }
        };
        tagCourses.sort(comparator);
        AUDIT_REPORT.append("Searched courses with tag,,").append(tag).append("\n");
        return tagCourses;
    }

    @Override
    public void takeCourse(int courseId) {
        List<EnrolledCourse> enrolledCourses = loggedUser.getEnrolledCourses();
        for(EnrolledCourse course: enrolledCourses) {
            if(course.getCourse().getCourseId() == courseId) {
                course.takeCourse();
                AUDIT_REPORT.append("Took course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
                return;
            }
        }
        AUDIT_REPORT.append("Tried to take in unenrolled course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
        System.out.println("Not enrolled in course.");
    }

    @Override
    public void rateCourse(int courseId, int rating) {
        List<EnrolledCourse> enrolledCourses = loggedUser.getEnrolledCourses();
        for(EnrolledCourse course: enrolledCourses) {
            if(course.getCourse().getCourseId() == courseId) {
                course.setRating(rating);
                AUDIT_REPORT.append("Rated course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
                return;
            }
        }
        AUDIT_REPORT.append("Tried to rate in unenrolled course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
        System.out.println("Not enrolled in course.");
    }

    @Override
    public void generateAudit() {
        try {
            Path reportsPath = Paths.get("src/audit/"
                    + "Audit_" + Instant.now().getNano() + ".csv");
            Files.createFile(reportsPath);
            Files.write(reportsPath, USER_REPORT.toString().getBytes(), APPEND);
            Files.write(reportsPath, "\n".getBytes(), APPEND);
            Files.write(reportsPath, COURSE_REPORT.toString().getBytes(), APPEND);
            Files.write(reportsPath, "\n".getBytes(), APPEND);
            Files.write(reportsPath, AUDIT_REPORT.toString().getBytes(), APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
