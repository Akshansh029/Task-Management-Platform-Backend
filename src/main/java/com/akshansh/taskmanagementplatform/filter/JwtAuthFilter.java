package com.akshansh.taskmanagementplatform.filter;

import com.akshansh.taskmanagementplatform.service.UserDetailsServiceImpl;
import com.akshansh.taskmanagementplatform.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Read authorization header
        String authHeader = request.getHeader("Authorization");

        // If no bearer token, then skip this filter
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token = authHeader.substring(7);

        // Validate jwt token
        try{
            if(jwtUtil.isTokenValid(token)){
                String email = jwtUtil.extractEmail(token);
                UserDetails principal = userDetailsService.loadUserByUsername(email);

                // Create auth object and store it
                var authToken = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities()
                );

                // Adding request object in auth token (best practice)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException e) {
            // Store exception as request attribute, then let Spring Security handle it
            request.setAttribute("jwt_exception", e);
            SecurityContextHolder.clearContext();
            // Do NOT rethrow — just return and let the entry point handle the response
            filterChain.doFilter(request, response); // or just return;
        }

        // Pass to the next filter
        filterChain.doFilter(request, response);
    }
}
