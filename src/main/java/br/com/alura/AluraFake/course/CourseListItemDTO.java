package br.com.alura.AluraFake.course;

import java.io.Serializable;
import java.time.LocalDate;

public class CourseListItemDTO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Status status;
    private LocalDate publishedAt;

    public CourseListItemDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.status = course.getStatus();
        this.publishedAt = course.getPublishedAt() != null ? course.getPublishedAt().toLocalDate() : null;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getPublishedAt() {
        return publishedAt;
    }
}
