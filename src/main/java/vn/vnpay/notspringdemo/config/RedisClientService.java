package vn.vnpay.notspringdemo.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

@Service
public class RedisClientService {

    private final Logger logger = LoggerFactory.getLogger(RedisClientService.class);

    @Value("${redis.database}")
    private int database;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.connection-timeout}")
    private long connectionTimeout;

    @Value("${redis.lettuce.pool.max-total}")
    private int maxTotal;

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

    private static GenericObjectPool<StatefulRedisConnection<String, Object>> pool;

    @PostConstruct
    private GenericObjectPool<StatefulRedisConnection<String, Object>> createPool() {
        try {
            RedisURI redisURI2 = RedisURI.Builder
                    .sentinel(nodes.get(0), port, redisMaster)
                    .withSentinel(nodes.get(1), port)
                    .withPassword(password.toCharArray())
                    .withDatabase(database)
                    .build();

            RedisClient client = RedisClient.create(redisURI2);
            client.setDefaultTimeout(Duration.ofMillis(connectionTimeout));

            GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig<>();
            genericObjectPoolConfig.setMaxIdle(maxIdle);
            genericObjectPoolConfig.setMinIdle(minIdle);
            genericObjectPoolConfig.setMaxTotal(maxTotal);
            genericObjectPoolConfig.setMaxWaitMillis(maxWait);
            genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictRun);

            pool = ConnectionPoolSupport
                    .createGenericObjectPool(() -> client.connect(), genericObjectPoolConfig);
            logger.info("Created Redis pool {}", pool.getJmxName());

            return pool;
        } catch (Exception exception) {
            logger.error("Thead Id {} : Exception", Thread.currentThread().getId(), exception);
            return null;
        }
    }

    private StatefulRedisConnection<String, Object> getConnection() {
        try {
            return pool.borrowObject();
        } catch (Exception exception) {
            logger.error("Thead Id {} : Exception", Thread.currentThread().getId(), exception);
            return null;
        }
    }

    private RedisCommands<String, Object> getCommands() {
        return getConnection().sync();
    }


    public Boolean setKey(String key, Object value) {

        RedisCommands<String, Object> commands = getCommands();
        if (value == null) {
            value = "NULL";
        }
        String result = commands.set(key, value);

        logger.info("Thread Id {}: Redis Set [key:  {},  value: {}]  status return: {}",
                Thread.currentThread().getId(),
                key,
                value,
                result.equals("OK") ? "OK" : "FAIL");

        if (result == null) {
            return false;
        }
        pool.returnObject(commands.getStatefulConnection());
        return result.equals("OK");
    }

    public Object getKey(String key) {

        RedisCommands<String, Object> commands = getCommands();
        Object result = commands.get(key);

        logger.info("Thread Id {}: Redis Get [key:  {},  value: {}]",
                Thread.currentThread().getId(),
                key,
                result);
        pool.returnObject(commands.getStatefulConnection());
        return result;
    }

    public Boolean hasKey(String key) {

        if (getKey(key) == null) {
            logger.info("Thread Id {}: Key {} not exists on redis",
                    Thread.currentThread().getId(),
                    key);
            return false;
        }
        return true;
    }

}
