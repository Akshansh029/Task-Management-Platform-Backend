package com.akshansh.taskmanagementplatform.dto.request;

import jakarta.persistence.Column;
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
public class CreateProjectRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description can be max 1000 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Start data cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "End data cannot be null")
    private LocalDateTime endDate;

    @NotNull(message = "Owner Id cannot be null")
    private Long ownerId;
}
