package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @InjectMocks
    private UserService userService;    // This is the class we are testing

    @Mock
    private UserRepository userRepo;
    @Mock
    private  PasswordEncoder passwordEncoder;


    @Nested
    @DisplayName("Create User Tests")       // Nested classes are used to organize tests
    class CreateUserTests{

        @Test
        void createUser() {
        }

    }

    @Test
    void getUserProfileById() {
    }


    @Test
    void updateUserRole() {
    }
}