package br.com.alura.AluraFake.task;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class NewMultipleChoiceDTO {

    @NotNull
    private Long courseId;

    @NotBlank
    private String statement;

    @Positive
    private int order;

    @Valid
    @Size(min = 3, max = 5, message = "There must be between 3 and 5 options")
    private List<Option> options;

    public NewMultipleChoiceDTO() {}

    public NewMultipleChoiceDTO(Long courseId, String statement, int order, List<Option> options) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
        this.options = options;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
