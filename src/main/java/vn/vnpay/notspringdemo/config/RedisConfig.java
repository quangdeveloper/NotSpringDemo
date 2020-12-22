package vn.vnpay.notspringdemo.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class RedisConfig {

    @Bean
    @Primary
    RedisProperties redisProperties() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setDatabase(0);
        redisProperties.setPassword("testredis@123");
        redisProperties.setConnectTimeout(Duration.ofMillis(500000));

        RedisProperties.Pool pool = new RedisProperties.Pool();
        pool.setMaxActive(30);
        pool.setMaxIdle(20);
        pool.setMinIdle(10);
        pool.setMaxWait(Duration.ofMillis(60000));
        pool.setTimeBetweenEvictionRuns(Duration.ofMillis(60000));

        RedisProperties.Lettuce lettuce = new RedisProperties.Lettuce();
        lettuce.setPool(pool);

        RedisProperties.Sentinel sentinel = new RedisProperties.Sentinel();
        sentinel.setMaster("mymaster");
        sentinel.setNodes(Arrays.asList("10.22.7.111:26379", "10.22.7.112:26379"));

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
