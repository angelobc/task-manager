package com.taskmanager.user.mapper;

import com.taskmanager.user.dto.UserResponse;
import com.taskmanager.user.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserResponse toResponse(User user);
}
