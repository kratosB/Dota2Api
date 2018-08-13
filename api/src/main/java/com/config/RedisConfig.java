package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Created on 2018/8/10.
 *
 * @author zhiqiang bao
 */
@Configuration
public class RedisConfig {

    private final String hostname;

    private final int port;

    @Autowired
    public RedisConfig(@Value("${spring.redis.host}") String hostname, @Value("${spring.redis.port}") int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(hostname);
        factory.setPort(port);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(40);
        config.setMaxTotal(50);
        config.setMinIdle(30);
        factory.setPoolConfig(config);
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
        // 过期时间为一天
        cacheManager.setDefaultExpiration(3600 * 24);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

}
