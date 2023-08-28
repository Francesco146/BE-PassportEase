package it.univr.passportease.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {

        int redisPort = 6379;
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost,
                redisPort);
        return new JedisConnectionFactory(redisStandaloneConfiguration);

    }

    @Bean
    public <T> RedisTemplate<String, T> redisTemplate() {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
