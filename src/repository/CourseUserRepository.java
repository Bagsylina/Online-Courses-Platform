package repository;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseUserRepository implements RepositoryInterface {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/courseuserdatabase";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "root";

    private static final String ADD_COURSE_SQL = "INSERT INTO COURSE(id, title, description, tag) values (?, ?, ?, ?)";
    private static final String ADD_USER_SQL = "INSERT INTO USER(username, firstname, lastname, email) values (?, ?, ?, ?)";
    private static final String ADD_LESSON_SQL = "INSERT INTO LESSON(id, courseid, title, description) values (?, ?, ?, ?)";
    private static final String ADD_ENROLLEDCOURSE_SQL = "INSERT INTO ENROLLEDCOUSE(courseid, username, score, rating) values (?, ?, 0, 0)";

    private static final String SET_RATING = "UPDATE ENROLLEDCOURSE SET rating = ? WHERE courseid = ? AND username = ?";
    private static final String SET_SCORE = "UPDATE ENROLLEDCOURSE SET score = ? WHERE courseid = ? AND username = ?";

    private static final String LESSONS_BY_TAG = "SELECT l.title FROM LESSON l JOIN COURSE c ON (l.courseid = c.id) WHERE UPPER(c.tag) LIKE UPPER(?)";
    private static final String USER_COURSES = "SELECT u.firstname, u.lastname, c.title, c.description, c.tag FROM COURSE c JOIN ENROLLEDCOURSE e ON (c.id = e.courseid) " +
            "JOIN USER u ON (u.username = e.username);";

    //Class is SINGLETON, only one object of this type available
    private static final class SINGLETON {
        private static final CourseUserRepository INSTANCE = new CourseUserRepository();
    }

    private CourseUserRepository() {}

    public static CourseUserRepository getInstance() {
        return SINGLETON.INSTANCE;
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    public boolean addNewCourse(Course course) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(ADD_COURSE_SQL);
        preparedStatement.setInt(1, course.getCourseId());
        preparedStatement.setString(2, course.getCourseTitle());
        preparedStatement.setString(3, course.getCourseDescription());
        preparedStatement.setString(4, course.getCourseTag().name());

        return preparedStatement.execute();
    }

    public boolean addNewUser(User user) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(ADD_USER_SQL);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getFirstName());
        preparedStatement.setString(3, user.getLastName());
        preparedStatement.setString(4, user.getEmail());

        return preparedStatement.execute();
    }

    public boolean addNewLesson(Lesson lesson, int courseId) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(ADD_LESSON_SQL);
        preparedStatement.setInt(1, lesson.getLessonId());
        preparedStatement.setInt(2, courseId);
        preparedStatement.setString(3, lesson.getLessonTitle());
        preparedStatement.setString(4, lesson.getLessonDescription());

        return preparedStatement.execute();
    }

    public boolean addNewEnrolledCourse(int courseId, String username) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(ADD_ENROLLEDCOURSE_SQL);
        preparedStatement.setInt(1, courseId);
        preparedStatement.setString(2, username);

        return preparedStatement.execute();
    }

    public boolean setRating(int courseId, String username, int rating) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(SET_RATING);
        preparedStatement.setInt(1, rating);
        preparedStatement.setInt(2, courseId);
        preparedStatement.setString(3, username);

        return preparedStatement.execute();
    }

    public boolean setScore(int courseId, String username, int score) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(SET_SCORE);
        preparedStatement.setInt(1, score);
        preparedStatement.setInt(2, courseId);
        preparedStatement.setString(3, username);

        return preparedStatement.execute();
    }

    public List<String> getLessonsByTag(Tag tag) throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(LESSONS_BY_TAG);
        preparedStatement.setString(1, tag.name());

        List<String> lessons = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            lessons.add(resultSet.getString("title"));
        }

        return lessons;
    }

    public List<UserCourses> getUserCourses() throws SQLException{
        PreparedStatement preparedStatement = getConnection().prepareStatement(USER_COURSES);

        List <UserCourses> userCourses = new ArrayList<>();
        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()) {
            userCourses.add(new UserCourses(resultSet.getString("firstname"), resultSet.getString("lastname"), resultSet.getString("title"),
                    resultSet.getString("description"), resultSet.getString("tag")));
        }

        return userCourses;
    }

    public void deleteAllData() throws SQLException {
        PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM COURSE");
        preparedStatement.execute();

        preparedStatement = getConnection().prepareStatement("DELETE FROM USER");
        preparedStatement.execute();

        preparedStatement = getConnection().prepareStatement("DELETE FROM ENROLLEDCOURSE");
        preparedStatement.execute();

        preparedStatement = getConnection().prepareStatement("DELETE FROM LESSON");
        preparedStatement.execute();
    }
}
