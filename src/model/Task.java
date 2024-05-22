package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Lesson that consints of multiple string that represent subtasks that need to be completed
public class Task extends Lesson{
    private List<String> subtasks = new ArrayList<>();

    public Task(int lessonId, String lessonTitle, String lessonDescription, List<String> subtasks) {
        super(lessonId, lessonTitle, lessonDescription);
        this.subtasks = subtasks;
    }

    public Task(Task task) {
        super(task);
        this.subtasks = task.subtasks;
    }

    @Override
    public int takeLesson() {
        System.out.println(lessonTitle);
        System.out.println(lessonDescription);
        Scanner scanner = new Scanner(System.in);
        int progress = 0;
        for(String subtask : subtasks) {
            System.out.println(subtask);
            System.out.println("1: Done");
            System.out.println("2: Not done");
            int answer = scanner.nextInt();
            if(answer == 1)
                progress++;
        }
        progress /= subtasks.size();
        progress *= 100;
        return progress;
    }

    public void addSubtask(String s) {
        this.subtasks.add(s);
    }

    @Override
    public Lesson copyLesson() {
        return new Task(this);
    }
}
