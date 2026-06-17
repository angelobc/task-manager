package com.taskmanager.task.dto;

import com.taskmanager.task.model.TaskPriority;
import com.taskmanager.task.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        int position,
        UUID assigneeId,
        UUID createdById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
