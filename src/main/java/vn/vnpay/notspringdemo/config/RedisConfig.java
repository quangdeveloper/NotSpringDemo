package vn.vnpay.notspringdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RedisConfig {

    @Value("${redis.database}")
    private int database;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.connection-timeout}")
    private long connectionTimeout;

    @Value("${redis.lettuce.pool.max-active}")
    private int maxActive;

    @Value("${redis.lettuce.pool.max-idle}")
    private int maxIdle;

    @Value("${redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Value("${redis.lettuce.pool.max-wait}")
    private long maxWait;

    @Value("${redis.lettuce.pool.time-between-evict-run}")
    private long timeBetweenEvictRun;

    @Value("${redis.sentinel.master}")
    private String redisMaster;

    @Value("${redis.sentinel.nodes}")
    private List<String> nodes;


    @Bean
    @Primary
    RedisProperties redisProperties() {

        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setDatabase(database);
        redisProperties.setPassword(password);
        redisProperties.setConnectTimeout(Duration.ofMillis(connectionTimeout));

        RedisProperties.Pool pool = new RedisProperties.Pool();
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxIdle);
        pool.setMinIdle(minIdle);
        pool.setMaxWait(Duration.ofMillis(maxWait));
        pool.setTimeBetweenEvictionRuns(Duration.ofMillis(timeBetweenEvictRun));

        RedisProperties.Lettuce lettuce = new RedisProperties.Lettuce();
        lettuce.setPool(pool);

        RedisProperties.Sentinel sentinel = new RedisProperties.Sentinel();
        sentinel.setMaster(redisMaster);
        sentinel.setNodes(nodes);

        return redisProperties;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisConfig redisConfig = new RedisConfig();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
