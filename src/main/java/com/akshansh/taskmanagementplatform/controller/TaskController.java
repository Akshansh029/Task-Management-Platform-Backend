package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskStatusRequest;
import com.akshansh.taskmanagementplatform.dto.response.TaskByIdResponse;
import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import com.akshansh.taskmanagementplatform.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @PostMapping("/api/projects/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody CreateTaskRequest request
    ){
        TaskResponse created = taskService.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-Project-ID") Long projectId,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ){
        Page<TaskResponse> tasks = taskService.getAllTasks(userId, projectId, pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/api/tasks/{taskId}")
    public ResponseEntity<TaskByIdResponse> getTaskById(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-Project-ID") Long projectId,
            @PathVariable Long taskId
    ){
        TaskByIdResponse task = taskService.getTaskById(userId, projectId, taskId);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasksByProjectId(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long projectId
    ){
        
        List<TaskResponse> tasksByProject = taskService.getAllTasksByProjectId(userId, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(tasksByProject);
    }

    @GetMapping("/api/assignee/{assigneeId}/tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasksByAssigneeId(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-Project-ID") Long projectId,
            @PathVariable Long assigneeId
    ){
        List<TaskResponse> tasksForAssignee =
                taskService.getAllTasksByAssigneeId(userId, projectId, assigneeId);

        return ResponseEntity.status(HttpStatus.OK).body(tasksForAssignee);
    }

    @PutMapping("/api/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request)
    {
        
        TaskResponse updated = taskService.updateTask(userId, taskId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/api/tasks/{taskId}/assign/{userId}")
    public ResponseEntity<Void> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId
    ){
        taskService.assignTaskToUser(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/tasks/{taskId}/status")
    public ResponseEntity<Void> updateTaskStatus(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request
    ){
        
        taskService.updateTaskStatus(userId, taskId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-Project-ID") Long projectId,
            @PathVariable Long taskId
    ){
        taskService.deleteTask(userId, projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}
