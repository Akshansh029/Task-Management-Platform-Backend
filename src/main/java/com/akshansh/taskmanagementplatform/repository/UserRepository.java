package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find all UserProfiles
//    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse(" +
//            "u.id, u.name, u.email, u.role, u.createdAt, SIZE(u.ownedProjects)) " +
//            "FROM User u WHERE (:search IS NULL OR " +
//            "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))"
//    )
//    Page<UserProfileResponse> findAllUserProfiles(Pageable pageable, String search);

    @Query("""
    SELECT new com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse(
        u.id, u.name, u.email, u.role, u.createdAt, SIZE(u.ownedProjects)
    )
    FROM User u
    WHERE (:search IS NULL OR
           LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
""")
    Page<UserProfileResponse> findAllUserProfiles(
            @Param("search") String search,
            Pageable pageable
    );

    // Find UserProfile by id
    @Query("SELECT new com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse(" +
            "u.id, u.name, u.email, u.role, u.createdAt, SIZE(u.ownedProjects)) " +
            "FROM User u WHERE u.id = ?1")
    UserProfileResponse findAllUserProfileById(Long userId);

    // Create User
    @Override
    <S extends User> S save(S entity);

    // Delete User
    void deleteUserById(Long userId);
}
