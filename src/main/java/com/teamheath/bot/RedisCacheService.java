package com.teamheath.bot;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheCheckin(String userId, String teamId, int score) {
        // Cache the last score
        redisTemplate.opsForValue().set("user:" + userId + ":last_score", String.valueOf(score));

        // Set check-in lock for this user under this team
        String checkInKey = "team:" + teamId + ":user:" + userId + ":checked_in";
        redisTemplate.opsForValue().set(checkInKey, "1", Duration.ofHours(24));
    }

    public boolean hasCheckedIn(String teamId, String userId) {
        String key = "team:" + teamId + ":user:" + userId + ":checked_in";
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Optional<Integer> getLastScore(String userId) {
        String val = redisTemplate.opsForValue().get("user:" + userId + ":last_score");
        if (val != null) {
            return Optional.of(Integer.parseInt(val));
        }
        return Optional.empty();
    }

    public void markCheckedIn(String teamId, String userId) {
        String checkInKey = "team:" + teamId + ":user:" + userId + ":checked_in";
        redisTemplate.opsForValue().set(checkInKey, "1", Duration.ofHours(24));

    }
}