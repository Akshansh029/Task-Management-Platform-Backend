package com.akshansh.taskmanagementplatform.repository;

import com.akshansh.taskmanagementplatform.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find all users
    @Override
    List<User> findAll();
}
