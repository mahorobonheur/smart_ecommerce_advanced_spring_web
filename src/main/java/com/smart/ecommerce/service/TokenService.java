package com.smart.ecommerce.service;

import com.smart.ecommerce.config.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final JwtUtil jwtUtil;

    private final Map<String, Long> revokedTokens = new ConcurrentHashMap<>();

    public TokenService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void revokeToken(String token) {
        Claims claims = jwtUtil.extractClaims(token);
        revokedTokens.put(token, claims.getExpiration().getTime());
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        revokedTokens.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
