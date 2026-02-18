package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.Task;
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
    private Long id;
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    private Long assigneeId;

    private String assigneeName;

    private Long projectId;

    private String projectTitle;

    public static TaskResponse convertToDto(Task t){
        return new TaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getCreatedAt(),
                t.getDueDate(),
                t.getAssignee().getId(),
                t.getAssignee().getName(),
                t.getProject().getId(),
                t.getProject().getTitle()
        );
    }
}
