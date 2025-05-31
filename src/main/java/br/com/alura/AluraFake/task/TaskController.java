package br.com.alura.AluraFake.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.alura.AluraFake.course.CourseRepository;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/new/opentext")
    public ResponseEntity<?> newOpenTextExercise(@RequestBody OpenText task) {
        task.setType("open_text");
        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<?> newSingleChoice(@RequestBody SingleChoice task) {
        task.setType("single_choice");
        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity<?> newMultipleChoice(@RequestBody MultipleChoice task) {
        task.setType("multiple_choice");
        taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}