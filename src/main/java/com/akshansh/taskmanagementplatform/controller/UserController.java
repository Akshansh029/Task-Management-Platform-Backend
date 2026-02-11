package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ValidationException;
import com.akshansh.taskmanagementplatform.service.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        if(id == null){
            throw new ValidationException("Type of id must be Long");
        }
        User user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles(){
        List<UserProfileResponse> profiles = userService.getAllUserProfiles();
        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long id){
        if(id == null){
            throw new ValidationException("Type of id must be Long");
        }
        UserProfileResponse profile = userService.getUserProfileById(id);
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request){
        if(request.getName().isEmpty() || request.getEmail().isEmpty() || request.getRole() == null){
            throw new ValidationException("Required fields of author are not valid");
        }
        User created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
