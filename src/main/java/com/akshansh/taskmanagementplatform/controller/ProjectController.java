package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateProjectRequest;
import com.akshansh.taskmanagementplatform.dto.response.ProjectDetailsResponse;
import com.akshansh.taskmanagementplatform.dto.response.ProjectResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@Tag(name = "Project Management", description = "APIs for managing projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }


    @Operation(summary = "Create a new project", description = "Add a new project to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Owner not found",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody CreateProjectRequest request
    ){
        
        ProjectResponse created = projectService.createProject(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(summary = "Get all projects", description = "Retrieve a list of all projects in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String search
    ){
        Page<ProjectResponse> allProjects = projectService.getAllProjects(pageNo, pageSize, search);
        return ResponseEntity.status(HttpStatus.OK).body(allProjects);
    }


    @Operation(summary = "Get project by ID", description = "Retrieve a project's details using their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectDetailsResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailsResponse> getProjectById(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId
    ){
        ProjectDetailsResponse prjById = projectService.getProjectById(userId, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(prjById);
    }


    @Operation(summary = "Update a project", description = "Update an existing project's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request)
    {
        
        ProjectResponse updated = projectService.updateProject(userId, projectId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }


    @Operation(summary = "Add a member to project", description = "Add an user to the project as member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member added successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User/Project not found",
                    content = @Content(schema = @Schema())),
    })
    @PostMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> addProjectMember(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ){
            projectService.addMemberToProject(userId, projectId, memberId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(summary = "Remove member from project", description = "Remove a member from the project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member removed successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User/Project not found",
                    content = @Content(schema = @Schema())),
    })
    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> removeProjectMember(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ){
            projectService.removeMemberFromProject(userId, projectId, memberId);
            return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Add multiple members to project", description = "Add multiple users to the project as members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Members added successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User/Project not found",
                    content = @Content(schema = @Schema())),
    })
    @PostMapping("/{projectId}/members")
    public ResponseEntity<Page<UserProfileResponse>> addProjectMembers(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId,
            @RequestBody List<Long> memberIds
    ){
        projectService.addMembersToProject(userId, projectId, memberIds);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(summary = "Get project members by project ID", description = "Retrieve a list of project members using the project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/{projectId}/members")
    public ResponseEntity<Page<UserProfileResponse>> getProjectMembers(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ){
        Page<UserProfileResponse> members = projectService.getProjectMembers(projectId, pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }


    @Operation(summary = "Delete a project", description = "Delete a project from the system using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId
    ){
        
        projectService.deleteProject(userId, projectId);
        return ResponseEntity.noContent().build();
    }
}
