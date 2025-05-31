package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenText extends Task {

    public OpenText(String statement, int order, Type taskType, Course course) {
        super(statement, order, taskType, course);
    }

    public void setType(String type) {
    String formatted = type.toUpperCase().replace("-", "_").replace(" ", "_");
    this.taskType = Type.valueOf(formatted);
    }

    
}
