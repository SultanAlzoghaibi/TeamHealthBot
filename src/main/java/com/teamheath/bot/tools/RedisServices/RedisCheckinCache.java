package com.teamheath.bot.tools.RedisServices;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCheckinCache {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisCheckinCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // =============== 👥 DUPLICATE CHECKIN BLOCKING ===============

    public boolean hasCheckedIn(String orgId, String teamId, String userId) {
        String key = String.format("checkin:scores:%s:%s", orgId, teamId);
        return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, userId));
    }

    public void cacheUserScore(String orgId, String teamId, String userId, int score) {
        String key = String.format("checkin:scores:%s:%s", orgId, teamId);
        redisTemplate.opsForHash().put(key, userId, String.valueOf(score));
        redisTemplate.expire(key, Duration.ofHours(12)); // same as voting window
    }

    // =============== 🧮 UNIQUE USER COUNT FOR SCORE TRIGGER ===============

    public void addUserToSet(String orgId, String teamId, String userId) {
        String key = String.format("checkin:userset:%s:%s", orgId, teamId);
        redisTemplate.opsForSet().add(key, userId);
        redisTemplate.expire(key, Duration.ofHours(12));
    }

    public long getUniqueUserCount(String orgId, String teamId) {
        String key = String.format("checkin:userset:%s:%s", orgId, teamId);
        return redisTemplate.opsForSet().size(key);
    }

    public void clearCheckinData(String orgId, String teamId) {
        redisTemplate.delete(String.format("checkin:scores:%s:%s", orgId, teamId));
        redisTemplate.delete(String.format("checkin:userset:%s:%s", orgId, teamId));
    }
}
