package model;

//Base class for all types of lessons
public class Lesson {
    protected int lessonId;
    protected String lessonTitle;
    protected String lessonDescription;

    //constructor
    public Lesson(int lessonId, String lessonTitle, String lessonDescription) {
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.lessonDescription = lessonDescription;
    }

    public Lesson(Lesson lesson) {
        this.lessonId = lesson.lessonId;
        this.lessonTitle = lesson.lessonTitle;
        this.lessonDescription = lesson.lessonDescription;
    }

    //getters and setters
    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getLessonDescription() {
        return lessonDescription;
    }

    public void setLessonDescription(String lessonDescription) {
        this.lessonDescription = lessonDescription;
    }

    //lesson class and subclasses functions

    //function that represents a user taking a lesson, and at the end returning a score, representing how well he did
    public int takeLesson() {
        System.out.println(lessonTitle);
        System.out.println(lessonDescription);
        return 100;
    }

    public Lesson copyLesson() {
        return new Lesson(this);
    }

    @Override
    public String toString() {
        return lessonTitle;
    }
}
