package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.response.ProjectDetailsResponse;
import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.akshansh.taskmanagementplatform.dto.response.ProjectDetailsResponse.convertToDetailedDto;
import static com.akshansh.taskmanagementplatform.entity.Project.convertToDto;

@Service
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    public ProjectService(ProjectRepository projectRepo, UserRepository userRepo){
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    public boolean isNotOwnerOrAdmin(Long userId, Long projectId){
        UserProfileResponse user = userRepo.findAllUserProfileById(userId);
        Project prj = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + projectId + " not found"));

        // Check for project owner or admin
        return user == null || (user.getRole() != UserRole.ADMIN && !prj.getOwner().getId().equals(userId));
    }

    public boolean isViewer(Long userId){
        UserProfileResponse user = userRepo.findAllUserProfileById(userId);

        return user.getRole() == UserRole.VIEWER;
    }

    public boolean isProjectMember(Long userId, Long projectId){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " not found"));
        Project prj = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + projectId + " not found"));

        return prj.getMembers().stream().toList().contains(user);
    }

    @Transactional
    public ProjectResponse createProject(Long userId, CreateProjectRequest request){
        if(isViewer(userId))
            throw new ForbiddenException("Only admins and members can create projects");

        User u = userRepo.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project prj = new Project(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate()
        );
        prj.setOwner(u);
        prj.addMember(u);

        projectRepo.save(prj);
        return convertToDto(prj);
    }

    public Page<ProjectResponse> getAllProjects(int pageNo, int pageSize, String search){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        if(search == null || search.isBlank()){
            return projectRepo.findAllProjects(pageable);
        }

        return projectRepo.findAllProjects(search, pageable);
    }

    public ProjectDetailsResponse getProjectById(Long userId, Long projectId){
        if(!isProjectMember(userId, projectId))
            throw new ForbiddenException("Only project members can view project details");

        Project prj = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + projectId + " not found"));
        return convertToDetailedDto(prj);
    }

    @Transactional
    public ProjectResponse updateProject(Long userId, Long projectId, UpdateProjectRequest request){
        if(isNotOwnerOrAdmin(userId, projectId))
            throw new ForbiddenException("Only admins/owner can update projects");

        Project prj = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Updates fields if they are not null or empty.
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
    public void addMemberToProject(Long userId, Long projectId, Long memberId){
        if(isNotOwnerOrAdmin(userId, projectId))
            throw new ForbiddenException("Only admins/owner can update projects");

        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + memberId + " not found"));

        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Add the member (this updates both sides)
        p.addMember(u);

        projectRepo.save(p);
    }

    @Transactional
    public void addMembersToProject(Long userId, Long projectId, List<Long> memberIds){
        if(isNotOwnerOrAdmin(userId, projectId))
            throw new ForbiddenException("Only admins/owner can update projects");

        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        for(Long memberId : memberIds){
            User u = userRepo.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + memberId + " not found"));

            p.addMember(u);
        }

        projectRepo.save(p);
    }

    @Transactional
    public void removeMemberFromProject(Long userId, Long projectId, Long memberId){
        if(isNotOwnerOrAdmin(userId, projectId))
            throw new ForbiddenException("Only admins/owner can update projects");

        User u = userRepo.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + memberId + " not found"));

        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

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
    public void deleteProject(Long userId, Long projectId){
        if(isNotOwnerOrAdmin(userId, projectId))
            throw new ForbiddenException("Only admins/owner can update projects");

        projectRepo.deleteById(projectId);
    }
}
