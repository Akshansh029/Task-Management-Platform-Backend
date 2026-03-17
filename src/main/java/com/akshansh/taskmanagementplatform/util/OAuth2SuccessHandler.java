package com.akshansh.taskmanagementplatform.util;

import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import com.akshansh.taskmanagementplatform.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userService;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) token.getPrincipal();

        String email = defaultOAuth2User.getAttribute("email");
        UserPrincipal user = (UserPrincipal) userService.loadUserByUsername(email);


    }
}
