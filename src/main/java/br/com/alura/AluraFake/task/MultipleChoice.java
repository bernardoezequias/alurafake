package br.com.alura.AluraFake.task;

import java.util.List;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoice extends Task {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "multiple_choice_options",
        joinColumns = @JoinColumn(name = "task_id")
    )
    @Valid
    @Size(min = 2, max = 5, message = "There must be between 3 and 5 options")
    @NotEmpty
    private List<Option> options;

    public MultipleChoice(String statement, int order, Type taskType, Course course, List<Option> options) {
        super(statement, order, taskType, course);
        this.options = options;

    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public void setType(String type) {
    String formatted = type.toUpperCase().replace("-", "_").replace(" ", "_");
    this.taskType = Type.valueOf(formatted);
}
}