package br.com.alura.AluraFake.task;

import org.hibernate.validator.constraints.Length;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Inheritance
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Statement cannot be null")
    @NotBlank(message = "Statement cannot be blank")
    @Length(min = 4, max = 255, message = "Statement must be between 4 and 255 characters")
    private String statement;

    @Positive(message = "order must be a positive number")
    @Column(name = "`order`")
    private int order;

    @Enumerated(EnumType.STRING)
    protected Type taskType;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Task(String statement, int order, Type taskType, Course course) {
        this.statement = statement;
        this.order = order;
        this.taskType = taskType;
        this.course = course;
    }

    public Type getTaskType() {
        return taskType;
    }

    public String getStatement() {
        return statement;
    }

    public int getOrder() {
        return order;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setTaskType(Type taskType) {
        this.taskType = taskType;
    }

    protected abstract void setType(String string);

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }
}

