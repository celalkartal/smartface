package com.smartface.eventservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    private static final String LOCK_PREFIX = "lock:";
    private static final String LOCK_VALUE = "locked";
    private static final long LOCK_EXPIRE_TIME = 1000000; // 1000 seconds

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean getLock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        return redisTemplate.opsForValue().setIfAbsent(key, LOCK_VALUE, LOCK_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public void releaseLock(String lockKey) {
        String key = LOCK_PREFIX + lockKey;
        redisTemplate.delete(key);
    }
}