package com.taskmanager.auth.dto;

import com.taskmanager.user.dto.UserResponse;

public record AuthResponse(
        UserResponse user,
        String accessToken,
        String refreshToken
) {}
