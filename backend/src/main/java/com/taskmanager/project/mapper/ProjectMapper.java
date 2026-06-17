package com.taskmanager.project.mapper;

import com.taskmanager.project.dto.ProjectResponse;
import com.taskmanager.project.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProjectMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    ProjectResponse toResponse(Project project);
}
