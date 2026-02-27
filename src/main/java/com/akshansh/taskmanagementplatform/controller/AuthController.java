package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import com.akshansh.taskmanagementplatform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
//            @Valid @RequestBody CreateUserRequest request
            @RequestParam String rawPassword
    ){
//        UserProfileResponse newUser = authService.registerUser(request);
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        return ResponseEntity.status(HttpStatus.OK).body(encryptedPassword);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginUser(
            @Valid @RequestBody CreateUserRequest request
    ){
//        UserProfileResponse newUser = authService.registerUser(request);
        return ResponseEntity.noContent().build();
    }
}
