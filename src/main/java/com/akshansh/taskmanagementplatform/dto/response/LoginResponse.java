package com.akshansh.taskmanagementplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
}
