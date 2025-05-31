package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoice extends Task {

    private boolean correctAnswer;

    public SingleChoice(String statement, int order, Type taskType, Course course, boolean correctAnswer) {
        super(statement, order, taskType, course);
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public void setType(String type) {
    String formatted = type.toUpperCase().replace("-", "_").replace(" ", "_");
    this.taskType = Type.valueOf(formatted);
    }
    
}
