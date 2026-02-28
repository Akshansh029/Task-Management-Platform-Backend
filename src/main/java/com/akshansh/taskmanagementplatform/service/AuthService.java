package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.dto.request.CreateUserRequest;
import com.akshansh.taskmanagementplatform.dto.request.LoginRequest;
import com.akshansh.taskmanagementplatform.dto.response.LoginResponse;
import com.akshansh.taskmanagementplatform.dto.response.UserProfileResponse;
import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import com.akshansh.taskmanagementplatform.exception.*;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import com.akshansh.taskmanagementplatform.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.akshansh.taskmanagementplatform.entity.User.convertToDto;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserProfileResponse registerUser(@Valid CreateUserRequest request) {
        User user = userRepo.findByEmail(request.getEmail());

        if(user != null){
            throw new UserAlreadyExistsException(
                    "User with email: " + request.getEmail() + " already exists");
        }

        if(!EnumUtils.isValidEnum(UserRole.class, request.getRole().toString())){
            throw new InvalidEnumValueException("Given role is not a valid UserRole");
        }

        User newUser = new User(
                request.getName(),
                request.getEmail(),
                request.getRole(),
                passwordEncoder.encode(request.getPassword()));

        userRepo.save(newUser);
        return convertToDto(newUser);
    }

    public LoginResponse loginUser(@Valid LoginRequest request) {
        // Authenticate email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return new LoginResponse("Login successful", token);

//        User user = userRepo.findByEmail(request.getEmail());
//
//        if(user == null){
//            throw new ResourceNotFoundException("No user found with email: " + request.getEmail());
//        }
//
//        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
//            throw new WrongPasswordException("Given password is wrong");
//        }
//
//        return "Login successful";
    }
}
