package br.com.alura.AluraFake.course;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.Type;
import jakarta.transaction.Transactional;

@Service
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
    }

    public void deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Course not found with id: " + id);
        }
    }

    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course updateCourse(Long id, Course course) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course not found with id: " + id);
        }
        course.setId(id);
        return courseRepository.save(course);
    }

    @Transactional
    public void publishCourse(Long id) {
        Course course = getCourseById(id);

        if (course.getStatus() == Status.PUBLISHED) {
            throw new IllegalStateException("Course is already published");
        }

        List<Task> tasks = course.getTasks();

        if (course.getTasks().isEmpty()) {
            throw new IllegalStateException("Course must have at least one task to be published");
        }

        Set<Type> taskTypes = tasks.stream()
        .map(Task::getTaskType)
        .collect(Collectors.toSet());

        boolean haveAllTypes = Arrays.stream(Type.values())
        .allMatch(taskTypes::contains);

        if (!haveAllTypes) {
            throw new IllegalStateException("Course must have at least one task of each type: " + Arrays.toString(Type.values()));
        }

        //TODO: implement order logic and sorting if necessary

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        courseRepository.save(course);
    }
}
