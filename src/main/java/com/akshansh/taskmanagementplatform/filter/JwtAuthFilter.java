package com.akshansh.taskmanagementplatform.filter;

import com.akshansh.taskmanagementplatform.service.UserDetailsServiceImpl;
import com.akshansh.taskmanagementplatform.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;

@Component
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
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token has expired, please login again");
            return;     // Stop filter chain
        } catch (MalformedJwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Malformed JWT token");
            return;
        }
        catch (JwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
            return;
        }

        // Pass to the next filter
        filterChain.doFilter(request, response);
    }


    // Helper to write error response
    private void sendErrorResponse(HttpServletResponse response,
                                   HttpStatus status,
                                   String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(
                String.format(
                        "{\"timestamp\": %s, \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                        LocalDateTime.now(), status.value(), status.getReasonPhrase(), message
                )
        );
    }
}
