package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRoleRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.exception.ValidationException;
import com.akshansh.taskmanagementplatform.service.UserService;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<UserProfileResponse>> getAllUserProfiles(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String search
    ){
        Page<UserProfileResponse> profiles = userService.getAllUsers(pageNo, pageSize, search);
        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }


    @Operation(summary = "Get user by ID", description = "Retrieve a user's details using their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
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


    @Operation(summary = "Create a new user", description = "Add a new user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserProfileResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ){

        UserProfileResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @Operation(summary = "Update a user", description = "Update an existing user's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ){
        UserProfileResponse updated = userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }


    @Operation(summary = "Update a user's role", description = "Update an existing user's role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request
    ){
        
        UserProfileResponse updatedRole = userService.updateUserRole(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRole);
    }


    @Operation(summary = "Delete a user", description = "Delete a user from the system using their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema()))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long id
    ){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Create multiple new users", description = "Add multiple new users to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users created successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Unauthorized action",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/bulk-create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> bulkUserCreation(
            @Valid @RequestBody List<CreateUserRequest> users
    ){
        userService.bulkCreateUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
