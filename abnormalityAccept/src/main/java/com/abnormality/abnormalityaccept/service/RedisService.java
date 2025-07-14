package com.abnormality.abnormalityaccept.service;

import java.util.List;
import java.util.Map;

public interface RedisService {

    /**
     * 存储数据
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 获取数据
     *
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 设置带过期时间的数据
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    void setEx(String key, Object value, long timeout);

    /**
     * 删除数据
     *
     * @param key 键
     */
    void delete(String key);

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 设置哈希表数据
     *
     * @param key     键
     * @param hashKey 哈希键
     * @param value   值
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 获取哈希表数据
     *
     * @param key     键
     * @param hashKey 哈希键
     * @return 值
     */
    Object hGet(String key, String hashKey);

    /**
     * 获取哈希表中所有数据
     *
     * @param key 键
     * @return 哈希表所有值
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 批量设置数据
     *
     * @param values 值集合
     */
    void batchSet(Map<String, Object> values);

    /**
     * 批量获取数据
     *
     * @param keys 键集合
     * @return 值集合
     */
    List<Object> batchGet(List<String> keys);
}
