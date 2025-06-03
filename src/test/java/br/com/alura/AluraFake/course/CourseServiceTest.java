package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.*;
import br.com.alura.AluraFake.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void publishCourse_should_publish_course_when_valid() {
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

        when(courseRepository.findById(42L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        courseService.publishCourse(42L);

        assertEquals(Status.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());
        verify(courseRepository).save(argThat(c -> c.getStatus() == Status.PUBLISHED && c.getPublishedAt() != null));
    }
}