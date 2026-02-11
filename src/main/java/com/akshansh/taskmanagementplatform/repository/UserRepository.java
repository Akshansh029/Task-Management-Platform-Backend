package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find all users
    @Override
    List<User> findAll();

    // Find user by id
    User findUserById(Long userId);

    // Find all UserProfiles
    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse(" +
            "u.id, u.name, u.email, u.role, SIZE(u.ownedProjects)) " +
            "FROM User u")
    List<UserProfileResponse> findAllUserProfiles();

    // Find UserProfile by id
    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse(" +
            "u.id, u.name, u.email, u.role, SIZE(u.ownedProjects)) " +
            "FROM User u WHERE u.id = ?1")
    UserProfileResponse findAllUserProfileById(Long userId);

    // Create User
    @Override
    <S extends User> S save(S entity);

    // Delete User
    void deleteUserById(Long userId);
}
