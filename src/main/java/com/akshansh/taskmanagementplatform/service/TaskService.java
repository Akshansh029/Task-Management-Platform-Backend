package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.Task;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.exception.UserNotPartOfProjectException;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.TaskRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.akshansh.taskmanagementplatform.entity.Task.convertToDto;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;

    public TaskService(
            TaskRepository taskRepo,
            UserRepository userRepo,
            ProjectRepository projectRepo
    ){
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request){
        User assignee = userRepo.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + request.getAssigneeId() + " not found"));

        Project prj = projectRepo.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + request.getProjectId() + " not found"));

        // Check if assignee is part of the project or not
        if (!prj.getMembers().contains(assignee))
            throw new UserNotPartOfProjectException("Invalid assignee ID");

        Task newTask = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getDueDate()
        );

        // Set assignee and project
        newTask.setAssignee(assignee);
        newTask.setProject(prj);

        taskRepo.save(newTask);
        return convertToDto(newTask);
    }

    public Page<TaskResponse> getAllTasks(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return taskRepo.findAllTasks(pageable);
    }

    public List<TaskResponse> getAllTasksByAssigneeId(Long assigneeId){
        userRepo.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + assigneeId + " not found"));
        return taskRepo.findAllByAssignee_Id(assigneeId);
    }

    public List<TaskResponse> getAllTasksByProjectId(Long projectId){
        projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID: " + projectId + " not found"));
        return taskRepo.findAllByProject_Id(projectId);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID: " + taskId + " not found"));

        // Updates fields if they are not null or empty.
        if (Objects.nonNull(request.getTitle()) && !"".equalsIgnoreCase(request.getTitle())) {
            task.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getDescription()) && !"".equalsIgnoreCase(request.getDescription())) {
            task.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getStatus())) {
            task.setStatus(request.getStatus());
        }
        if (Objects.nonNull(request.getPriority())) {
            task.setPriority(request.getPriority());
        }
        if (Objects.nonNull(request.getDueDate())) {
            task.setDueDate(request.getDueDate());
        }
        if (Objects.nonNull(request.getAssigneeId())) {
            User newAssignee = userRepo.findById(request.getAssigneeId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "User with ID: " + request.getAssigneeId() + " not found"));
            task.setAssignee(newAssignee);
        }

        taskRepo.save(task);
        return convertToDto(task);
    }

    @Transactional
    public void deleteTask(Long taskId){
        taskRepo.deleteById(taskId);
    }
}
