package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.MultipleChoice;
import br.com.alura.AluraFake.task.SingleChoice;
import br.com.alura.AluraFake.task.OpenText;
import br.com.alura.AluraFake.task.Option;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private CourseService courseService;
    @Autowired
    private ObjectMapper objectMapper;
        
    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_invalid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        doReturn(Optional.empty()).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }


    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_no_instructor() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(false).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newCourseDTO__should_return_created_when_new_course_request_is_valid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(true).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void listAllCourses__should_list_all_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);

        Course java = new Course("Java", "Curso de java", paulo);
        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);
        Course spring = new Course("Spring", "Curso de spring", paulo);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate, spring));

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description").value("Curso de java"))
                .andExpect(jsonPath("$[1].title").value("Hibernate"))
                .andExpect(jsonPath("$[1].description").value("Curso de hibernate"))
                .andExpect(jsonPath("$[2].title").value("Spring"))
                .andExpect(jsonPath("$[2].description").value("Curso de spring"));
    }

    @Test
    void publishCourse_should_publish_course_when_valid() throws Exception {
        User paulo = new User("Paulo", "emailInstructor", Role.INSTRUCTOR);

        Course course = new Course("Java", "Curso de java", paulo);
        ReflectionTestUtils.setField(course, "id", 42L);
        course.setStatus(Status.BUILDING);

        List<Option> singleChoiceOptions = Arrays.asList(
                new Option("Java", true),
                new Option("Python", false),
                new Option("Ruby", false)
        );

        List<Option> multipleChoiceOptions = Arrays.asList(
                new Option("Compilado", true),
                new Option("Interpretado", true),
                new Option("Script", false)
        );

        SingleChoice singleChoice = new SingleChoice(
                "Qual a linguagem mais usada?", 
                1,
                Type.SINGLE_CHOICE,
                course,
                singleChoiceOptions
        );

        MultipleChoice multipleChoice = new MultipleChoice(
                "Quais são linguagens compiladas?", 
                2,
                Type.MULTIPLE_CHOICE,
                course,
                multipleChoiceOptions
        );

        OpenText openText = new OpenText(
                "Descreva a sua experiência com Java.",
                3,
                Type.OPEN_TEXT,
                course
        );

        List<Task> tasks = Arrays.asList(singleChoice, multipleChoice, openText);
        course.setTasks(tasks);

        when(courseRepository.existsById(42L)).thenReturn(true);
        when(courseService.getCourseById(42L)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doAnswer(invocation -> {
        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(java.time.LocalDateTime.now());
        return null;
        }).when(courseService).publishCourse(42L);
        
        mockMvc.perform(post("/course/42/publish")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());

        //verify(courseRepository).save(argThat(c -> c.getStatus() == Status.PUBLISHED && c.getPublishedAt() != null));
}



}