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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.akshansh.taskmanagementplatform.entity.Project.convertToDto;

@Service
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    public ProjectService(ProjectRepository projectRepo, UserRepository userRepo){
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request){
        User u = userRepo.findById(request.getOwnerId())
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
        User u = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " not found"));

        Project p = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Add the member (this updates both sides)
        p.addMember(u);

        projectRepo.save(p);
    }

    @Transactional
    public void addMembersToProject(Long projectId, List<Long> userIds){
        Project p = projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        for(Long userId : userIds){
            User u = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " not found"));

            p.addMember(u);
        }

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

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<UserProfileResponse> members = project.getMembers()
                .stream()
                .map(User::convertToDto)
                .sorted()
                .toList();

        return new PageImpl<>(members, pageable, members.size());
    }

    @Transactional
    public void deleteProject(Long projectId){
        projectRepo.deleteById(projectId);
    }
}
