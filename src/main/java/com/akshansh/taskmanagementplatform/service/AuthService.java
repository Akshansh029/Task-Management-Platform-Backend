package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;


}
