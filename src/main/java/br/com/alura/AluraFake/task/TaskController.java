package br.com.alura.AluraFake.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.alura.AluraFake.course.CourseRepository;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final CourseRepository courseRepository;

    @Autowired
    public TaskController(TaskService taskService, CourseRepository courseRepository) {
        this.taskService = taskService;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/new/opentext")
    public ResponseEntity<?> newOpenTextExercise(@RequestBody OpenText task) {
        task.setType("open_text");
        taskService.createOpenTextTask(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<?> newSingleChoice(@RequestBody SingleChoice task) {
        task.setType("single_choice");
        System.out.println("Received task: " + task);
        taskService.createSingleChoiceTask(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity<?> newMultipleChoice(@RequestBody MultipleChoice task) {
        task.setType("multiple_choice");
        taskService.createMultipleChoiceTask(task);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        if (taskService.existsById(id)) {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}