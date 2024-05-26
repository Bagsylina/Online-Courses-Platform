package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.*;

//Class for a course that represents a course taken by a user and his progress
public class EnrolledCourse {
    private final Course course;
    private int rating = 0;
    private List<Integer> lessonScores = new ArrayList<>();
    private int quizNumber, taskNumber, lessonNumber;
    private int quizSum, taskSum, lessonSum;
    private int finalQuizScore;
    private int courseScore;
    private boolean isCompleted;

    public EnrolledCourse(Course course) throws InvalidLessonType {
        this.course = course;
        //checks for every lesson if lesson type is valid
        //counts every type of lesson (to be used for grading)
        for(Lesson lesson: course.getCourseLessons()) {
            lessonScores.add(0);
            if(lesson instanceof Quiz)
                quizNumber += 1;
            else if(lesson instanceof Task)
                taskNumber += 1;
            else if(lesson instanceof TextLesson || lesson instanceof VideoLesson)
                lessonNumber += 1;
            else {
                throw new InvalidLessonType("Invalid lesson type");
            }
        }
        courseScore = 0;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    //Sets a rating that a specific user left for a course
    //Cannot rate multiple times, rating must be between 1 and 10
    public void setRating(int rating) throws FailedRating{
        if(this.rating == 0 && rating >= 1 && rating <= 10) {
            this.rating = rating;
            course.rateCourse(rating);
        }
        else if(this.rating != 0) {
            throw new FailedRating("Course already rated");
        }
        else {
            throw new FailedRating("Invalid rating");
        }
    }

    public int getCourseScore() {
        return courseScore;
    }

    //Calculates the score of the course
    //70% all lesson scores and 30% final quiz score
    //50% quiz scores, 30% task scores, 20% normal lessons for all lesson scores
    public void updateCourseScore() {
        if(course.getCourseLessons().isEmpty()) {
            this.courseScore = finalQuizScore;
            return;
        }
        float quizScore, taskScore, lessonScore;
        if(quizNumber == 0)
            quizScore = 100;
        else
            quizScore = (float) quizSum / quizNumber;
        if(taskNumber == 0)
            taskScore = 100;
        else
            taskScore = (float) taskSum / taskNumber;
        if(lessonNumber == 0)
            lessonScore = 100;
        else
            lessonScore = (float) lessonSum / lessonNumber;
        courseScore = (int) (quizScore / 2 + taskScore / 10 * 3 + lessonScore / 5) * 7 / 10 + finalQuizScore * 3 / 10;
        //if course score is over 80%, then it's completed
        if(courseScore >= 80 && !isCompleted) {
            isCompleted = true;
            course.courseCompletion();
        }
    }

    //Takes a score, presenting a menu with all lessons and final quiz as options
    public void takeCourse() throws InvalidOption, InvalidLessonType{
        System.out.println(course.getCourseTitle());
        System.out.println(course.getCourseDescription());
        System.out.println("0: Back");
        List<Lesson> courseLessons = course.getCourseLessons();
        courseLessons.forEach(lesson -> System.out.println((courseLessons.indexOf(lesson) + 1) + ": " + lesson));
        System.out.println((courseLessons.size() + 1) + ": Final Quiz");
        Scanner scanner = new Scanner(System.in);
        int chosen = scanner.nextInt();
        if(chosen > 0 && chosen <= courseLessons.size())
        {
            chosen--;
            Lesson lesson = courseLessons.get(chosen);
            setLessonScore(chosen, lesson.takeLesson());
        }
        else if(chosen == courseLessons.size() + 1)
            finalQuizScore = course.getFinalQuiz().takeLesson();
        else if(chosen != 0){
            throw new InvalidOption("Invalid selected lesson");
        }
        updateCourseScore();
    }

    //Sets a socre to a certain lesson, while also depending on its' type
    public void setLessonScore(int id, int score) throws InvalidLessonType {
        Lesson lesson = course.getCourseLessons().get(id);
        if(lesson instanceof Quiz) {
            quizSum -= lessonScores.get(id);
            quizSum += score;
        }
        else if(lesson instanceof Task) {
            taskSum -= lessonScores.get(id);
            taskSum += score;
        }
        else if(lesson instanceof TextLesson || lesson instanceof VideoLesson) {
            lessonSum -= lessonScores.get(id);
            lessonSum += score;
        }
        else {
            throw new InvalidLessonType("Invalid lesson type");
        }
        lessonScores.set(id, score);
        this.updateCourseScore();
    }
}
