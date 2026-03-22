package com.akshansh.taskmanagementplatform.util;

import com.akshansh.taskmanagementplatform.entity.AuthProvider;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import com.akshansh.taskmanagementplatform.service.AuthCodeService;
import com.akshansh.taskmanagementplatform.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userService;
    private final JwtUtil jwtUtil;
    private final AuthCodeService authCodeService;

    @Value("${app.frontend.url}")
    private String frontendAppUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) token.getPrincipal();

        String email = defaultOAuth2User.getAttribute("email");

        UserPrincipal userPrincipal;
        try {
            userPrincipal = (UserPrincipal) userService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            // New user — register them
            User newUser = User.builder()
                    .name(defaultOAuth2User.getAttribute("name"))  // ✅ use "name" not getName()
                    .email(email)
                    .role(UserRole.VIEWER)
                    .provider(AuthProvider.GOOGLE)
                    .createdAt(LocalDateTime.now())
                    .build();
            User savedUser = userRepo.save(newUser);
            userPrincipal = new UserPrincipal(savedUser);
        }

        String accessToken = jwtUtil.generateAccessToken(userPrincipal);
        String refreshToken = jwtUtil.generateRefreshToken(userPrincipal);
        String oneTimeCode = UUID.randomUUID().toString();

        // Store mapping: code → tokens, expires in 2 minutes
        authCodeService.store(oneTimeCode, accessToken, refreshToken);

        // Redirect with just the code — not the token
        response.sendRedirect(frontendAppUrl + "/oauth2/callback?code=" + oneTimeCode);
    }
}
