package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Lesson that consists of multiple questions
public class Quiz extends Lesson{
    private List<QuizQuestion> questions = new ArrayList<>();

    public Quiz(int lessonId, String lessonTitle, String lessonDescription, List<QuizQuestion> questions) {
        super(lessonId, lessonTitle, lessonDescription);
        this.questions = questions;
    }

    public Quiz(Quiz quiz) {
        super(quiz);
        this.questions = quiz.questions;
    }

    //getters and setters
    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    @Override
    public int takeLesson() {
        System.out.println(lessonTitle);
        System.out.println(lessonDescription);
        Scanner scanner = new Scanner(System.in);
        int grade = 0;
        for(QuizQuestion question : questions)
        {
            System.out.println(question.getQuestion());
            for(int i = 0; i < question.getOptions().length; i++)
                System.out.println(i + ": " + question.getOptions()[i]);
            int answer = scanner.nextInt();
            grade += question.answerQuestion(answer);
        }
        grade /= questions.size();
        grade *= 100;
        return grade;
    }

    public void addQuestion(QuizQuestion newQuestion) {
        this.questions.add(new QuizQuestion(newQuestion));
    }

    @Override
    public Lesson copyLesson() {
        return new Quiz(this);
    }
}
