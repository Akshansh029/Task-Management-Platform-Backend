package com.akshansh.taskmanagementplatform.dto.request;

import com.akshansh.taskmanagementplatform.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {
    @NotBlank(message = "Role is required")
    private UserRole role;
}
