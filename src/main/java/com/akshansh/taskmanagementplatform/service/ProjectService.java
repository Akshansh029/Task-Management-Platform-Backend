package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateProjectRequest;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectService {
    private final ProjectRepository projectRepo;

    public ProjectService(ProjectRepository projectRepo){
        this.projectRepo = projectRepo;
    }

    public Project createProject(CreateProjectRequest request){
        Project prj = new Project(request.getTitle(), request.getDescription(), request.getStartDate());
        return projectRepo.save(prj);
    }

    public Page<Project> getAllProjects(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return projectRepo.findAll(pageable);
    }
}
