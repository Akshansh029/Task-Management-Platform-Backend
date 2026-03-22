package com.akshansh.taskmanagementplatform.service;

import com.akshansh.taskmanagementplatform.exception.InvalidAuthCodeException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthCodeService {

    // In-memory store with expiry (or use Redis in prod)
    private final Map<String, TokenPair> codeStore = new ConcurrentHashMap<>();

    public void store(String code, String accessToken, String refreshToken) {
        codeStore.put(code, new TokenPair(accessToken, refreshToken,
                LocalDateTime.now().plusMinutes(2)));  // expires in 2 min
    }

    public TokenPair exchange(String code) {
        TokenPair pair = codeStore.remove(code); // one-time use, deleted immediately
        if (pair == null || LocalDateTime.now().isAfter(pair.expiresAt())) {
            throw new InvalidAuthCodeException("Invalid or expired code");
        }
        return pair;
    }

    public record TokenPair(String accessToken, String refreshToken, LocalDateTime expiresAt) {}
}
