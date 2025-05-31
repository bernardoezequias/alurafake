package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoice extends Task {

    private String[] options;
    private String[] correctAnswers;

    public MultipleChoice(String statement, int order, Type taskType, Course course, String[] options, String[] correctAnswers) {
        super(statement, order, taskType, course);
        this.options = options;
        this.correctAnswers = correctAnswers;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String[] getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String[] correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setType(String type) {
    String formatted = type.toUpperCase().replace("-", "_").replace(" ", "_");
    this.taskType = Type.valueOf(formatted);
}
}