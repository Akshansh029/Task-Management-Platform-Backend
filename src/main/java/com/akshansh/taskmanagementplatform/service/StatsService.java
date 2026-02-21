package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.response.StatsResponse;
import com.akshansh.taskmanagementplatform.entity.TaskStatus;
import com.akshansh.taskmanagementplatform.repository.ProjectRepository;
import com.akshansh.taskmanagementplatform.repository.TaskRepository;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {
    private final ProjectRepository projectRepo;
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    public StatsService(
            ProjectRepository projectRepo,
            TaskRepository taskRepo,
            UserRepository userRepo
    ){
        this.projectRepo = projectRepo;
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public StatsResponse getStats(){
        int projectCount = projectRepo.findAll().size();
        int totalTasks = taskRepo.findAll().size();
        int inProgressTasks = taskRepo.findAll()
                .stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
                .toList()
                .size();
        int totalUsers = userRepo.findAll().size();

        int completedTasks = taskRepo.findAll()
                .stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .toList()
                .size();
        float completionRate =  (float) completedTasks/totalTasks * 100;

        return new StatsResponse(
                projectCount, totalTasks, inProgressTasks, totalUsers, completionRate
        );
    }
}
