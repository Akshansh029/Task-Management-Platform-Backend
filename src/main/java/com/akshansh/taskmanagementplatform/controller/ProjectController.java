package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.response.ProjectDetailsResponse;
import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project Management", description = "APIs for managing projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody CreateProjectRequest request
    ){
        
        ProjectResponse created = projectService.createProject(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String search
    ){
        Page<ProjectResponse> allProjects = projectService.getAllProjects(pageNo, pageSize, search);
        return ResponseEntity.status(HttpStatus.OK).body(allProjects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailsResponse> getProjectById(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId
    ){
        
        ProjectDetailsResponse prjById = projectService.getProjectById(userId, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(prjById);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request)
    {
        
        ProjectResponse updated = projectService.updateProject(userId, projectId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PostMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> addProjectMember(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ){
            projectService.addMemberToProject(userId, projectId, memberId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> removeProjectMember(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ){
            projectService.removeMemberFromProject(userId, projectId, memberId);
            return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<Page<UserProfileResponse>> addProjectMembers(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @RequestBody List<Long> memberIds
    ){
        projectService.addMembersToProject(userId, projectId, memberIds);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<Page<UserProfileResponse>> getProjectMembers(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ){
        Page<UserProfileResponse> members = projectService.getProjectMembers(projectId, pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId
    ){
        
        projectService.deleteProject(userId, projectId);
        return ResponseEntity.noContent().build();
    }
}
