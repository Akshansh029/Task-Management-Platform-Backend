package com.akshansh.taskmanagementplatform.dto.request;

import com.akshansh.taskmanagementplatform.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {
    private UserRole role;
}
