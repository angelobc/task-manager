package com.taskmanager.project.service;

import com.taskmanager.common.exception.ForbiddenException;
import com.taskmanager.common.exception.ResourceNotFoundException;
import com.taskmanager.project.dto.CreateProjectRequest;
import com.taskmanager.project.dto.ProjectResponse;
import com.taskmanager.project.mapper.ProjectMapper;
import com.taskmanager.project.model.Project;
import com.taskmanager.project.repository.ProjectRepository;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMapper projectMapper;

    private ProjectService projectService;

    private User owner;
    private User otherUser;
    private Project project;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, userRepository, projectMapper);

        owner = User.builder().id(UUID.randomUUID()).email("owner@example.com").build();
        otherUser = User.builder().id(UUID.randomUUID()).email("intruder@example.com").build();
        project = Project.builder().id(UUID.randomUUID()).name("Website Revamp").owner(owner).build();
    }

    @Test
    void getProject_returnsProjectWhenRequestedByOwner() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project))
                .thenReturn(new ProjectResponse(project.getId(), project.getName(), null, null, false, owner.getId(), null, null));

        ProjectResponse response = projectService.getProject(owner.getId(), project.getId());

        assertThat(response.id()).isEqualTo(project.getId());
    }

    @Test
    void getProject_throwsForbiddenWhenRequestedByNonOwner() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.getProject(otherUser.getId(), project.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getProject_throwsNotFoundWhenProjectDoesNotExist() {
        UUID missingId = UUID.randomUUID();
        when(projectRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProject(owner.getId(), missingId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createProject_savesProjectOwnedByRequestingUser() {
        CreateProjectRequest request = new CreateProjectRequest("New Project", "desc", "#FFFFFF");
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));
        when(projectMapper.toResponse(any(Project.class)))
                .thenAnswer(inv -> {
                    Project saved = inv.getArgument(0);
                    return new ProjectResponse(saved.getId(), saved.getName(), saved.getDescription(),
                            saved.getColor(), saved.isArchived(), saved.getOwner().getId(), null, null);
                });

        ProjectResponse response = projectService.createProject(owner.getId(), request);

        assertThat(response.ownerId()).isEqualTo(owner.getId());
        assertThat(response.name()).isEqualTo("New Project");
    }

    @Test
    void deleteProject_throwsForbiddenWhenRequestedByNonOwner() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.deleteProject(otherUser.getId(), project.getId()))
                .isInstanceOf(ForbiddenException.class);

        verify(projectRepository, never()).delete(any());
    }
}
