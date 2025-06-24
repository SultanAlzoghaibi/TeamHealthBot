package com.teamheath.bot.tools.RedisServices;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisSlackNameCache {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisSlackNameCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ============================ 🙋 DISPLAY NAME → USER ID ============================

    public void cacheSlackNameToId(String orgId, String slackDisplayName, String slackUserId) {
        String key = String.format("slack:name:%s:%s", orgId, slackDisplayName.toLowerCase());
        redisTemplate.opsForValue().set(key, slackUserId, Duration.ofHours(6));
    }

    public Optional<String> getUserIdFromSlackName(String orgId, String slackDisplayName) {
        String key = String.format("slack:name:%s:%s", orgId, slackDisplayName.toLowerCase());
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }
}