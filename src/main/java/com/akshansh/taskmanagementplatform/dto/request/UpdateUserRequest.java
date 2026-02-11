package com.akshansh.taskmanagementplatform.dto.request;

import com.akshansh.taskmanagementplatform.entity.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class UpdateUserRequest {
    @Column(name = "name")
    private String name;

    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;
}
