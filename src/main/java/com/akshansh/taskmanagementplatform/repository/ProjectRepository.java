package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find all Projects
    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.ProjectResponse(" +
            "p.id, p.title, p.description, p.startDate, p.endDate, p.createdAt, p.owner.name, p.owner.email) " +
            "FROM Project p")
    Page<ProjectResponse> findAllProjects(Pageable pageable);
}
