package com.akshansh.taskmanagementplatform.util;

import com.akshansh.taskmanagementplatform.entity.User;
import com.akshansh.taskmanagementplatform.entity.UserPrincipal;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private Key jwtSecretKey;

    private final long EXPIRY_MS = 1000 * 60 * 60;      // 1 hour

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecretKey.getEncoded());
    }

    public String generateToken(UserPrincipal principal){
        return Jwts.builder()
                .issuer("http://localhost:8080/api/v1")
                .subject(principal.getUserId())
                .claim("userName", principal.getName())
                .claim("email", principal.getUsername())
                .claim("role", principal.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .signWith(getSecretKey())
                .compact();
    }
}
