package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo = repo;
    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }

    public User getUserById(Long userId){
        return repo.findUserById(userId);
    }

    public List<UserProfileResponse> getAllUserProfiles(){
        return repo.findAllUserProfiles();
    }

    public UserProfileResponse getUserProfileById(Long userId){
        return repo.findAllUserProfileById(userId);
    }

    @Transactional
    public User createUser(CreateUserRequest data){
        User user = new User(data.getName(), data.getEmail(), data.getRole());
        return repo.save(user);
    }

    @Transactional
    public UserProfileResponse updateUser(Long userId, UpdateUserRequest data){
        User user = repo.findById(userId).get();

        // Updates fields if they are not null or empty.
        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            user.setName(data.getName());
        }
        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            user.setEmail(data.getEmail());
        }
        if (Objects.nonNull(data.getRole())) {
            user.setRole(data.getRole());
        }

        // Saves and returns the updated user entity.
        repo.save(user);
        return repo.findAllUserProfileById(user.getId());
    }

    @Transactional
    public void deleteUser(Long userId){
        repo.deleteById(userId);
    }
}
