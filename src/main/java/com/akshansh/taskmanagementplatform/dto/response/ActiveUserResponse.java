package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private int ownedProjectsCount;
    private int assignedTasksCount;
    private int memberOfProjectsCount;
}
