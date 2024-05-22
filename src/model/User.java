package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Class for a normal website user, has it's contacts, tags marked as favorite for suggesting courses and enrolled courses and their status
public class User {
    private final String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Tag> favoritedTags;
    private List<EnrolledCourse> enrolledCourses = new ArrayList<>();

    public User(String username, String firstName, String lastName, String email, String password, List<Tag> favoritedTags) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.favoritedTags = favoritedTags;
    }

    //getters and setters
    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //used for enrolling in course
    public void addCourse(Course course) {
        enrolledCourses.add(new EnrolledCourse(course));
    }

    //used for login
    public boolean verifyCredentials(String password) {
        return Objects.equals(this.password, password);
    }

    public List<Tag> getFavoritedTags() {
        return favoritedTags;
    }

    public List<EnrolledCourse> getEnrolledCourses(){
        return enrolledCourses;
    }

    public List<Course> getCourses() {
        return enrolledCourses.stream().map(EnrolledCourse::getCourse).toList();
    }

    //displays all information about user, including the status of his courses
    public void userProfile() {
        System.out.println("Username: " + username);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Score: " + calculateScore());
    }

    //calculates a score based on his enrolled courses and their status
    public int calculateScore() {
        int score = 0;
        for(EnrolledCourse course: enrolledCourses) {
            score += (course.getCourseScore() / 20) * (course.getCourseScore() / 20) * (1 + (course.isCompleted() ? 1 : 0));
        }
        return score;
    }

    @Override
    public String toString() {
        return username + ": " + firstName + ' ' + lastName;
    }
}
