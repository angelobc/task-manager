package com.taskmanager.task.service;

import com.taskmanager.common.exception.ForbiddenException;
import com.taskmanager.common.exception.ResourceNotFoundException;
import com.taskmanager.project.model.Project;
import com.taskmanager.project.repository.ProjectRepository;
import com.taskmanager.task.dto.*;
import com.taskmanager.task.mapper.TaskMapper;
import com.taskmanager.task.model.Task;
import com.taskmanager.task.model.TaskPriority;
import com.taskmanager.task.model.TaskStatus;
import com.taskmanager.task.repository.TaskRepository;
import com.taskmanager.user.model.User;
import com.taskmanager.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(UUID userId, UUID projectId,
                                       TaskStatus status, TaskPriority priority, String search) {
        Project project = findAccessibleProjectOrThrow(userId, projectId);
        String searchParam = search != null ? "%" + search.toLowerCase() + "%" : null;
        return taskMapper.toResponseList(
                taskRepository.findByProjectWithFilters(project, status, priority, searchParam));
    }

    public TaskResponse createTask(UUID userId, UUID projectId, CreateTaskRequest request) {
        Project project = findAccessibleProjectOrThrow(userId, projectId);
        User creator = findUserOrThrow(userId);

        User assignee = request.assigneeId() != null
                ? findUserOrThrow(request.assigneeId())
                : null;

        int nextPosition = taskRepository.findMaxPositionByProject(project) + 1;

        Task task = Task.builder()
                .project(project)
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : TaskStatus.TODO)
                .priority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM)
                .dueDate(request.dueDate())
                .position(nextPosition)
                .assignee(assignee)
                .createdBy(creator)
                .build();

        return taskMapper.toResponse(taskRepository.saveAndFlush(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID userId, UUID projectId, UUID taskId) {
        findAccessibleProjectOrThrow(userId, projectId);
        return taskMapper.toResponse(findTaskInProjectOrThrow(taskId, projectId));
    }

    public TaskResponse updateTask(UUID userId, UUID projectId, UUID taskId, UpdateTaskRequest request) {
        findAccessibleProjectOrThrow(userId, projectId);
        Task task = findTaskInProjectOrThrow(taskId, projectId);

        User assignee = request.assigneeId() != null
                ? findUserOrThrow(request.assigneeId())
                : null;

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setAssignee(assignee);

        return taskMapper.toResponse(taskRepository.saveAndFlush(task));
    }

    public void deleteTask(UUID userId, UUID projectId, UUID taskId) {
        findAccessibleProjectOrThrow(userId, projectId);
        taskRepository.delete(findTaskInProjectOrThrow(taskId, projectId));
    }

    public TaskResponse updateStatus(UUID userId, UUID projectId, UUID taskId, TaskStatus status) {
        findAccessibleProjectOrThrow(userId, projectId);
        Task task = findTaskInProjectOrThrow(taskId, projectId);
        task.setStatus(status);
        return taskMapper.toResponse(taskRepository.saveAndFlush(task));
    }

    public List<TaskResponse> reorderTasks(UUID userId, UUID projectId, List<ReorderRequest.ReorderItem> items) {
        Project project = findAccessibleProjectOrThrow(userId, projectId);

        items.forEach(item -> {
            Task task = findTaskInProjectOrThrow(item.id(), projectId);
            task.setPosition(item.position());
            taskRepository.saveAndFlush(task);
        });

        return taskMapper.toResponseList(taskRepository.findByProjectOrderByPositionAsc(project));
    }

    private Project findAccessibleProjectOrThrow(UUID userId, UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
        return project;
    }

    private Task findTaskInProjectOrThrow(UUID taskId, UUID projectId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!task.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("Task not found in this project");
        }
        return task;
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
