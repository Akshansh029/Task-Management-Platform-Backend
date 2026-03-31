package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
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
    private UserProfileResponse userProfileResponse;

    @BeforeEach
    void setup(){
        this.createUserRequest = CreateUserRequest.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .password("jd123456")
                .role(UserRole.VIEWER)
                .build();

        this.userProfileResponse = UserProfileResponse.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.VIEWER)
                .createdAt(LocalDateTime.now())
                .ownedProjectsCount(2)
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
            verify(userRepo, times(1)).findByEmail(createUserRequest.getEmail());
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

            verify(userRepo, times(1)).findByEmail(createUserRequest.getEmail());
        }
    }

    @Nested
    @DisplayName("Get User Profile By Id Tests")
    class GetUserProfileByIdTests{

        @Test
        @DisplayName("Should return User Profile if user exists")
        void getUserProfileByTest_shouldReturnProfile_whenUserExists() {
            // arrange
            when(userRepo.findUserProfileById(1L)).thenReturn(userProfileResponse);

            // act
            UserProfileResponse result = userService.getUserProfileById(1L);

            // assert
            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals(UserRole.VIEWER, result.getRole());

            // verify
            verify(userRepo, times(1)).findUserProfileById(1L);
        }

        @Test
        @DisplayName("Should throw exception if user does not exists")
        void getUserProfileByTest_shouldThrowException_whenUserDoesNotExists() {
            // arrange
            when(userRepo.findUserProfileById(1L)).thenThrow(new ResourceNotFoundException("User not found"));

            // act + assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserProfileById(1L));

            assertTrue(exception.getMessage().contains("User not found"));
        }
    }
}