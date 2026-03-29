package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.UserAlreadyExistsException;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private  PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;    // This is the class we are testing

    private CreateUserRequest createUserRequest;

    @BeforeEach
    void setup(){
        this.createUserRequest = CreateUserRequest.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .password("jd123456")
                .role(UserRole.VIEWER)
                .build();
    }


    @Nested
    @DisplayName("Create User Tests")       // Nested classes are used to organize tests
    class CreateUserTests{

        @Test
        @DisplayName("Should return the user if user is created")
        void createUser_shouldReturnUserProfile_whenUserIsCreated() {
            // arrange
            User savedUser = User.builder()
                    .id(null)
                    .name("John Doe")
                    .email("johndoe1234@gmail.com")
                    .role(UserRole.VIEWER)
                    .password(null)          // service will encode it, so null is fine here
                    .provider(null)
                    .createdAt(LocalDateTime.now())
                    .build();
            when(userRepo.save(any(User.class))).thenReturn(savedUser);

            // act
            UserProfileResponse result = UserServiceTest.this.userService.createUser(createUserRequest);

            // assert
            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("johndoe1234@gmail.com", result.getEmail());

            // verify
            verify(userRepo, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException if email already exists")
        void createUser_shouldThrowException_whenUserAlreadyExists() {
            // arrange
            when(userRepo.findByEmail(createUserRequest.getEmail())).thenThrow(
                    new UserAlreadyExistsException("User with email: " + createUserRequest.getEmail() + " already exists"));

            // act + assert
            UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                    () -> userService.createUser(createUserRequest));

            assertTrue(exception.getMessage().contains("User with email"));
        }
    }
}