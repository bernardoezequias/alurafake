package br.com.alura.AluraFake.task;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.alura.AluraFake.course.CourseService;

import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final CourseService courseService;

    @Autowired
    public TaskController(TaskService taskService, CourseService courseService) {
        this.taskService = taskService;
        this.courseService = courseService;
    }

    @PostMapping("/new/opentext")
    public ResponseEntity<?> newOpenTextExercise(@RequestBody NewOpenTextDTO OpenTextDTO) {
        OpenText task = new OpenText(
            OpenTextDTO.getStatement(),
            OpenTextDTO.getOrder(),
            Type.OPEN_TEXT,
            courseService.getCourseById(OpenTextDTO.getCourseId())
        );
        task.setType("open_text");
        taskService.createOpenTextTask(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<?> newSingleChoice(@RequestBody NewSingleChoiceDTO SingleChoiceDTO) {
        SingleChoice task = new SingleChoice(
            SingleChoiceDTO.getStatement(),
            SingleChoiceDTO.getOrder(),
            Type.SINGLE_CHOICE,
            courseService.getCourseById(SingleChoiceDTO.getCourseId()),
            SingleChoiceDTO.getOptions()
        );
        task.setType("single_choice");
        taskService.createSingleChoiceTask(task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity<?> newMultipleChoice(@RequestBody NewMultipleChoiceDTO MultipleChoiceDTO) {
        MultipleChoice task = new MultipleChoice(
            MultipleChoiceDTO.getStatement(),
            MultipleChoiceDTO.getOrder(),
            Type.MULTIPLE_CHOICE,
            courseService.getCourseById(MultipleChoiceDTO.getCourseId()),
            MultipleChoiceDTO.getOptions()
        );
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