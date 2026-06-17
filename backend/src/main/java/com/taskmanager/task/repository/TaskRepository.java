package com.taskmanager.task.repository;

import com.taskmanager.project.model.Project;
import com.taskmanager.task.model.Task;
import com.taskmanager.task.model.TaskPriority;
import com.taskmanager.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectOrderByPositionAsc(Project project);

    @Query("""
            SELECT t FROM Task t
            WHERE t.project = :project
              AND (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:search IS NULL OR LOWER(t.title) LIKE :search)
            ORDER BY t.position ASC
            """)
    List<Task> findByProjectWithFilters(
            @Param("project") Project project,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("search") String search);

    @Query("SELECT COALESCE(MAX(t.position), -1) FROM Task t WHERE t.project = :project")
    int findMaxPositionByProject(@Param("project") Project project);
}
