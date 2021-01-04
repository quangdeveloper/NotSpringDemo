package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Service
public class RabbitService {

    Logger logger = LoggerFactory.getLogger(RabbitService.class);

    @Value("${rabbitMQ.queueName}")
    private String queueName;

    @Value("${rabbitMQ.topicExchangeName}")
    private String topicExchangeName;

    @Value("${rabbitMQ.routingKey}")
    private String routingKey;

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

    private static GenericObjectPool<Channel> channelPool;

    @PostConstruct
    private GenericObjectPool<Channel> createChannelPool() {

        GenericObjectPoolConfig<Channel> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictRuns);
        genericObjectPoolConfig.setBlockWhenExhausted(true);

        PoolChannelRabbitMQFactory factory = new PoolChannelRabbitMQFactory();
        channelPool = new GenericObjectPool<>(factory);
        channelPool.setConfig(genericObjectPoolConfig);
        logger.info("Created Rabbit MQ Channel pool {}", channelPool.getJmxName());
        return channelPool;
    }

    public void sendMessage(String mess, String routeKey) {
        Channel channel = null;
        try {

            channel = channelPool.borrowObject();

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

        } catch (IOException ioException) {

            logger.error("Thread Id {}: IOException",
                    Thread.currentThread().getId(),
                    ioException);
        } catch (NullPointerException exception) {

            logger.error("Thread Id {} NullPointerException: ",
                    Thread.currentThread().getId(),
                    exception);

        } catch (Exception exception) {
            logger.error("Thread Id {} Exception: ",
                    Thread.currentThread().getId(),
                    exception);
        }
        if (channel != null) {
            channelPool.returnObject(channel);
        }
    }

}
