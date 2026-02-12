package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.akshansh.taskmanagementplatform.entity.Project.convertToDto;

@Repository
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    public ProjectService(ProjectRepository projectRepo, UserRepository userRepo){
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request){
        User u = userRepo.findById(request.getOwner_id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project prj = new Project(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate()
        );
        prj.setOwner(u);

        projectRepo.save(prj);
        return convertToDto(prj);
    }

    public Page<ProjectResponse> getAllProjects(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return projectRepo.findAllProjects(pageable);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, UpdateProjectRequest request){
        Project prj = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));


        if (Objects.nonNull(request.getTitle()) && !"".equalsIgnoreCase(request.getTitle())) {
            prj.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getDescription()) && !"".equalsIgnoreCase(request.getDescription())) {
            prj.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getStartDate())) {
            prj.setStartDate(request.getStartDate());
        }
        if (Objects.nonNull(request.getEndDate())) {
            prj.setEndDate(request.getEndDate());
        }

        projectRepo.save(prj);
        return convertToDto(prj);
    }

    @Transactional
    public void addMemberToProject(Long projectId, Long userId){
        User u = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project p = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Add the member (this updates both sides)
        p.addMember(u);

        projectRepo.save(p);
    }

    @Transactional
    public void removeMemberFromProject(Long projectId, Long userId){
        User u = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project p = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        p.removeMember(u);

        projectRepo.save(p);
    }

    public Page<UserProfileResponse> getProjectMembers(Long projectId, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return projectRepo.findByIdWithMembers(projectId, pageable);
    }

    @Transactional
    public void deleteProject(Long projectId){
        projectRepo.deleteById(projectId);
    }
}
