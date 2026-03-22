package com.akshansh.taskmanagementplatform.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${app.frontend.url}")
    private String frontendAppUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        String errorMessage = "Authentication failed";
        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            OAuth2Error error = oauth2Exception.getError();
            errorMessage = switch (error.getErrorCode()) {
                case "invalid_token" -> "Your session has expired. Please log in again.";
                case "access_denied" -> "Access was denied. Please check your permissions.";
                case "invalid_client" -> "Application configuration error. Please contact support.";
                case "server_error" -> "The login provider is experiencing issues. Try again later.";
                default -> "Login failed: " + error.getDescription();
            };
        }

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect(frontendAppUrl + "/login?error=" + encodedMessage);
    }
}
