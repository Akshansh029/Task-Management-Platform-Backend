package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.Comment;
import com.akshansh.taskmanagementplatform.entity.Label;
import com.akshansh.taskmanagementplatform.entity.TaskPriority;
import com.akshansh.taskmanagementplatform.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskByIdResponse{
    private Long id;
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime dueDate;

    private String assigneeName;

    private String projectTitle;

    private List<Comment> comments;

    private Set<Label> labels;
}
