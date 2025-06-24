package com.teamheath.bot.tools.RedisServices;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RedisTeamScoreCache {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisTeamScoreCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Key format: team:last4:<orgId>:<teamId>
    private String buildKey(String orgId, String teamId) {
        return String.format("team:last4:%s:%s", orgId, teamId);
    }

    public void cacheLast4Scores(String orgId, String teamId, List<Integer> scores) {
        String key = buildKey(orgId, teamId);
        redisTemplate.delete(key); // Clear previous
        List<String> scoreStrings = scores.stream().map(String::valueOf).toList();
        redisTemplate.opsForList().rightPushAll(key, scoreStrings);
        redisTemplate.expire(key, Duration.ofHours(6));
    }


    public void clearLast4Scores(String orgId, String teamId) {
        redisTemplate.delete(buildKey(orgId, teamId));
    }

    public List<String> getLast4TeamScores(String key) {
        return redisTemplate.opsForList().range(key, 0, 3);
    }

    public void cacheLast4TeamScores(String key, List<Integer> scores) {
        List<String> scoreStrings = scores.stream().map(String::valueOf).toList();
        redisTemplate.delete(key);
        redisTemplate.opsForList().rightPushAll(key, scoreStrings);
        redisTemplate.expire(key, Duration.ofHours(6));
    }
}
