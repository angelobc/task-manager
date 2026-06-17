package com.taskmanager.project.repository;

import com.taskmanager.project.model.Project;
import com.taskmanager.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByOwnerOrderByCreatedAtDesc(User owner);
}
