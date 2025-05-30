package com.blooddonation.blood_donation_support_system.service;

import com.blooddonation.blood_donation_support_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    @Autowired
    private JwtUtil jwtUtil;

    private Set<String> blacklisttedTokens = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        blacklisttedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklisttedTokens.contains(token);
    }

    @Scheduled(fixedRate = 3600000)
    public void clearExpiredTokens() {
        blacklisttedTokens.removeIf(token -> {
        try {
            return jwtUtil.isTokenExpired(token);
        } catch (Exception e) {
            return true;
        }
    });
}
}
