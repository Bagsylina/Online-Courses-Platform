package repository;

import model.Course;
import model.Lesson;
import model.Tag;
import model.User;

import java.sql.SQLException;
import java.util.List;

public interface RepositoryInterface {

    //Adds a course to the database
    boolean addNewCourse(Course course) throws SQLException;

    //Adds a user to the database
    boolean addNewUser(User user) throws SQLException;

    //Adds a lesson to the database
    boolean addNewLesson(Lesson lesson, int courseId) throws SQLException;

    //Adds an enrolled course to the database
    boolean addNewEnrolledCourse(int courseId, String username) throws SQLException;

    //Rating a course
    boolean setRating(int courseId, String username, int rating) throws SQLException;

    //Setting the score on a course
    boolean setScore(int courseId, String username, int score) throws SQLException;

    //Get all lessons from courses with a certain tag
    List<String> getLessonsByTag(Tag tag) throws SQLException;

    //Get all courses for all users
    List<UserCourses> getUserCourses() throws SQLException;

    //Deletes all rows from all tables in database
    void deleteAllData() throws SQLException;
}
