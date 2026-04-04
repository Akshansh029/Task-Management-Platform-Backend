package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.AuthProvider;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("Find User Profile By Id Test")
    void whenFindUserProfileById_thenReturnProfile() {
        // arrange
        User savedUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(savedUser);

        // act
        UserProfileResponse retrievedUser = userRepository.findUserProfileById(1L);

        // assert
        Assertions.assertThat(retrievedUser).isNotNull();
        Assertions.assertThat(retrievedUser.getName()).isEqualTo(savedUser.getName());
        Assertions.assertThat(retrievedUser.getRole()).isEqualTo(savedUser.getRole());
    }

    @Test
    @DisplayName("Create User Test")
    void saveUser_thenReturnUserProfile() {
        User newUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(newUser);

        User createdUser = userRepository.save(newUser);

        Assertions.assertThat(createdUser).isNotNull();
        Assertions.assertThat(createdUser.getName()).isEqualTo(newUser.getName());
        Assertions.assertThat(createdUser.getRole()).isEqualTo(newUser.getRole());
    }


}