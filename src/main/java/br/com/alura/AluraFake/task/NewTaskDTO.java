package br.com.alura.AluraFake.task;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NewTaskDTO {
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255, message = "Statement must be between 4 and 255 characters")
    private String statement;
    @Positive(message = "Order must be a positive number")
    private int order;
    @NotNull(message = "Task type cannot be null")
    private Type taskType;
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;

    public NewTaskDTO() {}
    
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

    public Type getTaskType() {
        return taskType;
    }

    public void setTaskType(Type taskType) {
        this.taskType = taskType;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    
}
