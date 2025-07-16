package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务实现类，提供对 Redis 数据库的常见操作封装。
 *
 * <p>该类基于 Spring Data Redis 提供的 RedisTemplate 实现，支持字符串、哈希、批量操作等常见 Redis 操作。</p>
 */
@Service
public class RedisServiceImpl implements RedisService {

    /**
     * Redis 模板对象，用于执行 Redis 操作。
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 将指定的键值对存入 Redis，不设置过期时间。
     *
     * @param key   Redis 键
     * @param value Redis 值
     */
    @Override
    public void set(String key, Object value) {
        // 使用 RedisTemplate 的 opsForValue 方法进行字符串存储操作
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 根据指定的键获取对应的值。
     *
     * @param key Redis 键
     * @return Redis 中存储的值
     */
    @Override
    public Object get(String key) {
        // 获取 Redis 中对应 key 的值
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 存储带有过期时间的键值对。
     *
     * @param key     Redis 键
     * @param value   Redis 值
     * @param timeout 过期时间（单位：秒）
     */
    @Override
    public void setEx(String key, Object value, long timeout) {
        // 设置值并指定过期时间为秒级
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 删除指定的 Redis 键。
     *
     * @param key Redis 键
     */
    @Override
    public void delete(String key) {
        // 删除 Redis 中指定的 key
        redisTemplate.delete(key);
    }

    /**
     * 判断指定的 key 是否存在于 Redis 中。
     *
     * @param key Redis 键
     * @return 如果存在返回 true，否则返回 false
     */
    @Override
    public boolean exists(String key) {
        // 判断 key 是否存在
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 向 Redis 的哈希结构中存储指定字段和值。
     *
     * @param key     Redis 键（哈希表名）
     * @param hashKey 哈希字段名
     * @param value   哈希字段值
     */
    @Override
    public void hSet(String key, String hashKey, Object value) {
        // 将 hashKey 和 value 存入 key 对应的哈希结构中
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取 Redis 哈希结构中指定字段的值。
     *
     * @param key     Redis 键（哈希表名）
     * @param hashKey 哈希字段名
     * @return 哈希字段对应的值
     */
    @Override
    public Object hGet(String key, String hashKey) {
        // 获取 key 对应的哈希表所有字段和值
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        // 判断哈希表是否为空，若不为空则返回对应字段的值
        if (map != null && !map.isEmpty()) {
            return map.get(hashKey);
        }
        return null;
    }

    /**
     * 获取 Redis 哈希结构中所有的字段和值。
     *
     * @param key Redis 键（哈希表名）
     * @return 包含所有字段和值的 Map
     */
    @Override
    public Map<Object, Object> hGetAll(String key) {
        // 获取 key 对应的哈希表所有字段和值
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 批量存储多个键值对。
     *
     * @param values 包含多个键值对的 Map
     */
    @Override
    public void batchSet(Map<String, Object> values) {
        // 批量设置多个 key-value 对
        redisTemplate.opsForValue().multiSet(values);
    }

    /**
     * 批量获取多个键对应的值。
     *
     * @param keys 包含多个 Redis 键的列表
     * @return 按顺序返回每个 key 对应的值列表
     */
    @Override
    public List<Object> batchGet(List<String> keys) {
        // 批量获取多个 key 的值，去重后传入 RedisTemplate
        return redisTemplate.opsForValue().multiGet(new HashSet<>(keys));
    }
}
