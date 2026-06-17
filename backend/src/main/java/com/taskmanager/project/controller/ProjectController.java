package com.taskmanager.project.controller;

import com.taskmanager.common.response.ApiResponse;
import com.taskmanager.project.dto.CreateProjectRequest;
import com.taskmanager.project.dto.ProjectResponse;
import com.taskmanager.project.dto.UpdateProjectRequest;
import com.taskmanager.project.service.ProjectService;
import com.taskmanager.user.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjects(currentUser.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(projectService.createProject(currentUser.getId(), request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProject(currentUser.getId(), id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.updateProject(currentUser.getId(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        projectService.deleteProject(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ProjectResponse>> toggleArchive(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.toggleArchive(currentUser.getId(), id)));
    }
}
