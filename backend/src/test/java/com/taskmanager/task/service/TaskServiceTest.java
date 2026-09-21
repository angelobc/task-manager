package com.taskmanager.task.service;

import com.taskmanager.common.exception.ForbiddenException;
import com.taskmanager.common.exception.ResourceNotFoundException;
import com.taskmanager.project.model.Project;
import com.taskmanager.project.repository.ProjectRepository;
import com.taskmanager.task.dto.CreateTaskRequest;
import com.taskmanager.task.dto.TaskResponse;
import com.taskmanager.task.mapper.TaskMapper;
import com.taskmanager.task.model.Task;
import com.taskmanager.task.model.TaskPriority;
import com.taskmanager.task.model.TaskStatus;
import com.taskmanager.task.repository.TaskRepository;
import com.taskmanager.user.model.User;
import com.taskmanager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;

    private TaskService taskService;

    private User owner;
    private User otherUser;
    private Project project;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, projectRepository, userRepository, taskMapper);

        owner = User.builder().id(UUID.randomUUID()).email("owner@example.com").build();
        otherUser = User.builder().id(UUID.randomUUID()).email("intruder@example.com").build();
        project = Project.builder().id(UUID.randomUUID()).name("Website Revamp").owner(owner).build();
    }

    @Test
    void createTask_assignsNextPositionAndDefaultStatusPriority() {
        CreateTaskRequest request = new CreateTaskRequest("Design homepage", "desc", null, null, null, null);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(taskRepository.findMaxPositionByProject(project)).thenReturn(2);
        when(taskRepository.saveAndFlush(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        when(taskMapper.toResponse(any(Task.class))).thenAnswer(inv -> {
            Task saved = inv.getArgument(0);
            return new TaskResponse(saved.getId(), project.getId(), saved.getTitle(), saved.getDescription(),
                    saved.getStatus(), saved.getPriority(), saved.getDueDate(), saved.getPosition(),
                    null, owner.getId(), null, null);
        });

        TaskResponse response = taskService.createTask(owner.getId(), project.getId(), request);

        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response.priority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(response.position()).isEqualTo(3);
    }

    @Test
    void createTask_throwsForbiddenWhenProjectNotOwnedByRequestingUser() {
        CreateTaskRequest request = new CreateTaskRequest("Design homepage", null, null, null, null, null);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> taskService.createTask(otherUser.getId(), project.getId(), request))
                .isInstanceOf(ForbiddenException.class);

        verify(taskRepository, never()).saveAndFlush(any());
    }

    @Test
    void getTask_throwsNotFoundWhenTaskBelongsToDifferentProject() {
        UUID taskId = UUID.randomUUID();
        Project otherProject = Project.builder().id(UUID.randomUUID()).name("Other").owner(owner).build();
        Task task = Task.builder().id(taskId).project(otherProject).title("Unrelated task").build();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.getTask(owner.getId(), project.getId(), taskId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getTasks_throwsForbiddenWhenProjectNotOwnedByRequestingUser() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> taskService.getTasks(otherUser.getId(), project.getId(), null, null, null))
                .isInstanceOf(ForbiddenException.class);
    }
}
