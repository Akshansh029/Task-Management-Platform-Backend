package com.akshansh.taskmanagementplatform.util;

import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    @Value("${jwt.issuer}")
    private String jwtIssuer;

    private final long EXPIRY_MS = 1000 * 60 * 60;      // 1 hour

    @PostConstruct
    private void validateSecret() {
        if (jwtSecretKey == null || jwtSecretKey.isBlank()) {
            throw new IllegalStateException("JWT secret is missing. Configure JWT_SECRET_KEY environment variable.");
        }
    }

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserPrincipal principal){
        return Jwts.builder()
                .issuer(jwtIssuer)
                .subject(principal.getUserId())
                .claim("userName", principal.getName())
                .claim("email", principal.getUsername())
                .claim("role", principal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                )
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(getSecretKey())
                .compact();
    }
}
