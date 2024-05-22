package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public EnrolledCourse(Course course)
    {
        this.course = course;
        for(Lesson lesson: course.getCourseLessons()) {
            lessonScores.add(0);
            if(lesson instanceof Quiz)
                quizNumber += 1;
            else if(lesson instanceof Task)
                taskNumber += 1;
            else if(lesson instanceof TextLesson || lesson instanceof VideoLesson)
                lessonNumber += 1;
        }
        courseScore = 0;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setRating(int rating) {
        if(this.rating == 0 && rating >= 1 && rating <= 10) {
            this.rating = rating;
            course.rateCourse(rating);
        }
        else
            System.out.println("Invalid rating or course already rated");
    }

    public int getCourseScore() {
        return courseScore;
    }

    public void updateCourseScore() {
        if(course.getCourseLessons().isEmpty()) {
            courseScore = finalQuizScore;
            return;
        }
        float quizScore = 0, taskScore = 0, lessonScore = 0;
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
        if(courseScore >= 80 && !isCompleted) {
            isCompleted = true;
            course.courseCompletion();
        }
    }

    public void takeCourse() {
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
        updateCourseScore();
    }

    public void setLessonScore(int id, int score) {
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
        lessonScores.set(id, score);
        this.updateCourseScore();
    }
}
