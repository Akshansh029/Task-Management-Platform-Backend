package com.akshansh.taskmanagementplatform.dto.response;

import com.akshansh.taskmanagementplatform.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private int ownedProjectsCount;
}
