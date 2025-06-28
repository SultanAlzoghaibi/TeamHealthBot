package com.teamheath.bot.tools;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CacheDebugger {

    private static CacheDebugger instance;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static void printAllRedisCache() {
        if (instance == null) {
            System.out.println("❗ CacheDebugger not initialized");
            return;
        }

        Set<String> keys = instance.redisTemplate.keys("*");
        if (keys == null || keys.isEmpty()) {
            System.out.println("❗ No Redis cache keys found.");
            return;
        }

        System.out.println("📦 Redis Cache Contents:");
        for (String key : keys) {
            String value = instance.redisTemplate.opsForValue().get(key);
            System.out.println("— Key: " + key + " → " + value);
        }
    }

}