package com.akshansh.taskmanagementplatform.controller;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.filter.JwtAuthFilter;
import com.akshansh.taskmanagementplatform.service.UserService;
import com.akshansh.taskmanagementplatform.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private UserProfileResponse response;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)       // uses full Spring context
                .defaultRequest(
                        get("/").servletPath("")        // clears any default
                )
                .build();

        this.response = UserProfileResponse.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .ownedProjectsCount(2)
                .build();
    }

    @Nested
    @DisplayName("Get User Profile By Id Tests")
    class GetUserProfileByIdTests{

        @Test
        @DisplayName("Should return 200 OK when Id exists")
        @WithMockUser
        void getUserProfileById_shouldReturn200_whenIdExists() throws Exception {
            when(userService.getUserProfileById(1L)).thenReturn(response);

            mockMvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("johndoe1234@gmail.com"));
        }

        @Test
        @DisplayName("Should return 404 when Id does not exist")
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
        @DisplayName("Should return 201 CREATED when request data is valid")
        void createUser_shouldReturn201_whenRequestIsValid() throws Exception {
            CreateUserRequest request = CreateUserRequest.builder()
                    .name("John Doe")
                    .email("johndoe1234@gmail.com")
                    .password("jd123456")
                    .role(UserRole.MEMBER)
                    .build();

            when(userService.createUser(request)).thenReturn(response);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.email").value("johndoe1234@gmail.com"));
        }

        @Test
        @DisplayName("Should return 400 INVALID when request data is invalid")
        void createUser_shouldReturn400_whenRequestIsInvalid() throws Exception{
            CreateUserRequest request = CreateUserRequest.builder()
                    .name("Ja")
                    .email("johndoe1234@gmail.com")
                    .role(UserRole.MEMBER)
                    .password("jd123456")
                    .build();


            when(userService.createUser(request)).thenReturn(response);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)
                            ))
                    .andExpect(status().is4xxClientError());
        }
    }
}
