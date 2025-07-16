package com.abnormality.abnormalityaccept.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类，用于定义和配置 Redis 相关的 Bean。
 *
 * <p>该类通过 Spring 的 @Configuration 注解标识为配置类，包含创建 RedisConnectionFactory 和 RedisTemplate 的 Bean 定义。</p>
 * <p>主要功能包括：</p>
 * <ul>
 *   <li>配置 Redis 连接工厂（Lettuce 实现）。</li>
 *   <li>配置 RedisTemplate，指定键值序列化方式为 StringRedisSerializer。</li>
 * </ul>
 */
@Configuration
public class RedisConfig {

    /**
     * 创建并返回 RedisConnectionFactory Bean。
     *
     * <p>使用 Lettuce 作为 Redis 客户端实现，创建默认的 Redis 连接工厂。</p>
     *
     * @return 返回一个 LettuceConnectionFactory 实例
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 使用 LettuceConnectionFactory 默认配置创建连接工厂
        return new LettuceConnectionFactory();
    }

    /**
     * 创建并配置 RedisTemplate Bean。
     *
     * <p>该方法创建 RedisTemplate 实例，并设置以下内容：</p>
     * <ul>
     *   <li>使用提供的 RedisConnectionFactory 建立连接。</li>
     *   <li>设置键（Key）和值（Value）的序列化器为 StringRedisSerializer，确保 Redis 中存储的键和值为标准字符串格式。</li>
     *   <li>调用 afterPropertiesSet() 完成初始化。</li>
     * </ul>
     *
     * @param factory RedisConnectionFactory 实例，用于建立 Redis 连接
     * @return 配置完成的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建 RedisTemplate 实例
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置连接工厂
        template.setConnectionFactory(factory);

        // 设置键的序列化方式为 StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());

        // 设置值的序列化方式为 StringRedisSerializer
        template.setValueSerializer(new StringRedisSerializer());

        // 初始化配置
        template.afterPropertiesSet();

        // 返回配置完成的 RedisTemplate
        return template;
    }
}

