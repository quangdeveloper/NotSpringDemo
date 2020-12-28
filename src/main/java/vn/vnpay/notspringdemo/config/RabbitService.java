package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


@Service
public class RabbitService {

    Logger logger = LoggerFactory.getLogger(RabbitService.class);

    @Value("${rabbitMQ.queueName}")
    private String queueName;

    @Value("${rabbitMQ.topicExchangeName}")
    private String topicExchangeName;

    @Value("${rabbitMQ.routingKey}")
    private String routingKey;

    @Value("${rabbitMQ.host}")
    private String host;

    @Value("${rabbitMQ.port}")
    private int port;

    @Value("${rabbitMQ.username}")
    private String username;

    @Value("${rabbitMQ.password}")
    private String password;

    @Value("${rabbitMQ.virtual-host}")
    private String virtualHost;

    @Value("${rabbitMQ.request-heart-beat}")
    private int requestHeartBeat;

    @Value("${rabbitMQ.exchange-type}")
    private String exchangeType;

    @Value("${rabbitMQ.channel.max-total}")
    private int maxTotal;

    @Value("${rabbitMQ.channel.max-idle}")
    private int maxIdle;

    @Value("${rabbitMQ.channel.min-idle}")
    private int minIdle;

    @Value("${rabbitMQ.channel.max-wait}")
    private long maxWait;

    @Value("${rabbitMQ.channel.time-between-eviction-runs}")
    private long timeBetweenEvictRuns;


    private ConnectionFactory getConnectionFactory() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setRequestedHeartbeat(requestHeartBeat);
        return factory;
    }

    public Connection getConnection() {

        try {
            return getConnectionFactory().newConnection();
        } catch (IOException ioException) {
            logger.error("Thread Id {} IOException: ", Thread.currentThread().getId());
            ioException.printStackTrace();
            return null;
        } catch (TimeoutException timeoutException) {
            logger.error("Thread Id {} TimeoutException: ", Thread.currentThread().getId());
            timeoutException.printStackTrace();
            return null;
        }
    }

    private static GenericObjectPool<Channel> pool;

    @PostConstruct
    private GenericObjectPool<Channel> createChannelPool() {

        GenericObjectPoolConfig<Channel> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictRuns);
        logger.info("Created  Rabbit MQ pool {}", pool.getJmxName());
        return pool;
    }

    public void sendMessage(String mess, String routeKey) {

        try {
            Channel channel = getConnection().createChannel();

            // 3 params of exchange: name , type, unable auto delete
            channel.exchangeDeclare(topicExchangeName, exchangeType, true);

            // 4 params of queue: name, is persistence (hàng đợi bền), is monopoly (độc quyền),
            // auto delete (tu dong xoa) not info map
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, topicExchangeName, routingKey);
            channel.basicPublish(topicExchangeName, routeKey, null, mess.getBytes(StandardCharsets.UTF_8));

            logger.info("Thead Id {}: [Success: Send message to Queue {} exchangeName: {}, routingKey: {}, value [{}]]",
                    Thread.currentThread().getId(),
                    queueName,
                    topicExchangeName,
                    routingKey,
                    mess);

            channel.close();
        } catch (IOException ioException) {

            logger.error("Thread Id {}: IOException",
                    Thread.currentThread().getId(),
                    ioException);

        } catch (TimeoutException timeoutException) {

            logger.error("Thread Id {} TimeoutException: ",
                    Thread.currentThread().getId(),
                    timeoutException);

        } catch (NullPointerException exception) {

            logger.error("Thread Id {} NullPoiterException: ",
                    Thread.currentThread().getId(),
                    exception);

        } catch (Exception exception) {
            logger.error("Thread Id {} Exception: ",
                    Thread.currentThread().getId(),
                    exception);
        }
    }

}
