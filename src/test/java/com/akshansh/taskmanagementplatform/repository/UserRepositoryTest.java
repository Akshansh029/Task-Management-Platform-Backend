package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.response.ActiveUserResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Find User Profile By Id Test")
    void findUserProfileById_thenReturnProfile() {
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
        UserProfileResponse retrievedUser = userRepository.findUserProfileById(savedUser.getId());

        // assert
        Assertions.assertThat(retrievedUser).isNotNull();
        Assertions.assertThat(retrievedUser.getName()).isEqualTo(savedUser.getName());
        Assertions.assertThat(retrievedUser.getRole()).isEqualTo(savedUser.getRole());
    }

    @Test
    @DisplayName("Find All User Profiles Test")
    void findAllUserProfiles_thenReturnPagedResponse() {
        User user1 = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        User user2 = User.builder()
                .name("Steve Smith")
                .email("stevesmith1234@gmail.com")
                .role(UserRole.VIEWER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.LOCAL)
                .build();
        entityManager.persist(user1);
        entityManager.persist(user2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<UserProfileResponse> response = userRepository.findAllUserProfiles(pageable);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Find All User Profiles Test with Search")
    void findAllUserProfilesWithSearch_thenReturnPagedResponse() {
        User user1 = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        User user2 = User.builder()
                .name("Steve Smith")
                .email("stevesmith1234@gmail.com")
                .role(UserRole.VIEWER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.LOCAL)
                .build();
        entityManager.persist(user1);
        entityManager.persist(user2);
        Pageable pageable = PageRequest.of(0, 10);

        Page<UserProfileResponse> response = userRepository.findAllUserProfiles( "Smith", pageable);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(response.getContent().get(0).getName()).isEqualTo(user2
                .getName());
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

    @Test
    @DisplayName("Update User Test")
    void updateUser_thenReturnUpdatedUser() {
        User newUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(newUser);

        String newName = "John Doe Sr";
        newUser.setName(newName);
        User updatedUser = userRepository.save(newUser);

        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Find By Email Test")
    void findByEmail_thenReturnUser() {
        User newUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(newUser);

        String email = "johndoe1234@gmail.com";
        User retrievedUser = userRepository.findByEmail(email);

        Assertions.assertThat(retrievedUser).isNotNull();
        Assertions.assertThat(retrievedUser.getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    @DisplayName("Save All Test")
    void saveAll_thenReturnListOfUsers() {
        User user1 = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        User user2 = User.builder()
                .name("Steve Smith")
                .email("stevesmith1234@gmail.com")
                .role(UserRole.VIEWER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.LOCAL)
                .build();
        List<User> newUsers = List.of(user1, user2);

        List<User> savedUsers = userRepository.saveAll(newUsers);

        Assertions.assertThat(savedUsers).isNotNull();
        Assertions.assertThat(savedUsers.size()).isEqualTo(newUsers.size());
    }

    @Test
    @DisplayName("Find By Id Test")
    void findById_thenReturnOptional() {
        User savedUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(savedUser);

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        Assertions.assertThat(retrievedUser.get().getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    @DisplayName("Delete By Id Test")
    void deleteById_thenSuccess() {
        User newUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(newUser);

        userRepository.deleteById(newUser.getId());
        Assertions.assertThat(entityManager.find(User.class, newUser.getId())).isNull();
    }

    @Test
    @DisplayName("Find Active User Details")
    void findActiveUserDetails_thenReturnActiveUserResponse() {
        User activeUser = User.builder()
                .name("John Doe")
                .email("johndoe1234@gmail.com")
                .role(UserRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .provider(AuthProvider.GOOGLE)
                .build();
        entityManager.persist(activeUser);

        ActiveUserResponse userDetails = userRepository.findActiveUserDetails(activeUser.getId());

        Assertions.assertThat(userDetails).isNotNull();
        Assertions.assertThat(userDetails.getEmail()).isEqualTo(activeUser.getEmail());
        Assertions.assertThat(userDetails.getRole()).isEqualTo(activeUser.getRole());
    }
}