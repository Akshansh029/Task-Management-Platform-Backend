package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request){
        ProjectResponse created = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ){
        Page<ProjectResponse> allProjects = projectService.getAllProjects(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(allProjects);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestBody UpdateProjectRequest request)
    {
        ProjectResponse updated = projectService.updateProject(projectId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> addProjectMember(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ){
            projectService.addMemberToProject(projectId, userId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeProjectMember(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ){
            projectService.removeMemberFromProject(projectId, userId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<Page<UserProfileResponse>> addProjectMembers(
            @PathVariable Long projectId,
            @RequestBody List<Long> userIds
    ){
        projectService.addMembersToProject(projectId, userIds);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
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
    public ResponseEntity<Void> updateProject(@PathVariable Long projectId){
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
