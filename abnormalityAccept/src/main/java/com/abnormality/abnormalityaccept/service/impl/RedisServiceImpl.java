package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setEx(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Object hGet(String key, String hashKey) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        if (map != null && !map.isEmpty()) {
            return map.get(hashKey);
        }
        return null;
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void batchSet(Map<String, Object> values) {
        redisTemplate.opsForValue().multiSet(values);
    }

    @Override
    public List<Object> batchGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(new HashSet<>(keys));
    }
}
