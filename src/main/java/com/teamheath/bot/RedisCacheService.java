package com.teamheath.bot;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RedisCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean hasCheckedIn(String orgId, String teamId, String userId) {
        String key = "org:" + orgId + ":team:" + teamId + ":scores";
        return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, userId));
    }

    public void cacheScore(String orgId, String teamId, String userId, int score) {
        String key = "org:" + orgId + ":team:" + teamId + ":scores";
        redisTemplate.opsForHash().increment(key, userId, score);
        redisTemplate.expire(key, Duration.ofHours(12));
    }

    public Optional<Integer> getLastScore(String orgId, String teamId, String userId) {
        String key = "org:" + orgId + ":team:" + teamId + ":scores";
        Object raw = redisTemplate.opsForHash().get(key, userId);
        if (raw != null) {
            try {
                return Optional.of(Integer.parseInt(raw.toString()));
            } catch (NumberFormatException ignored) {}
        }
        return Optional.empty();
    }

    public Map<String, String> getTeamScores(String orgId, String teamId) {
        String key = "org:" + orgId + ":team:" + teamId + ":scores";
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);

        // Convert to <String, String>
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : raw.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }
}