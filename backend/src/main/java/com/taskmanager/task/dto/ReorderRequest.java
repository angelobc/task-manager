package com.taskmanager.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReorderRequest(
        @NotEmpty @Valid List<ReorderItem> items
) {
    public record ReorderItem(
            @NotNull UUID id,
            @NotNull Integer position
    ) {}
}
