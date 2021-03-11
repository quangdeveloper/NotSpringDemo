package vn.vnpay.notspringdemo.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Component
public class PoolConnectionRedisFactory extends BasePooledObjectFactory<StatefulRedisConnection<String, String>> {

    @Value("${redis.sentinel.nodes}")
    private List<String> nodes;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.sentinel.master}")
    private String master;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.database}")
    private int database;

    private static RedisURI redisURI;

    @PostConstruct
    private void intiClass() {

        RedisURI.Builder redisURIs = RedisURI.builder();

        nodes.forEach(node -> redisURIs.withSentinel(node, port));

        redisURI = redisURIs.withPassword(password.toCharArray())
                .withDatabase(database)
                .withSentinelMasterId(master).build();
    }

    @Override
    public StatefulRedisConnection<String, String> create() throws Exception {

        RedisClient client = RedisClient.create(redisURI);
        client.setDefaultTimeout(Duration.ofMillis(60000));
        return client.connect();
    }

    @Override
    public PooledObject<StatefulRedisConnection<String, String>> wrap(
            StatefulRedisConnection<String, String> stringObjectStatefulRedisConnection) {

        return new DefaultPooledObject<>(stringObjectStatefulRedisConnection);
    }
}
