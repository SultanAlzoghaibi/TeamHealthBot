package com.teamheath.bot;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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
    public void addUserToTeamSet(String orgId, String teamId, String userId) {
        String redisKey = "team_checkins:" + orgId + ":" + teamId;
        redisTemplate.opsForSet().add(redisKey, userId);
    }

    public long getTeamSetSize(String orgId, String teamId) {
        String redisKey = "team_checkins:" + orgId + ":" + teamId;
        return redisTemplate.opsForSet().size(redisKey);
    }

    public void clearTeamSet(String orgId, String teamId) {
        String redisKey = "team_checkins:" + orgId + ":" + teamId;
        redisTemplate.delete(redisKey);
    }

    public Long incrementCheckinCount(String orgId, String teamId) {
        String key = String.format("checkins:%s:%s", orgId, teamId);
        Long count = redisTemplate.opsForValue().increment(key);
        // Optional TTL
        redisTemplate.expire(key, Duration.ofMinutes(1));
        return count;
    }

    public void clearTeamScores(String orgId, String teamId) {
        String redisKey = "team_checkins:" + orgId + ":" + teamId;
        redisTemplate.delete(redisKey);
    }

    public List<String> getLast4TeamScores(String key) {
        return redisTemplate.opsForList().range(key, 0, 3);
    }

    public void cacheLast4TeamScores(String key, List<Integer> scores) {
        List<String> scoreStrings = scores.stream().map(String::valueOf).toList();
        redisTemplate.delete(key); // clean up previous
        redisTemplate.opsForList().rightPushAll(key, scoreStrings);
        redisTemplate.expire(key, Duration.ofHours(6)); // optional
    }

    // for priveldge checking
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




}