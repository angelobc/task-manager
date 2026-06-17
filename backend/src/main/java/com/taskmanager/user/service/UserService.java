package com.taskmanager.user.service;

import com.taskmanager.common.exception.ResourceNotFoundException;
import com.taskmanager.user.dto.ChangePasswordRequest;
import com.taskmanager.user.dto.UpdateUserRequest;
import com.taskmanager.user.dto.UserResponse;
import com.taskmanager.user.mapper.UserMapper;
import com.taskmanager.user.model.User;
import com.taskmanager.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        return userMapper.toResponse(findUserOrThrow(userId));
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = findUserOrThrow(userId);
        user.setFullName(request.fullName());
        user.setAvatarUrl(request.avatarUrl());
        return userMapper.toResponse(userRepository.save(user));
    }

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = findUserOrThrow(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
