package com.akshansh.taskmanagementplatform.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @Size(max = 1000, message = "Description can be max 1000 characters")
    @NotBlank(message = "Description is required")
    @Column(name = "description")
    private String description;

    @NotNull(message = "Start data cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
}
