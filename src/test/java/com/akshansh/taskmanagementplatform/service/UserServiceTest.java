package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRoleRequest;
import com.akshansh.taskmanagementplatform.dto.response.ActiveUserResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.*;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.exception.UserAlreadyExistsException;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

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
    private User user;
    private UpdateUserRequest updateUserRequest;
    private UpdateUserRoleRequest updateUserRoleRequest;
    private ActiveUserResponse activeUserResponse;

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

        this.user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .ownedProjects(new HashSet<>())
                .assignedTasks(new HashSet<>())
                .projects(new HashSet<>())
                .build();

        UserPrincipal principal = new UserPrincipal(user);

        var authToken = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        this.updateUserRequest = UpdateUserRequest.builder()
                .name("John Doe Sr")
                .email("johndoesr1234@gmail.com")
                .build();

        this.updateUserRoleRequest = UpdateUserRoleRequest.builder()
                .role(UserRole.VIEWER)
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

    @AfterEach
    void cleanup(){
        SecurityContextHolder.clearContext();
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
        void getUserProfileByIdTest_shouldReturnProfile_whenUserExists() {
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

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests{

        @Test
        @DisplayName("Should update name and email when both fields are provided")
        void updateUser_shouldUpdateNameAndEmail_whenBothFieldsProvided(){
            // arrange
            when(userRepo.findById(1L)).thenReturn(Optional.of(user));

            // act
            UserProfileResponse result = userService.updateUser(1L, updateUserRequest);

            // assert
            assertNotNull(result);
            assertEquals("John Doe Sr", result.getName());
            assertEquals("johndoesr1234@gmail.com", result.getEmail());

            // verify
            verify(userRepo, times(1)).findById(1L);
            verify(userRepo, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should update name when email is null or blank")
        void updateUser_shouldUpdateOnlyName_whenEmailIsNullOrBlank() {
            UpdateUserRequest nameOnlyReq = UpdateUserRequest.builder()
                    .name("John Doe Sr")
                    .email(null)
                    .build();
            when(userRepo.findById(1L)).thenReturn(Optional.of(user));

            UserProfileResponse result = userService.updateUser(1L, nameOnlyReq);

            assertNotNull(result);
            assertEquals("John Doe Sr", result.getName());
            assertEquals("johndoe1234@gmail.com", result.getEmail());

            verify(userRepo, times(1)).findById(1L);
            verify(userRepo, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should update email when name is null or blank")
        void updateUser_shouldUpdateOnlyEmail_whenNameIsNullOrBlank() {
            UpdateUserRequest emailOnlyReq = UpdateUserRequest.builder()
                    .name(null)
                    .email("johndoesr1234@gmail.com")
                    .build();
            when(userRepo.findById(1L)).thenReturn(Optional.of(user));

            UserProfileResponse result = userService.updateUser(1L, emailOnlyReq);

            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("johndoesr1234@gmail.com", result.getEmail());

            verify(userRepo, times(1)).findById(1L);
            verify(userRepo, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should Throw Forbidden Exception when a user tries to update another user")
        void updateUser_shouldThrowForbiddenException_whenUserIsNotOwner() {
            ForbiddenException exception = assertThrows(ForbiddenException.class, () -> userService.updateUser(2L, updateUserRequest));

            assertTrue(exception.getMessage().contains("update their profile"));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exists")
        void updateUser_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {
            when(userRepo.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateUserRequest));

            assertTrue(exception.getMessage().contains("User not found"));

            verify(userRepo, times(1)).findById(1L);
            verify(userRepo, times(0)).save(any(User.class));
        }

    }

    @Nested
    @DisplayName("Update User's Role Tests")
    class UpdateUserRoleTests{

        @Test
        @DisplayName("Should update user's role when user exists")
        void updateUserRole_shouldUpdateUserRole_whenUserExists() {
            when(userRepo.findById(1L)).thenReturn(Optional.of(user));

            UserProfileResponse result = userService.updateUserRole(1L, updateUserRoleRequest);

            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("VIEWER", result.getRole().toString());

            verify(userRepo, times(1)).findById(1L);
            verify(userRepo, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user doesn't exists")
        void updateUserRole_shouldThrowException_whenUserDoesNotExists() {
            when(userRepo.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> userService.updateUserRole(1L, updateUserRoleRequest));

            assertTrue(exception.getMessage().contains("User with ID"));

            verify(userRepo, times(1)).findById(1L);
            verify(userRepo, times(0)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests{

        @Test
        @DisplayName("Should delete user if user exists")
        void deleteUser_shouldDeleteUser_whenUserExists() {
            userService.deleteUser(1L);

            verify(userRepo, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should not throw any exception when user does not exists")
        void deleteUser_shouldNotThrow_whenUserDoesNotExists() {
            doNothing().when(userRepo).deleteById(99L);

            assertDoesNotThrow(() -> userService.deleteUser(99L));

            verify(userRepo, times(1)).deleteById(99L);
        }
    }

    @Nested
    @DisplayName("Get Active User Details Test")
    class GetActiveUserDetailsTests{

        @Test
        @DisplayName("Should return user details when the user is authenticated")
        void getActiveUserDetails_shouldReturnDetails_ifUserIsAuthenticated() {
            when(userRepo.findActiveUserDetails(1L)).thenReturn(activeUserResponse);

            ActiveUserResponse result = userService.getActiveUserDetails();

            assertNotNull(result);
            assertEquals("John Doe", result.getName());
            assertEquals("MEMBER", result.getRole().toString());

            verify(userRepo, times(1)).findActiveUserDetails(1L);
        }

        @Test
        @DisplayName("Should throw exception if user is unauthenticated")
        void getActiveUserDetails_shouldThrow_whenUserIsUnauthenticated() {
            SecurityContextHolder.clearContext();

            assertThrows(AuthenticationException.class, () -> userService.getActiveUserDetails());

            verify(userRepo, times(0)).findActiveUserDetails(any(Long.class ));
        }
    }
}
