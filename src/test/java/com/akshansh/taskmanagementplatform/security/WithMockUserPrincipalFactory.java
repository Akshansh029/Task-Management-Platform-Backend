package com.akshansh.taskmanagementplatform.security;

import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import com.akshansh.taskmanagementplatform.entity.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockUserPrincipalFactory
        implements WithSecurityContextFactory<WithMockUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
        // Build a User entity from the annotation values
        User user = User.builder()
                .id(annotation.id())
                .name(annotation.name())
                .email(annotation.email())
                .role(UserRole.valueOf(annotation.role()))
                .build();

        // Wrap it in YOUR UserPrincipal — same as what your app does at login
        UserPrincipal principal = new UserPrincipal(user);

        // Create the authentication token with the real UserPrincipal
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );

        // Put it in a SecurityContext and return it
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
