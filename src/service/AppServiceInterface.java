package service;

import model.Course;
import model.Tag;
import model.User;
import repository.UserCourses;

import java.util.List;

public interface AppServiceInterface {

    //Add a user or a course
    void addUser(User user);
    void addCourse(Course course);

    //Form for creating a user
    void createUser();

    //Login functionality
    void login(String username, String password);

    //Suggest courses based on the what the logged user prefers
    List<Course> suggestCourses();

    //List of top users based on their score
    List<User> topUsers();

    //Logged user enrolls in a brand new course
    void enrollInCourse(int courseId);

    //Get a course from the app
    Course getCourse(int courseId);

    //Get a user from the app
    User getUser(String username);

    //Display a certain users profile
    void displayUserProfile(String username);

    //Display courses with a certain tag, sorted from highest rating
    List<Course> searchTag(Tag tag);

    //Take a certain course that the user should be enrolled in
    void takeCourse(int courseId);

    //Logged user rates a certain course
    void rateCourse(int courseId, int rating);

    //Generate xls file with user and course data and actions taken
    void generateAudit();

    //Using sql to get all lessons from courses with a certain tag
    List<String> getLessonsByTag(Tag tag);

    //Using sql to get all courses for all users
    List<UserCourses> getUserCourses();

    //Deletes all rows from all tables in database
    void clearDatabase();
}
