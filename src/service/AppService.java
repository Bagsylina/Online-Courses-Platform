package service;

import repository.CourseUserRepository;
import repository.UserCourses;
import model.*;
import exceptions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;

public class AppService implements AppServiceInterface {
    private final Map<String, User> userList = new HashMap<>();
    private final List<Course> courseList = new ArrayList<>();
    private User loggedUser = null;
    private final StringBuilder AUDIT_REPORT = new StringBuilder();
    private final StringBuilder USER_REPORT = new StringBuilder();
    private final StringBuilder COURSE_REPORT = new StringBuilder();
    CourseUserRepository repository = CourseUserRepository.getInstance();

    //Class is SINGLETON, only one object of this type available
    private static final class SINGLETON {
        private static final AppService INSTANCE = new AppService();
    }

    //Generates all headers for the 3 types of reports
    private AppService() {
        USER_REPORT.append("USERNAME,").append("FIRST NAME,").append("LAST NAME,").append("EMAIL,").append("\n");
        COURSE_REPORT.append("COURSE ID,").append("COURSE TITLE,").append("COURSE DESCRIPTION,").append("COURSE TAG,").append("\n");
        AUDIT_REPORT.append("ACTION,").append("USERNAME,").append("COURSE ID,").append("\n");
    }

    public static AppService getInstance() {
        return SINGLETON.INSTANCE;
    }

    //Adds a user in user dictionary and database
    @Override
    public void addUser(User user) {
        try {
            userList.put(user.getUsername(), user);
            repository.addNewUser(user);
            AUDIT_REPORT.append("Added a new user,").append(user.getUsername()).append("\n");
            USER_REPORT.append(user.getUsername()).append(",").append(user.getFirstName()).append(",").append(user.getLastName()).append(",")
                    .append(user.getEmail()).append("\n");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed SQL Query\n");
        }
    }

    //Adds a course in course list and database
    @Override
    public void addCourse(Course course) {
        try {
            courseList.add(course);
            repository.addNewCourse(course);
            for(Lesson lesson: course.getCourseLessons())
                repository.addNewLesson(lesson, course.getCourseId());
            AUDIT_REPORT.append("Added a new course,,").append(course.getCourseId()).append("\n");
            COURSE_REPORT.append(course.getCourseId()).append(",").append(course.getCourseTitle()).append(",").append(course.getCourseDescription())
                    .append(",").append(course.getCourseTag()).append("\n");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed SQL Query\n");
        }
    }

