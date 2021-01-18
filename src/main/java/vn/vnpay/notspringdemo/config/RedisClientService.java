package vn.vnpay.notspringdemo.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.vnpay.notspringdemo.exception.GeneralException;
import vn.vnpay.notspringdemo.util.Constant;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
            logger.error("Token [{}] Exception: ", ThreadContext.get("token"), exception);
            return null;
        }
    }

    private StatefulRedisConnection<String, Object> getConnection() {
        try {
            return pool.borrowObject();
        } catch (Exception exception) {
            logger.error("Token [{}] Exception: ", ThreadContext.get("token"), exception);
            return null;
        }
    }

    public Boolean setKey(String key, Object value) {

        StatefulRedisConnection<String, Object> redisConnection = getConnection();
        if (redisConnection == null) {
            logger.error("Token [{}] : Can not connect to redis server ", ThreadContext.get("token"));
            throw new GeneralException(Constant.RESPONSE.CODE.C999, Constant.RESPONSE.MESSAGE.C999);
        }

        if (value == null) {
            value = "NULL";
        }
        String result = redisConnection.sync().set(key, value);

        logger.info("Token [{}] : Redis Set [key:  {},  value: {}]  status return: {}",
                ThreadContext.get("token"),
                key,
                value,
                result.equals("OK") ? "OK" : "FAIL");

        pool.returnObject(redisConnection);
        return result.equals("OK");
    }

    public Object getKey(String key) {

        StatefulRedisConnection<String, Object> redisConnection = getConnection();

        if (redisConnection == null) {
            logger.error("Token [{}]: Can not connect to redis server ", ThreadContext.get("token"));
            throw new GeneralException(Constant.RESPONSE.CODE.C999, Constant.RESPONSE.MESSAGE.C999);
        }
        Object result = redisConnection.sync().get(key);

        logger.info("Token [{}]: Redis Get key {} successful",
                ThreadContext.get("token"), key);
        pool.returnObject(redisConnection);
        return result;
    }

    public Boolean hasKey(String key) {

        if (getKey(key) == null) {
            logger.info("Token [{}]:  Key {} not exists on redis",
                    ThreadContext.get("token"),
                    key);
            return false;
        }
        return true;
    }

}
