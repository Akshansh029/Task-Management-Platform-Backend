package com.akshansh.taskmanagementplatform.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLabelRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color")
    @Size(min = 7, max = 7, message = "Must be valid hex color code")
    private String color;
}
