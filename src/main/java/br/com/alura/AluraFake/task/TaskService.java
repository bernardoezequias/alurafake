package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    
    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }


    public OpenText createOpenTextTask(OpenText task) {
        task.setType("open_text");
        sortTasksByOrder(task.getCourse(), task);
        return taskRepository.save(task);
    }


    public SingleChoice createSingleChoiceTask(SingleChoice task) {
        validateSingleChoice(task.getOptions(), task.getStatement());
        sortTasksByOrder(task.getCourse(), task);
        task.setType("single_choice");
        return taskRepository.save(task);
    }


    public MultipleChoice createMultipleChoiceTask(MultipleChoice task) {
        validateMutlitpleChoice(task.getOptions(), task.getStatement());
        sortTasksByOrder(task.getCourse(), task);
        task.setType("multiple_choice");
        return taskRepository.save(task);
    }
    

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }


    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
    }


    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
    }


    public boolean existsById(Long id) {
        return taskRepository.existsById(id);

    }


    private void validateSingleChoice(List<Option> options, String statement) {
    if (options.size() < 2 || options.size() > 5) {
        throw new IllegalArgumentException("Options must have between 2 and 5 items.");
    }

    long correctCount = options.stream().filter(Option::isCorrect).count();
    if (correctCount != 1) {
        throw new IllegalArgumentException("There must be exactly one correct option." + options + correctCount);
    }

    long uniqueCount = options.stream().map(Option::getOption).distinct().count();
    if (uniqueCount != options.size()) {
        throw new IllegalArgumentException("Options must be unique.");
    }

    if (options.stream().anyMatch(opt -> opt.getOption().equalsIgnoreCase(statement))) {
        throw new IllegalArgumentException("Options cannot be equal to the statement.");
    }

    for (Option opt : options) {
        if (opt.getOption().length() < 4 || opt.getOption().length() > 80) {
            throw new IllegalArgumentException("Each option must be between 4 and 80 characters.");
        }
    }
}

    private void validateMutlitpleChoice(List<Option> options, String statement) {

    if (options.size() < 3 || options.size() > 5) {
        throw new IllegalArgumentException("Options must have between 2 and 5 items.");
    }

    long correctCount = options.stream().filter(Option::isCorrect).count();
    long incorrectCount = options.size() - correctCount;

    if (correctCount < 2) {
        throw new IllegalArgumentException("There must be at least two correct options.");
    }
    if (incorrectCount < 1) {
        throw new IllegalArgumentException("There must be at least one incorrect option.");
    }

    long uniqueCount = options.stream().map(Option::getOption).distinct().count();
    if (uniqueCount != options.size()) {
        throw new IllegalArgumentException("Options must be unique.");
    }

    if (options.stream().anyMatch(opt -> opt.getOption().equalsIgnoreCase(statement))) {
        throw new IllegalArgumentException("Options cannot be equal to the statement.");
    }

    for (Option opt : options) {
        if (opt.getOption().length() < 4 || opt.getOption().length() > 80) {
            throw new IllegalArgumentException("Each option must be between 4 and 80 characters.");
        }
    }
    
    }

    private void sortTasksByOrder(Course course, Task newTask) {
        List<Task> tasks = taskRepository.findByCourseIdOrderByOrderAsc(course.getId());
        int actualSize = tasks.size();

        if (newTask.getOrder() > actualSize + 1) {
            throw new IllegalArgumentException("Order cannot have breaks between them.");
        }

        for (Task task: tasks) {
            if (task.getOrder() >= newTask.getOrder()) {
                task.setOrder(task.getOrder() + 1);
                taskRepository.save(task);
            }
        }

        taskRepository.save(newTask);
    }
}





