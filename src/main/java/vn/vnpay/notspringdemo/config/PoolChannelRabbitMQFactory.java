package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class PoolChannelRabbitMQFactory extends BasePooledObjectFactory<Channel> {

    private final Logger logger = LoggerFactory.getLogger(PoolChannelRabbitMQFactory.class);

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

    private static Connection connection;

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

    @PostConstruct
    public void getConnection() {
        try {
            connection = getConnectionFactory().newConnection();
            connection.createChannel();
            logger.info("Thread id {}: Create rabbit connection successful with connection id is {} ",
                    Thread.currentThread().getId(),
                    connection.getAddress());
        } catch (IOException ioException) {

            logger.error("Thread Id {} IOException: ",
                    Thread.currentThread().getId(),
                    ioException);

        } catch (TimeoutException timeoutException) {

            logger.error("Thread Id {} TimeoutException: ",
                    Thread.currentThread().getId(),
                    timeoutException);
        }
    }

    /**
     * dùng để tạo channels
     * @return channel of rabbit mq
     * @throws Exception ex
     */
    @Override
    public Channel create() throws Exception {
        Channel channel = connection.createChannel();

        logger.info("Thread Id {} Created channel rabbit mq: {}",
                Thread.currentThread().getId(),
                channel.getChannelNumber());
        return channel;
    }

    /**
     * dùng để tạo ra 1 PoolObject
     * @param channel channel
     * @return PoolObject
     */
    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<>(channel);
    }
}
