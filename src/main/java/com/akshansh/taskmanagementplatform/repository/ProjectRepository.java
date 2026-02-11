package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
