package com.akshansh.taskmanagementplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {

    private int totalProjects;
    private int totalTasks;
    private int inProgressTasks;
    private int totalUsers;
    private float taskCompletionRate;
}
