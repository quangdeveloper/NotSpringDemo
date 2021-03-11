package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.ThreadContext;
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

    @Value("${rabbitMQ.queue-name}")
    private String queueName;

    @Value("${rabbitMQ.topic-exchange-name}")
    private String topicExchangeName;

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
            channel.basicPublish(topicExchangeName, routeKey, null, mess.getBytes(StandardCharsets.UTF_8));

            logger.info("Token [{}]:  Send message {} success",
                    ThreadContext.get("token"),
                    mess);

        } catch (IOException ioException) {

            logger.error("Token [{}] IOException: ",
                    ThreadContext.get("token"),
                    ioException);
        } catch (NullPointerException exception) {

            logger.error("Token [{}] NullPointerException: ",
                    ThreadContext.get("token"),
                    exception);

        } catch (Exception exception) {
            logger.error("Token [{}] Exception: ",
                    ThreadContext.get("token"),
                    exception);
        }
        if (channel != null) {
            channelPool.returnObject(channel);
        }
    }

}
