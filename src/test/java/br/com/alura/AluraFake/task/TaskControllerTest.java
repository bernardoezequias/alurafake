package br.com.alura.AluraFake.task;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    private Course testCourse;
    
    @BeforeEach
    void setUp() {
        testCourse = new Course("Java", "Curso de Java", new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR));
        ReflectionTestUtils.setField(testCourse, "id", 42L);
    }

    @Nested
    class CreateTaskTests {
        @Test
        void shouldCreateOpenTextTask() throws Exception {
            OpenText task = new OpenText("Enunciado", 1, Type.OPEN_TEXT, testCourse);

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(task)))
                    .andExpect(status().isOk());

            verify(taskService).createOpenTextTask(any(OpenText.class));
        }

        @Test
        void shouldCreateSingleChoiceTask() throws Exception {
            List<Option> options = Arrays.asList(
                    new Option("A", true),
                    new Option("B", false),
                    new Option("C", false)
            );

            SingleChoice task = new SingleChoice("Enunciado", 1, Type.SINGLE_CHOICE, testCourse, options);

            mockMvc.perform(post("/task/new/singlechoice"));
            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(task)))
                    .andExpect(status().isOk());
            verify(taskService).createSingleChoiceTask(any(SingleChoice.class));
        }

        @Test
        void shouldCreateMultipleChoiceTask() throws Exception {
            List<Option> options = Arrays.asList(
                    new Option("A", true),
                    new Option("B", true),
                    new Option("C", false)
            );

            MultipleChoice task = new MultipleChoice(
                    "Enunciado", 1, Type.MULTIPLE_CHOICE, testCourse, options
            );

            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON));
            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(task)))
                    .andExpect(status().isOk());
        }
    }
    
    @Nested
    class CreateTaskWithDTOTests {
        @Test
        void shouldCreateOpenTextTaskWithDTO() throws Exception {
            when(courseService.getCourseById(42L)).thenReturn(testCourse);

            NewOpenTextDTO dto = new NewOpenTextDTO();
            dto.setStatement("Enunciado");
            dto.setOrder(1);
            dto.setCourseId(42L);

            mockMvc.perform(post("/task/new/opentext")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            verify(taskService).createOpenTextTask(any(OpenText.class));
        }

        @Test
        void shouldCreateSingleChoiceTaskWithDTO() throws Exception {
            when(courseService.getCourseById(42L)).thenReturn(testCourse);

            List<Option> options = Arrays.asList(
                    new Option("A", true),
                    new Option("B", false)
            );

            NewSingleChoiceDTO dto = new NewSingleChoiceDTO();
            dto.setStatement("Enunciado");
            dto.setOrder(1);
            dto.setCourseId(42L);
            dto.setOptions(options);

            mockMvc.perform(post("/task/new/singlechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
            verify(taskService).createSingleChoiceTask(any(SingleChoice.class));
        }

        @Test
        void shouldCreateMultipleChoiceTaskWithDTO() throws Exception {
            when(courseService.getCourseById(42L)).thenReturn(testCourse);

            List<Option> options = Arrays.asList(
                    new Option("A", true),
                    new Option("B", true),
                    new Option("C", false)
            );

            NewMultipleChoiceDTO dto = new NewMultipleChoiceDTO();
            dto.setStatement("Enunciado");
            dto.setOrder(1);
            dto.setCourseId(42L);
            dto.setOptions(options);

            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON));
            mockMvc.perform(post("/task/new/multiplechoice")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }
    
    @Nested
    class RetrieveTasksTests {
        @Test
        void shouldReturnAllTasks() throws Exception {
            OpenText task1 = new OpenText("Question 1", 1, Type.OPEN_TEXT, new Course());
            SingleChoice task2 = new SingleChoice("Question 2", 2, Type.SINGLE_CHOICE, new Course(), List.of(new Option("A", true)));
            when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

            mockMvc.perform(get("/task")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].statement").value("Question 1"))
                    .andExpect(jsonPath("$[1].statement").value("Question 2"));

            verify(taskService).getAllTasks();
        }

        @Test
        void shouldReturnTaskById() throws Exception {
            OpenText task = new OpenText("Enunciado", 1, Type.OPEN_TEXT, testCourse);
            ReflectionTestUtils.setField(task, "id", 42L);
            when(taskService.getTaskById(42L)).thenReturn(task);

            mockMvc.perform(get("/task/42")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statement").value("Enunciado"));

            verify(taskService).getTaskById(42L);
        }

        @Test
        void shouldReturn404WhenTaskByIdNotFound() throws Exception {
            when(taskService.getTaskById(99L)).thenReturn(null);

            mockMvc.perform(get("/task/99")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteTasksTests {
        @Test
        void shouldDeleteTaskWhenExists() throws Exception {
            when(taskService.existsById(42L)).thenReturn(true);

            mockMvc.perform(delete("/task/42"))
                    .andExpect(status().isNoContent());

            verify(taskService).deleteTask(42L);
        }

        @Test
        void shouldReturn404WhenDeletingTaskThatDoesNotExist() throws Exception {
            when(taskService.existsById(99L)).thenReturn(false);

            mockMvc.perform(delete("/task/99"))
                    .andExpect(status().isNotFound());
        }
    }
}