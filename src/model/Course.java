package model;

import java.util.ArrayList;
import java.util.List;

//App consists of multiple courses, each with a tag and multiple modules
public class Course {
    private int courseId;
    private String courseTitle;
    private String courseDescription;
    List<Lesson> courseLessons = new ArrayList<>();
    private Quiz finalQuiz;
    private Tag courseTag;
    private float courseRating;
    private int numberOfRatings;
    private int completions;
    private float suggestRating;

    public Course(int courseId, String courseTitle, String courseDescription, Quiz finalQuiz, Tag courseTag) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.finalQuiz = finalQuiz;
        this.courseTag = courseTag;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public List<Lesson> getCourseLessons() {
        return courseLessons;
    }

    public Quiz getFinalQuiz() {
        return finalQuiz;
    }

    public void setFinalQuiz(Quiz finalQuiz) {
        this.finalQuiz = finalQuiz;
    }

    public Tag getCourseTag() {
        return courseTag;
    }

    public void setCourseTag(Tag courseTag) {
        this.courseTag = courseTag;
    }

    public float getCourseRating() {
        return courseRating;
    }

    public int getCompletions() {
        return completions;
    }

    public void courseCompletion() {
        completions++;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public float getSuggestRating() {
        return suggestRating;
    }

    public void setSuggestRating(float suggestRating) {
        this.suggestRating = suggestRating;
    }

    public void addLesson(Lesson l){
        courseLessons.add(l.copyLesson());
    }

    public void rateCourse(int rate){
        if(rate >= 1 && rate <= 10) {
            courseRating *= numberOfRatings;
            numberOfRatings += 1;
            courseRating += rate;
            courseRating /= numberOfRatings;
        }
    }

    @Override
    public String toString() {
        return courseId + ": " + courseTitle;
    }
}
