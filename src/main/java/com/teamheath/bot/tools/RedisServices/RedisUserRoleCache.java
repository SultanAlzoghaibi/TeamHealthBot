package com.teamheath.bot.tools.RedisServices;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisUserRoleCache {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisUserRoleCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ============================
    // 🔐 USER ROLE CACHE
    // ============================

    public void cacheUserRole(String userId, String role) {
        redisTemplate.opsForValue().set("userRole:" + userId, role, Duration.ofDays(7));
    }

    public Optional<String> getUserRole(String userId) {
        String role = redisTemplate.opsForValue().get("userRole:" + userId);
        return Optional.ofNullable(role);
    }

    public boolean isPM(String userId) {
        return getUserRole(userId)
                .map(role -> role.equalsIgnoreCase("PM"))
                .orElse(false);
    }

    public boolean isAdmin(String userId) {
        return getUserRole(userId)
                .map(role -> role.equalsIgnoreCase("ADMIN"))
                .orElse(false);
    }
    public void removeUserRole(String userId) {
        redisTemplate.delete(userId); // or redisTemplate.delete("role:" + userId); if you're using a key prefix
    }

}