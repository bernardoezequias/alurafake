package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCourseId(Long courseId);

    List<Task> findByCourseIdAndTaskType(Long courseId, Type taskType);

    List<Task> findByCourseIdAndOrder(Long courseId, int order);
    
}
