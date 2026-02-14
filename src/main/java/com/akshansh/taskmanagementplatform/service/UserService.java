package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.akshansh.taskmanagementplatform.entity.User.convertToDto;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo){
        this.repo = repo;
    }

//    public Page<User> getAllUsers(int pageNo, int pageSize){
//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        return repo.findAll(pageable);
//    }

    public Page<UserProfileResponse> getAllUsers(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return repo.findAllUserProfiles(pageable);
    }

    public UserProfileResponse getUserProfileById(Long userId){
        return repo.findAllUserProfileById(userId);
    }

    @Transactional
    public UserProfileResponse createUser(CreateUserRequest data){
        User user = new User(data.getName(), data.getEmail(), data.getRole());
        repo.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void bulkCreateUsers(List<CreateUserRequest> data){
        List<User> users = data.stream()
                        .map(d -> {
                            return new User(d.getName(), d.getEmail(), d.getRole());
                        })
                        .toList();
        repo.saveAll(users);
    }

    @Transactional
    public UserProfileResponse updateUser(Long userId, UpdateUserRequest data){
        User user = repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(Long userId){
        repo.deleteById(userId);
    }
}
