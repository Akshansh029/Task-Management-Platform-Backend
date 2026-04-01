package com.akshansh.taskmanagementplatform.util;

import com.akshansh.taskmanagementplatform.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Pick the most specific exception message available
        Exception cause = (Exception) request.getAttribute("jwt_exception");
        if (cause == null) {
            cause = (Exception) request.getAttribute("username_not_found_exception");
        }

        String message = cause != null ? cause.getMessage() : authException.getMessage();
        String errorType = cause != null ? cause.getClass().getSimpleName() : "AuthenticationException";

        // Write exactly ONE response
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                errorType,
                message,
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
