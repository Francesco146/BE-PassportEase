package it.univr.passportease.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis configuration class.
 * It is used to configure the connection to the Redis server
 * and to create a RedisTemplate bean. Redis configuration is
 * read from the application.properties file and the port is set
 * to 6379 by default.
 */
@Configuration
@Log4j2
public class RedisConfig {
    /**
     * Redis host.
     */
    @Value("${spring.data.redis.host}")
    private String redisHost;

    /**
     * Creates a connection to the Redis server.
     *
     * @return JedisConnectionFactory, used to create a connection to the Redis server
     */
    @Bean
    public JedisConnectionFactory redisConnectionFactory() {

        int redisPort = 6379;

        log.info("Redis host: " + redisHost);
        log.info("Redis port: " + redisPort);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost,
                redisPort);
        return new JedisConnectionFactory(redisStandaloneConfiguration);

    }

    /**
     * Creates a RedisTemplate bean, used to interact with the Redis server.
     *
     * @param <T> Type of the RedisTemplate
     * @return RedisTemplate, used to interact with the Redis server
     */
    @Bean
    public <T> RedisTemplate<String, T> redisTemplate() {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
