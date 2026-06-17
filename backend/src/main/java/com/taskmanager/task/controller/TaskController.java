package com.taskmanager.task.controller;

import com.taskmanager.common.response.ApiResponse;
import com.taskmanager.task.dto.*;
import com.taskmanager.task.model.TaskPriority;
import com.taskmanager.task.model.TaskStatus;
import com.taskmanager.task.service.TaskService;
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
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.getTasks(currentUser.getId(), projectId, status, priority, search)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(taskService.createTask(currentUser.getId(), projectId, request)));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.getTask(currentUser.getId(), projectId, taskId)));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.updateTask(currentUser.getId(), projectId, taskId, request)));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {
        taskService.deleteTask(currentUser.getId(), projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.updateStatus(currentUser.getId(), projectId, taskId, request.status())));
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> reorderTasks(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID projectId,
            @Valid @RequestBody ReorderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                taskService.reorderTasks(currentUser.getId(), projectId, request.items())));
    }
}
