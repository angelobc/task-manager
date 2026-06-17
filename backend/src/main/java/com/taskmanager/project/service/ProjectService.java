package com.taskmanager.project.service;

import com.taskmanager.common.exception.ForbiddenException;
import com.taskmanager.common.exception.ResourceNotFoundException;
import com.taskmanager.project.dto.CreateProjectRequest;
import com.taskmanager.project.dto.ProjectResponse;
import com.taskmanager.project.dto.UpdateProjectRequest;
import com.taskmanager.project.mapper.ProjectMapper;
import com.taskmanager.project.model.Project;
import com.taskmanager.project.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(UUID userId) {
        User user = findUserOrThrow(userId);
        return projectRepository.findByOwnerOrderByCreatedAtDesc(user)
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    public ProjectResponse createProject(UUID userId, CreateProjectRequest request) {
        User owner = findUserOrThrow(userId);
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .color(request.color())
                .owner(owner)
                .build();
        return projectMapper.toResponse(projectRepository.saveAndFlush(project));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID userId, UUID projectId) {
        return projectMapper.toResponse(findOwnedProjectOrThrow(userId, projectId));
    }

    public ProjectResponse updateProject(UUID userId, UUID projectId, UpdateProjectRequest request) {
        Project project = findOwnedProjectOrThrow(userId, projectId);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setColor(request.color());
        return projectMapper.toResponse(projectRepository.saveAndFlush(project));
    }

    public void deleteProject(UUID userId, UUID projectId) {
        projectRepository.delete(findOwnedProjectOrThrow(userId, projectId));
    }

    public ProjectResponse toggleArchive(UUID userId, UUID projectId) {
        Project project = findOwnedProjectOrThrow(userId, projectId);
        project.setArchived(!project.isArchived());
        return projectMapper.toResponse(projectRepository.saveAndFlush(project));
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Project findOwnedProjectOrThrow(UUID userId, UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!project.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
        return project;
    }
}
