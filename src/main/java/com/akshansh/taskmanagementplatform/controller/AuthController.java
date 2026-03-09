package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.LoginRequest;
import com.akshansh.taskmanagementplatform.dto.request.RegisterUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.LoginResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "APIs for authentication")
public class AuthController {
    private final AuthService authService;


    @Operation(summary = "Register the user", description = "Register the user and add details in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request
    ){
        UserProfileResponse newUser = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }


    @Operation(summary = "Login the user", description = "Sign in the user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        com.akshansh.taskmanagementplatform.dto.response.LoginResponse loginResp = authService.loginUser(request);

        Cookie cookie = new Cookie("refreshToken", loginResp.getRefreshToken());
        cookie.setHttpOnly(true);       // http-only cookie
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(loginResp);
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request){

        String refreshToken = Arrays.stream(request.getCookies()) //getCookies() method returns a array of cookie
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()-> new AuthenticationServiceException("RefreshToken not found"));
        LoginResponse loginResponseDto = authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }
}
