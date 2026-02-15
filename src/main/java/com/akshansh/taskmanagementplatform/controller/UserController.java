package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.exception.ValidationException;
import com.akshansh.taskmanagementplatform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> getAllUserProfiles(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ){
        Page<UserProfileResponse> profiles = userService.getAllUsers(pageNo, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long id){
        if(id == null){
            throw new ValidationException("Type of id must be Long");
        }
        UserProfileResponse profile = userService.getUserProfileById(id);
        if (profile == null){
            throw new ResourceNotFoundException("User not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> createUser(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody CreateUserRequest request
    ){
        UserProfileResponse created = userService.createUser(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request){
        UserProfileResponse updated = userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long id
    ){
        userService.deleteUser(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk-create")
    public ResponseEntity<Void> bulkUserCreation(
            @RequestHeader("X-User-ID") Long userId,
            @Valid @RequestBody List<CreateUserRequest> users
    ){
        UserProfileResponse user = userService.getUserProfileById(userId);
        if(user.getRole() != UserRole.ADMIN){
            throw new ForbiddenException("Only admins can create users");
        }
        userService.bulkCreateUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
