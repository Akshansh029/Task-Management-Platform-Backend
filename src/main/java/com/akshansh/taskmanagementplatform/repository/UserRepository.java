package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.entity.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find all users
    @Override
    List<User> findAll();

    // Create User
    @Override
    <S extends User> S save(S entity);
}
