package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User getUserById(Long id){
        return repo.findUserById(id);
    }

    public List<UserProfileResponse> getAllUserProfiles(){
        return repo.findAllUserProfiles();
    }

    public UserProfileResponse getUserProfileById(Long id){
        return repo.findAllUserProfileById(id);
    }

    @Transactional
    public User createUser(@Valid CreateUserRequest data){
        User user = new User(data.getName(), data.getEmail(), data.getRole());
        return repo.save(user);
    }
}
