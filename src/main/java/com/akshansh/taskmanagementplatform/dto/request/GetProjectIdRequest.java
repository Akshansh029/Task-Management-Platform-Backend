package com.akshansh.taskmanagementplatform.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetProjectIdRequest {
    @NotNull(message = "Project ID is required")
    @Size(min = 1, max = 1)
    private Long projectId;
}
