package com.akshansh.taskmanagementplatform.dto.request;

import com.akshansh.taskmanagementplatform.entity.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@RequiredArgsConstructor
@Data
public class CreateUserRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email can be max 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 50, message = "Password can be max 50 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
