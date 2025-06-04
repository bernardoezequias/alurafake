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

        @Test
        void publishCourse_should_throw_exception_when_course_already_published() {
        User instructor = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
        Course course = new Course("Java", "Java course", instructor);
        ReflectionTestUtils.setField(course, "id", 1L);
        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> courseService.publishCourse(1L));
        
        assertEquals("Course is already published", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
        }

        @Test
        void publishCourse_should_throw_exception_when_course_has_no_tasks() {
        User instructor = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
        Course course = new Course("Java", "Java course", instructor);
        ReflectionTestUtils.setField(course, "id", 1L);
        course.setStatus(Status.BUILDING);
        course.setTasks(List.of());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> courseService.publishCourse(1L));
        
        assertEquals("Course must have at least one task to be published", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
        }

        @Test
        void publishCourse_should_throw_exception_when_missing_task_types() {
        User instructor = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
        Course course = new Course("Java", "Java course", instructor);
        ReflectionTestUtils.setField(course, "id", 1L);
        course.setStatus(Status.BUILDING);
        
        // Only adding SINGLE_CHOICE task, missing other types
        SingleChoice task = new SingleChoice(
                "What is Java?", 
                1, 
                Type.SINGLE_CHOICE, 
                course,
                Arrays.asList(
                        new Option("Programming Language", true),
                        new Option("Coffee", false)
                )
        );
        course.setTasks(List.of(task));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> courseService.publishCourse(1L));
        
        assertEquals("Course must have at least one task of each type: [OPEN_TEXT, MULTIPLE_CHOICE, SINGLE_CHOICE]", 
                exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
        }

        @Test
        void getCourseById_should_return_course_when_found() {
        User instructor = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
        Course course = new Course("Java", "Java course", instructor);
        ReflectionTestUtils.setField(course, "id", 1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1L);

        assertEquals(course, result);
        verify(courseRepository).findById(1L);
        }

        @Test
        void getCourseById_should_throw_exception_when_not_found() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> courseService.getCourseById(99L));
        
        assertEquals("Course not found with id: 99", exception.getMessage());
        }

        @Test
        void createCourse_should_save_and_return_course() {
        User instructor = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
        Course course = new Course("Java", "Java course", instructor);

        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseService.createCourse(course);

        assertEquals(course, result);
        verify(courseRepository).save(course);
        }

        @Test
        void deleteCourse_should_delete_when_course_exists() {
        Long courseId = 1L;
        when(courseRepository.existsById(courseId)).thenReturn(true);
        
        courseService.deleteCourse(courseId);
        
        verify(courseRepository).deleteById(courseId);
        }

        @Test
        void deleteCourse_should_throw_exception_when_course_not_found() {
        Long courseId = 99L;
        when(courseRepository.existsById(courseId)).thenReturn(false);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> courseService.deleteCourse(courseId));
        
        assertEquals("Course not found with id: 99", exception.getMessage());
        verify(courseRepository, never()).deleteById(any());
        }

        @Test
        void existsById_should_return_true_when_course_exists() {
        Long courseId = 1L;
        when(courseRepository.existsById(courseId)).thenReturn(true);
        
        boolean result = courseService.existsById(courseId);
        
        assertTrue(result);
        }

        @Test
        void existsById_should_return_false_when_course_does_not_exist() {
        Long courseId = 99L;
        when(courseRepository.existsById(courseId)).thenReturn(false);
        
        boolean result = courseService.existsById(courseId);
        
        assertFalse(result);
        }

        @Test
        void getAllCourses_should_return_all_courses() {
        User instructor = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
        List<Course> courses = Arrays.asList(
                new Course("Java", "Java course", instructor),
                new Course("Python", "Python course", instructor)
        );
        
        when(courseRepository.findAll()).thenReturn(courses);
        
        List<Course> result = courseService.getAllCourses();
        
        assertEquals(courses, result);
        assertEquals(2, result.size());
        }
        @Test
        void getAllCourses_should_return_empty_list_when_no_courses() {
                when(courseRepository.findAll()).thenReturn(Collections.emptyList());
                
                List<Course> result = courseService.getAllCourses();
                
                assertTrue(result.isEmpty());
                verify(courseRepository).findAll();
        }
}
