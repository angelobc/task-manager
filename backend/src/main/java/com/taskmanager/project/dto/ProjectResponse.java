package com.taskmanager.project.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        String color,
        boolean archived,
        UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
