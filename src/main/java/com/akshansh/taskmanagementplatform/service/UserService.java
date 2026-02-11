package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo = repo;
    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }
}
