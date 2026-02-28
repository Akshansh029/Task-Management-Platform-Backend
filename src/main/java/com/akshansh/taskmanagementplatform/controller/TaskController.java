package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.GetProjectIdRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskStatusRequest;
import com.akshansh.taskmanagementplatform.dto.response.TaskByIdResponse;
import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import com.akshansh.taskmanagementplatform.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }


    @Operation(summary = "Create a new task", description = "Add a new task to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/projects/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request
    ){
        TaskResponse created = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(summary = "Get all tasks", description = "Retrieve a list of all tasks in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action"),
            @ApiResponse(responseCode = "404", description = "User/Project not found",
                content = @Content(schema = @Schema())),
    })
    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ){
        Page<TaskResponse> tasks = taskService.getAllTasks(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }


    @Operation(summary = "Get task by ID", description = "Retrieve a task's details using its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskByIdResponse.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action"),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskByIdResponse> getTaskById(
            @PathVariable Long taskId,
            @Valid @RequestBody GetProjectIdRequest request
            ){
        TaskByIdResponse task = taskService.getTaskById(request.getProjectId(), taskId);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }


    @Operation(summary = "Get all tasks by project", description = "Retrieve a list tasks for a project using project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasksByProjectId(
            @PathVariable Long projectId
    ){
        List<TaskResponse> tasksByProject = taskService.getAllTasksByProjectId(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(tasksByProject);
    }


    @Operation(summary = "Get all tasks by assignee", description = "Retrieve a list tasks for a particular user using user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/assignee/{assigneeId}/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasksByAssigneeId(
            @Valid @RequestBody GetProjectIdRequest request,
            @PathVariable Long assigneeId
    ){
        List<TaskResponse> tasksForAssignee =
                taskService.getAllTasksByAssigneeId(request.getProjectId(), assigneeId);

        return ResponseEntity.status(HttpStatus.OK).body(tasksForAssignee);
    }


    @Operation(summary = "Update a task", description = "Update an existing task's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request)
    {
        TaskResponse updated = taskService.updateTask(taskId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }


    @Operation(summary = "Assign task to user", description = "Assign a task to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task assigned successfully",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "User/Project not found",
                    content = @Content(schema = @Schema())),
    })
    @PatchMapping("/tasks/{taskId}/assign/{userId}")
    public ResponseEntity<Void> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId
    ){
        taskService.assignTaskToUser(taskId, userId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update a task's status", description = "Update an existing task's status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ){
        taskService.updateTaskStatus(taskId, request);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Delete a task", description = "Delete a task from the system using their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Task/Project not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @Valid @RequestBody GetProjectIdRequest request,
            @PathVariable Long taskId
    ){
        taskService.deleteTask(request.getProjectId(), taskId);
        return ResponseEntity.noContent().build();
    }
}
