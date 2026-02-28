package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import com.akshansh.taskmanagementplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }

        return new UserPrincipal(user);
    }
}
