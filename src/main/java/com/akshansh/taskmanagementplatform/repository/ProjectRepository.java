package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find all Projects
    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.ProjectResponse(" +
            "p.id, p.title, p.startDate, p.endDate, p.createdAt, p.owner.name, SIZE(p.members), SIZE(p.tasks)) " +
            "FROM Project p WHERE (:search IS NULL OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.owner.name) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<ProjectResponse> findAllProjects(Pageable pageable, String search);
}
