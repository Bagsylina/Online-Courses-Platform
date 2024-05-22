package model;

//CLass for a question in a quiz, consisting of the statement, possible options and answer
public class QuizQuestion {
    private String question;
    private String[] options;
    private int answer;

    public QuizQuestion(String question, String[] options, int answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public QuizQuestion(QuizQuestion question) {
        this.question = question.question;
        this.options = question.options;
        this.answer = question.answer;
    }

    //getters and setters
    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    //check if provided answer is correct, incorrect or invalid
    public int answerQuestion(int chosen) {
        if(chosen < options.length) {
            if (chosen == this.answer)
                return 1;
        }
        else
            System.out.println("Invalid Answer");
        return 0;
    }
}
