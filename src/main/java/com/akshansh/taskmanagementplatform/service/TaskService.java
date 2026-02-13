package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateTaskRequest;
import com.akshansh.taskmanagementplatform.dto.response.TaskResponse;
import com.akshansh.taskmanagementplatform.entity.Project;
import com.akshansh.taskmanagementplatform.entity.Task;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.TaskRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Task newTask = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getDueDate()
        );

        newTask.setAssignee(assignee);
        newTask.setProject(prj);

        taskRepo.save(newTask);
        return convertToDto(newTask);
    }
}