    //Text form for creating a new user
    //Needs to complete all credentials
    //Tag is selected if the answer is yes
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
        try {
            repository.addNewUser(user);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed SQL Query\n");
        }
    }

    //Login in current session
    //Checks if password is correct
    @Override
    public void login(String username, String password) {
        User user = userList.get(username);
        try {
            if (user != null) {
                if (user.verifyCredentials(password)) {
                    loggedUser = user;
                    AUDIT_REPORT.append("Logged in,").append(username).append("\n");
                }
                else
                    throw new FailedLogin("Wrong password");
            }
            else
                throw new FailedLogin("User not found");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed to login,").append(username).append("\n");
        }
    }

    //Suggest courses for a logged on user based on his preferred tags and popularity of course
    //Sorts them and returns top 10
    @Override
    public List<Course> suggestCourses() {
        try {
            if (loggedUser == null)
                throw new NotLoggedIn("Not logged in");
            List<Course> userCourses = loggedUser.getCourses();
            List<Course> suggestableCourses = courseList.stream()
                    .filter(course -> !userCourses.contains(course)).collect(Collectors.toList());
            suggestableCourses.forEach(course -> {
                if (loggedUser.getFavoritedTags().contains(course.getCourseTag()))
                    course.setSuggestRating(((5 + (float) course.getCompletions() / 1000) * (course.getCourseRating() * course.getCourseRating())));
                else
                    course.setSuggestRating((((float) course.getCompletions() / 1000) * (course.getCourseRating() * course.getCourseRating())));
            });
            Comparator<Course> comparator = new Comparator<>() {
                @Override
                public int compare(final Course course1, final Course course2) {
                    return Float.compare(course2.getSuggestRating(), course1.getSuggestRating());
                }
            };
            suggestableCourses.sort(comparator);
            AUDIT_REPORT.append("Got recommended courses,").append(loggedUser.getUsername()).append("\n");
            return suggestableCourses.stream().limit(10).collect(Collectors.toList());
        }
        catch (NotLoggedIn e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Not logged in\n");
            return null;
        }
    }

    //Shows top 10 users with best scores
    //Score is sum of squared course scores doubled if completed
    @Override
    public List<User> topUsers() {
        List<User> classableUsers = userList.values().stream()
                .filter(user -> user.calculateScore() > 0).collect(Collectors.toList());
        Comparator<User> comparator = new Comparator<>() {
            @Override
            public int compare(final User user1, final User user2) {
                return Float.compare(user2.calculateScore(), user1.calculateScore());
            }
        };
        classableUsers.sort(comparator);
        AUDIT_REPORT.append("Got top users\n");
        return classableUsers.stream().limit(5).collect(Collectors.toList());
    }

    //Logged user enrolls in a certain course
    //Checks if user is logged and if not already enrolled in course
    @Override
    public void enrollInCourse(int courseId) {
        try {
            if(loggedUser == null)
                throw new NotLoggedIn("Not logged in");
            Course course = getCourse(courseId);
            if (loggedUser.getCourses().contains(course))
                throw new FailedEnroll("User already enrolled");
            loggedUser.addCourse(course);
            repository.addNewEnrolledCourse(course.getCourseId(), loggedUser.getUsername());
        }
        catch(NotLoggedIn e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Not logged in\n");
        }
        catch(FailedEnroll e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed enroll\n");
        }
        catch(InvalidLessonType e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Lesson with invalid type\n");
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed SQL");
        }
    }

    //Gets a course by its id
    @Override
    public Course getCourse(int courseId) {
        try {
            for (Course course : courseList) {
                if (course.getCourseId() == courseId) {
                    AUDIT_REPORT.append("Got course,,").append(courseId).append("\n");
                    return course;
                }
            }
            throw new CourseNotFound("Course not found");
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Course not found");
            return null;
        }
    }

    //Gets a user by its username
    @Override
    public User getUser(String username) {
        try {
            User user = userList.get(username);
            if(user == null)
                throw new UserNotFound("User not found");
            AUDIT_REPORT.append("Got user,").append(username).append("\n");
            return userList.get(username);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("User not found");
            return null;
        }
    }

    //Gets a user profile by its username
    @Override
    public void displayUserProfile(String username) {
        try {
            User user = getUser(username);
            if (user == null)
                throw new UserNotFound("User not found");
            user.userProfile();
            AUDIT_REPORT.append("Displayed user profile,").append(username).append("\n");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("User not found,").append(username).append("\n");
        }
    }

    //Searches for courses with a certain tag
    //Sorts them by popularity
    @Override
    public List<Course> searchTag(Tag tag) {
        List<Course> tagCourses = courseList.stream()
                .filter(course -> course.getCourseTag() == tag).collect(Collectors.toList());
        Comparator<Course> comparator = new Comparator<>() {
            @Override
            public int compare(final Course course1, final Course course2) {
                return Float.compare(course2.getSuggestRating(), course1.getSuggestRating());
            }
        };
        tagCourses.sort(comparator);
        AUDIT_REPORT.append("Searched courses with tag,,").append(tag).append("\n");
        return tagCourses;
    }

    //Logged user takes a certain enrolled course, identified by id
    //Checks if user is enrolled in that course
    @Override
    public void takeCourse(int courseId) {
        try {
            List<EnrolledCourse> enrolledCourses = loggedUser.getEnrolledCourses();
            for (EnrolledCourse course : enrolledCourses) {
                if (course.getCourse().getCourseId() == courseId) {
                    course.takeCourse();
                    AUDIT_REPORT.append("Took course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
                    repository.setScore(course.getCourse().getCourseId(), loggedUser.getUsername(), course.getCourseScore());
                    return;
                }
            }
            throw new FailedEnroll("User not enrolled in course");

        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed SQL Query\n");
        }
        catch(FailedEnroll e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Tried to take an unenrolled course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed to take course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
        }
    }

    //Rates a course
    @Override
    public void rateCourse(int courseId, int rating) {
        try {
            List<EnrolledCourse> enrolledCourses = loggedUser.getEnrolledCourses();
            for (EnrolledCourse course : enrolledCourses) {
                if (course.getCourse().getCourseId() == courseId) {
                    course.setRating(rating);
                    repository.setRating(courseId, loggedUser.getUsername(), rating);
                    AUDIT_REPORT.append("Rated course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
                    return;
                }
            }
            throw new FailedEnroll("User not enrolled in course");
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed SQL Query\n");
        }
        catch(FailedEnroll e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Tried to rate an unenrolled course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            AUDIT_REPORT.append("Failed to rate course,").append(loggedUser.getUsername()).append(",").append(courseId).append("\n");
        }
    }

    //Generates a csv file that contains 3 reports:
    //All users and some of their data
    //All courses and some of their data
    //Logs with actions taken in the app
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

    //Uses SQL Query to get all lessons from courses with a certain tag
    public List<String> getLessonsByTag(Tag tag){
        try {
            List<String> lessons = repository.getLessonsByTag(tag);
            AUDIT_REPORT.append("Searched lessons with tag,,").append(tag.name()).append("\n");
            return lessons;
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //Uses SQL Query to get all enrolled courses for all users
    public List<UserCourses> getUserCourses() {
        try {
            return repository.getUserCourses();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //Deletes all entries in the database
    public void clearDatabase() {
        try {
            repository.deleteAllData();
        }
        catch (Exception e) {
            System.out.println("Delete failed");
        }
    }
}
