package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRoleRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
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
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo){
        this.userRepo = userRepo;
    }

    public boolean isAdmin(Long userId){
        UserProfileResponse user = userRepo.findAllUserProfileById(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public Page<UserProfileResponse> getAllUsers(int pageNo, int pageSize, String search){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return userRepo.findAllUserProfiles(pageable, search);
    }

    public UserProfileResponse getUserProfileById(Long userId){
        return userRepo.findAllUserProfileById(userId);
    }

    @Transactional
    public UserProfileResponse createUser(Long userId, CreateUserRequest data){
        if(!isAdmin(userId))
            throw new ForbiddenException("Only admins can create users");

        User newUser = new User(data.getName(), data.getEmail(), data.getRole());
        userRepo.save(newUser);
        return convertToDto(newUser);
    }

    @Transactional
    public void bulkCreateUsers(List<CreateUserRequest> data){
        List<User> users = data.stream()
                        .map(d -> {
                            return new User(d.getName(), d.getEmail(), d.getRole());
                        })
                        .toList();
        userRepo.saveAll(users);
    }

    @Transactional
    public UserProfileResponse updateUser(Long userId, Long id, UpdateUserRequest data){
        if (!isAdmin(userId) && !userId.equals(id)){
            throw new ForbiddenException("Only admins and users can update their profile");
        }

        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Updates fields if they are not null or empty.
        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            user.setName(data.getName());
        }
        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            user.setEmail(data.getEmail());
        }

        // Saves and returns the updated user entity.
        userRepo.save(user);
        return convertToDto(user);
    }

    @Transactional
    public UserProfileResponse updateUserRole(Long userId, Long id, UpdateUserRoleRequest request){
        if(!isAdmin(userId))
            throw new ForbiddenException("Only admins can update user role");

        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " not found"));

        user.setRole(request.getRole());
        userRepo.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(Long userId, Long id){
        if(!isAdmin(userId))
            throw new ForbiddenException("Only admins can delete users");

        userRepo.deleteById(id);
    }
}
