package br.com.alura.AluraFake.task;   

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TaskService taskService;

    private Course course;
    private OpenText openTextTask;
    private SingleChoice singleChoiceTask;
    private MultipleChoice multipleChoiceTask;

    @BeforeEach
    void setUp() {
        course = new Course();
        ReflectionTestUtils.setField(course, "id", 1L);

        openTextTask = new OpenText();
        ReflectionTestUtils.setField(openTextTask, "id", 1L);
        openTextTask.setCourse(course);
        openTextTask.setStatement("Describe your Java experience");
        openTextTask.setOrder(1);

        List<Option> singleOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Python", false),
                new Option("Ruby", false)
        );

        singleChoiceTask = new SingleChoice();
        ReflectionTestUtils.setField(singleChoiceTask, "id", 2L);
        singleChoiceTask.setCourse(course);
        singleChoiceTask.setStatement("Which is the most popular language?");
        singleChoiceTask.setOrder(2);
        singleChoiceTask.setOptions(singleOptions);

        List<Option> multipleOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Kotlin", true),
                new Option("Python", false),
                new Option("Ruby", false)
        );

        multipleChoiceTask = new MultipleChoice();
        ReflectionTestUtils.setField(multipleChoiceTask, "id", 3L);
        multipleChoiceTask.setCourse(course);
        multipleChoiceTask.setStatement("Which are JVM languages?");
        multipleChoiceTask.setOrder(3);
        multipleChoiceTask.setOptions(multipleOptions);
    }

    @Test
    void createOpenTextTask_ShouldSetTypeAndSortTasks() {
        when(taskRepository.findByCourseIdOrderByOrderAsc(anyLong())).thenReturn(Collections.emptyList());
        when(taskRepository.save(any(OpenText.class))).thenReturn(openTextTask);

        OpenText result = taskService.createOpenTextTask(openTextTask);

        assertEquals("OPEN_TEXT", result.getTaskType().toString());
        verify(taskRepository).findByCourseIdOrderByOrderAsc(course.getId());
        verify(taskRepository, times(2)).save(any(OpenText.class));
    }

    @Test
    void createSingleChoiceTask_ShouldValidateAndSave() {
        when(taskRepository.findByCourseIdOrderByOrderAsc(anyLong())).thenReturn(Collections.emptyList());
        when(taskRepository.save(any(SingleChoice.class))).thenReturn(singleChoiceTask);
        
        // Make sure the order is set to 1 when no existing tasks
        singleChoiceTask.setOrder(1);

        SingleChoice result = taskService.createSingleChoiceTask(singleChoiceTask);

        assertEquals("SINGLE_CHOICE", result.getTaskType().toString());
        verify(taskRepository).findByCourseIdOrderByOrderAsc(course.getId());
        verify(taskRepository, times(2)).save(any(SingleChoice.class));
    }

    @Test
    void createMultipleChoiceTask_ShouldValidateAndSave() {
        when(taskRepository.findByCourseIdOrderByOrderAsc(anyLong())).thenReturn(Collections.emptyList());
        when(taskRepository.save(any(MultipleChoice.class))).thenReturn(multipleChoiceTask);
        
        // Make sure the order is set to 1 when no existing tasks
        multipleChoiceTask.setOrder(1);

        MultipleChoice result = taskService.createMultipleChoiceTask(multipleChoiceTask);

        assertEquals("MULTIPLE_CHOICE", result.getTaskType().toString());
        verify(taskRepository).findByCourseIdOrderByOrderAsc(course.getId());
        verify(taskRepository, times(2)).save(any(MultipleChoice.class));
    }

    @Test
    void createSingleChoiceTask_ShouldThrowExceptionWhenInvalidOptions() {
        // Less than 2 options
        List<Option> invalidOptions = Collections.singletonList(new Option("Java", true));
        SingleChoice invalidTask = new SingleChoice();
        invalidTask.setCourse(course);
        invalidTask.setOptions(invalidOptions);
        invalidTask.setStatement("Question");

        assertThrows(IllegalArgumentException.class, () -> taskService.createSingleChoiceTask(invalidTask));

        // More than one correct answer
        List<Option> tooManyCorrectOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Python", true)
        );
        invalidTask.setOptions(tooManyCorrectOptions);

        assertThrows(IllegalArgumentException.class, () -> taskService.createSingleChoiceTask(invalidTask));
    }

    @Test
    void createMultipleChoiceTask_ShouldThrowExceptionWhenInvalidOptions() {
        // Less than 3 options
        List<Option> invalidOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Python", true)
        );
        MultipleChoice invalidTask = new MultipleChoice();
        invalidTask.setCourse(course);
        invalidTask.setOptions(invalidOptions);
        invalidTask.setStatement("Question");

        assertThrows(IllegalArgumentException.class, () -> taskService.createMultipleChoiceTask(invalidTask));

        // Less than 2 correct options
        List<Option> notEnoughCorrectOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Python", false),
                new Option("Ruby", false)
        );
        invalidTask.setOptions(notEnoughCorrectOptions);

        assertThrows(IllegalArgumentException.class, () -> taskService.createMultipleChoiceTask(invalidTask));

        // No incorrect option
        List<Option> noIncorrectOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Python", true),
                new Option("Ruby", true)
        );
        invalidTask.setOptions(noIncorrectOptions);

        assertThrows(IllegalArgumentException.class, () -> taskService.createMultipleChoiceTask(invalidTask));
    }

    @Test
    void sortTasksByOrder_ShouldReorderTasksWhenNewTaskInserted() {
        Task existingTask1 = new OpenText();
        ReflectionTestUtils.setField(existingTask1, "id", 1L);
        existingTask1.setOrder(1);

        Task existingTask2 = new OpenText();
        ReflectionTestUtils.setField(existingTask2, "id", 2L);
        existingTask2.setOrder(2);

        List<Task> existingTasks = Arrays.asList(existingTask1, existingTask2);
        
        when(taskRepository.findByCourseIdOrderByOrderAsc(course.getId())).thenReturn(existingTasks);

        // Insert new task at order 1
        OpenText newTask = new OpenText();
        newTask.setCourse(course);
        newTask.setOrder(1);
        
        // Mock saved tasks to have IDs
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            if (ReflectionTestUtils.getField(savedTask, "id") == null) {
                ReflectionTestUtils.setField(savedTask, "id", 3L);
            }
            return savedTask;
        });
        
        taskService.createOpenTextTask(newTask);

        // Verify existing tasks were reordered
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, atLeast(3)).save(taskCaptor.capture());
        
        List<Task> savedTasks = taskCaptor.getAllValues();
        boolean foundUpdatedTask1 = false;
        boolean foundUpdatedTask2 = false;
        
        for (Task task : savedTasks) {
            Long taskId = (Long) ReflectionTestUtils.getField(task, "id");
            if (taskId != null) {
                if (taskId == 1L && task.getOrder() == 2) {
                    foundUpdatedTask1 = true;
                }
                if (taskId == 2L && task.getOrder() == 3) {
                    foundUpdatedTask2 = true;
                }
            }
        }
        
        assertTrue(foundUpdatedTask1 || foundUpdatedTask2);
    }

    @Test
    void getTaskById_ShouldReturnTaskWhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(openTextTask));

        Task result = taskService.getTaskById(1L);

        assertEquals(openTextTask, result);
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_ShouldThrowExceptionWhenNotExists() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void deleteTask_ShouldDeleteWhenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_ShouldThrowExceptionWhenNotExists() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        List<Task> tasks = Arrays.asList(openTextTask, singleChoiceTask, multipleChoiceTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(3, result.size());
        assertEquals(tasks, result);
    }

    @Test
    void existsById_ShouldReturnTrueWhenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        assertTrue(taskService.existsById(1L));
    }

    @Test
    void existsById_ShouldReturnFalseWhenNotExists() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertFalse(taskService.existsById(99L));
    }
}