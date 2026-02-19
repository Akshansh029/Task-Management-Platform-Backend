package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskStatusRequest;
import com.akshansh.taskmanagementplatform.dto.response.TaskByIdResponse;
import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.Task;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.exception.InvalidTaskDueDate;
import org.springframework.data.domain.Page;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.exception.UserNotPartOfProjectException;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.TaskRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.akshansh.taskmanagementplatform.dto.response.TaskResponse.convertToDto;
import static com.akshansh.taskmanagementplatform.entity.Task.convertToTaskByIdDto;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final ProjectService projectService;

    public TaskService(
            TaskRepository taskRepo,
            UserRepository userRepo,
            ProjectRepository projectRepo,
            ProjectService projectService
    ){
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
        this.projectService = projectService;
    }

    @Transactional
    public TaskResponse createTask(Long userId, CreateTaskRequest request){
        if(!projectService.isProjectMember(userId, request.getProjectId())){
            throw new ForbiddenException("Only project members can create tasks");
        }

        User creator = userRepo.findById(userId).get();

        Project prj = projectRepo.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + request.getProjectId() + " not found"));

        if(request.getDueDate().isAfter(prj.getEndDate())){
            throw new InvalidTaskDueDate("Task's due date cannot be greater than project's end date");
        }

        Task newTask = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getDueDate()
        );

        // Set project
        newTask.setProject(prj);
        newTask.setAssignee(creator);

        taskRepo.save(newTask);
        return convertToDto(newTask);
    }

    public Page<TaskResponse> getAllTasks(Long userId, Long projectId, int pageNo, int pageSize){
        if(!projectService.isProjectMember(userId, projectId)){
            throw new ForbiddenException("Only project members can view tasks");
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return taskRepo.findAllTasks(pageable);
    }

    public TaskByIdResponse getTaskById(Long userId, Long projectId, Long taskId){
        if(!projectService.isProjectMember(userId, projectId)){
            throw new ForbiddenException("Only project members can view tasks");
        }

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));
        return convertToTaskByIdDto(task);
    }

    public List<TaskResponse> getAllTasksByAssigneeId(Long userId, Long projectId, Long assigneeId){
        if(!projectService.isProjectMember(userId, projectId)){
            throw new ForbiddenException("Only project members can view tasks");
        }

        userRepo.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + assigneeId + " not found"));
        return taskRepo.findAllByAssignee_Id(assigneeId);
    }

    public List<TaskResponse> getAllTasksByProjectId(Long userId, Long projectId){
        if(!projectService.isProjectMember(userId, projectId)){
            throw new ForbiddenException("Only project members can view tasks");
        }

        projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + projectId + " not found"));
        return taskRepo.findAllByProject_Id(projectId);
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, UpdateTaskRequest request){
        if(projectService.isNotOwnerOrAdmin(userId, request.getProjectId())){
            throw new ForbiddenException("Only admin/project owner can update tasks");
        }

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));

        if(!task.getProject().getId().equals(request.getProjectId()))
            throw new ForbiddenException("Task must be of given project");

        // Updates fields if they are not null or empty.
        if (Objects.nonNull(request.getTitle()) && !"".equalsIgnoreCase(request.getTitle())) {
            task.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getDescription()) && !"".equalsIgnoreCase(request.getDescription())) {
            task.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getPriority())) {
            task.setPriority(request.getPriority());
        }
        if (Objects.nonNull(request.getDueDate())) {
            task.setDueDate(request.getDueDate());
        }

        taskRepo.save(task);
        return convertToDto(task);
    }

    @Transactional
    public void assignTaskToUser(Long taskId, Long userId) {
        User assignee = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " not found"));

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));

        if(!task.getProject().getMembers().contains(assignee)){
            throw new UserNotPartOfProjectException("Assignee is not part of the project");
        }

        task.setAssignee(assignee);
        taskRepo.save(task);
    }

    @Transactional
    public void updateTaskStatus(Long userId, Long taskId, UpdateTaskStatusRequest request) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));

        // Check for assignee
        if(!task.getAssignee().getId().equals(userId)){
            throw new ForbiddenException("Only task assignee can update task status");
        }

        task.setStatus(request.getStatus());
        taskRepo.save(task);
    }

    @Transactional
    public void deleteTask(Long userId, Long projectId, Long taskId){
        if(projectService.isNotOwnerOrAdmin(userId, projectId)){
            throw new ForbiddenException("Only admin/project owner can delete tasks");
        }
        taskRepo.deleteById(taskId);
    }
}
