package com.akshansh.taskmanagementplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {
    @NotNull
    @NotBlank(message = "Comment content is required")
    @Size(min = 3, max = 1000, message = "Content must be between 3 and 1000 characters")
    private String content;

    @NotNull(message = "User ID is required")
    private Long userId;
}
