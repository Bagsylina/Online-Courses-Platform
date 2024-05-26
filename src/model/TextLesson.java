package model;

import java.util.ArrayList;
import java.util.List;

//Lesson which consits of multiple strings that represent text pages
public class TextLesson extends Lesson {
    private List<String> textBody = new ArrayList<>();

    public TextLesson(int lessonId, String lessonTitle, String lessonDescription, List<String> textBody) {
        super(lessonId, lessonTitle, lessonDescription);
        this.textBody = textBody;
    }

    public TextLesson(TextLesson textLesson) {
        super(textLesson);
        this.textBody = textLesson.textBody;
    }

    //getters and setters
    public List<String> getTextBody() {
        return textBody;
    }

    public void setTextBody(List<String> textBody) {
        this.textBody = textBody;
    }

    //Array of strings, each representing a page
    @Override
    public int takeLesson() {
        System.out.println(lessonTitle);
        System.out.println(lessonDescription);
        for(int i = 0; i < textBody.size(); i++) {
            System.out.println("Page " + i);
            System.out.println(textBody.get(i));
        }
        return 100;
    }

    @Override
    public Lesson copyLesson() {
        return new TextLesson(this);
    }
}
