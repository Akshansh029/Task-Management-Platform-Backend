package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRoleRequest;
import com.akshansh.taskmanagementplatform.dto.response.ActiveUserResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.filter.JwtAuthFilter;
import com.akshansh.taskmanagementplatform.security.WithMockUserPrincipal;
import com.akshansh.taskmanagementplatform.service.UserService;
import com.akshansh.taskmanagementplatform.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private HttpSecurity httpSecurity;

    private MockMvc mockMvc;
    private UserProfileResponse createResponse;
    private UserProfileResponse updateResponse;
    private ActiveUserResponse activeUserResponse;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)       // uses full Spring context
                .defaultRequest(
                        get("/").servletPath("")        // clears any default
                )
                .build();

        this.createResponse = UserProfileResponse.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .ownedProjectsCount(2)
                .build();

        this.updateResponse = UserProfileResponse.builder()
                .id(1L)
                .name("John Doe Sr")
                .email("johndoesr1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .ownedProjectsCount(2)
                .build();

        this.activeUserResponse = ActiveUserResponse.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .ownedProjectsCount(1)
                .assignedTasksCount(4)
                .memberOfProjectsCount(2)
                .build();
    }

    @Nested
    @DisplayName("Get User Profile By Id Tests")
    class GetUserProfileByIdTests{

        @Test
        @DisplayName("Get User Profile should return 200 OK when Id exists")
        @WithMockUser
        void getUserProfileById_shouldReturn200_whenIdExists() throws Exception {
            when(userService.getUserProfileById(1L)).thenReturn(createResponse);

            mockMvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("johndoe1234@gmail.com"));
        }

        @Test
        @DisplayName("Get User Profile should return 404 when Id does not exist")
        @WithMockUser
        void getUserProfileById_shouldReturn404_whenIdDoesNotExist() throws Exception {
            when(userService.getUserProfileById(99L))
                    .thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(get("/users/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests{

        @Test
        @DisplayName("Create User should return 201 CREATED when request data is valid")
        void createUser_shouldReturn201_whenRequestIsValid() throws Exception {
            CreateUserRequest request = CreateUserRequest.builder()
                    .name("John Doe")
                    .email("johndoe1234@gmail.com")
                    .password("jd123456")
                    .role(UserRole.MEMBER)
                    .build();

            when(userService.createUser(request)).thenReturn(createResponse);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("johndoe1234@gmail.com"));
        }

        @Test
        @DisplayName("Create User should return 400 INVALID when request data is invalid")
        void createUser_shouldReturn400_whenRequestIsInvalid() throws Exception{
            CreateUserRequest request = CreateUserRequest.builder()
                    .name("Ja")
                    .email("johndoe1234@gmail.com")
                    .role(UserRole.MEMBER)
                    .password("jd123456")
                    .build();


            when(userService.createUser(request)).thenReturn(createResponse);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)
                            ))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests{

        @Test
        @DisplayName("Update User should return 200 when request data is valid")
        @WithMockUserPrincipal(id = 1L, name = "John Doe", email = "johndoe1234@gmail.com", role = "MEMBER")
        void updateUser_shouldReturn200_whenRequestIsValid() throws Exception {
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .name("John Doe Sr")
                    .email("johndoesr1234@gmail.com")
                    .build();

            when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                    .thenReturn(updateResponse);

            mockMvc.perform(put("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John Doe Sr"))
                    .andExpect(jsonPath("$.email").value("johndoesr1234@gmail.com"));
        }

        @Test
        @DisplayName("Update User should return 400 when request data is invalid")
        @WithMockUserPrincipal(id = 1L, name = "John Doe", email = "johndoe1234@gmail.com", role = "MEMBER")
        void updateUser_shouldReturn400_whenRequestIsInvalid() throws Exception {
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .name("Ja")                      // too short — triggers @Valid
                    .email("johndoesr1234@gmail.com")
                    .build();

            // No stub needed here — @Valid rejects the request before the service is called
            mockMvc.perform(put("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUserPrincipal(id = 1L)  // logged in as user 1
        @DisplayName("Should return 403 when user tries to update another user")
        void updateUser_shouldReturn403_whenUserUpdatesOtherProfile() throws Exception {
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .name("Hacker")
                    .email("hacker@gmail.com")
                    .build();

            when(userService.updateUser(2L, request))
                    .thenThrow(new ForbiddenException("Only admins or user themselves can update"));

            mockMvc.perform(put("/users/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Update User Role Tests")
    class UpdateUserRoleTests{

        @Test
        @DisplayName("Update user role should return 200 OK when request is valid")
        void updateUserRole_shouldReturn200_whenRequestIsValid() throws Exception {
            UpdateUserRoleRequest request = UpdateUserRoleRequest.builder()
                    .role(UserRole.MEMBER)
                    .build();

            when(userService.updateUserRole(eq(1L), any(UpdateUserRoleRequest.class)))
                    .thenReturn(createResponse);

            mockMvc.perform(patch("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value(UserRole.MEMBER.name())); // enum → string
        }

        @Test
        @DisplayName("Update user role should return 404 NOT FOUND when user does not exists")
        void updateUserRole_shouldReturn404_whenUserDoesNotExists() throws Exception {
            UpdateUserRoleRequest request = UpdateUserRoleRequest.builder()
                    .role(UserRole.MEMBER)
                    .build();

            when(userService.updateUserRole(eq(1L), any(UpdateUserRoleRequest.class)))
                    .thenThrow(new ResourceNotFoundException("User with ID: 1 not found"));

            mockMvc.perform(patch("/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.name").doesNotExist());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests{

        @Test
        @DisplayName("Delete User should return 204")
        void deleteUser_shouldReturn204_whenUserExists() throws Exception{
            userService.deleteUser(1L);

            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Get Active User Details Tests")
    class GetActiveUserDetailsTest{

        @Test
        @DisplayName("Get Active User Details should return 200 when user is authenticated")
        void getActiveUserDetails_shouldReturn200_whenUserIsAuthenticated() throws Exception {
            when(userService.getActiveUserDetails()).thenReturn(activeUserResponse);

            mockMvc.perform(get("/users/me"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.role").value("MEMBER"))
                    .andExpect(jsonPath("$.assignedTasksCount").value(4));

        }

        @Test
        @DisplayName("Get Active User Details should return 401 when user is unauthenticated")
        void getActiveUserDetails_shouldReturn401_whenUserIsUnauthenticated() throws Exception {
            when(userService.getActiveUserDetails())
                    .thenThrow(new AuthenticationCredentialsNotFoundException("No authenticated user found"));

            mockMvc.perform(get("/users/me"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.name").doesNotExist());

        }
    }
}
