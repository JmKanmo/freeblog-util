package com.service.util.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    public void setValueWithExpiration(String key, String value, long expirationMinutes) {
        // Set the value with expiration
        stringRedisTemplate.opsForValue().set(key, value, expirationMinutes, TimeUnit.MINUTES);
    }

    public String getValue(String key) {
        // Retrieve the value for the given key
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
}
