package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.TaskPriority;
import com.akshansh.taskmanagementplatform.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    private String assigneeName;

    private String projectTitle;
}
