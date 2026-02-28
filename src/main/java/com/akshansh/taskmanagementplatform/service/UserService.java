package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.UpdateUserRoleRequest;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.ForbiddenException;
import com.akshansh.taskmanagementplatform.exception.ResourceNotFoundException;
import com.akshansh.taskmanagementplatform.exception.UserAlreadyExistsException;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.akshansh.taskmanagementplatform.entity.User.convertToDto;
import static com.akshansh.taskmanagementplatform.util.UserUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public Page<UserProfileResponse> getAllUsers(int pageNo, int pageSize, String search){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        if(search == null || search.isBlank()){
            return userRepo.findAllUserProfiles(pageable);
        }

        return userRepo.findAllUserProfiles(search, pageable);
    }

    public UserProfileResponse getUserProfileById(Long userId){
        return userRepo.findUserProfileById(userId);
    }

    @Transactional
    public UserProfileResponse createUser(CreateUserRequest data){
        User user = userRepo.findByEmail(data.getEmail());

        if(user != null){
            throw new UserAlreadyExistsException(
                    "User with email: " + data.getEmail() + " already exists");
        }

        User newUser = new User(
                data.getName(),
                data.getEmail(),
                data.getRole(),
                passwordEncoder.encode(data.getPassword()));

        userRepo.save(newUser);
        return convertToDto(newUser);
    }

    @Transactional
    public void bulkCreateUsers(List<CreateUserRequest> data){
        List<User> users = data.stream()
                        .map(d -> {
                            User user = userRepo.findByEmail(d.getEmail());

                            if(user != null){
                                throw new UserAlreadyExistsException(
                                        "User with email: " + d.getEmail() + " already exists");
                            }

                            return new User(d.getName(), d.getEmail(), d.getRole(), passwordEncoder.encode(d.getPassword()));
                        })
                        .toList();
        userRepo.saveAll(users);
    }

    @Transactional
    public UserProfileResponse updateUser(Long id, UpdateUserRequest data){
        // Check if current user is update profile or not
        UserPrincipal currentUser = getCurrentUser();

        if(!id.equals(currentUser.getUserId())){
            throw new ForbiddenException("Only admins or user themselves can update their profile");
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
    public UserProfileResponse updateUserRole(Long id, UpdateUserRoleRequest request){
//        if(!isAdmin(userId))
//            throw new ForbiddenException("Only admins can update user role");

        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " not found"));

        user.setRole(request.getRole());
        userRepo.save(user);
        return convertToDto(user);
    }

    @Transactional
    public void deleteUser(Long id){
        userRepo.deleteById(id);
    }
}
