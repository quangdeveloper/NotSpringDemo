package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.vnpay.notspringdemo.exception.GeneralException;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class PoolChannelRabbitMQFactory extends BasePooledObjectFactory<Channel> {

    private final Logger logger = LoggerFactory.getLogger(PoolChannelRabbitMQFactory.class);

    private static String exchangeName;
    private static String exchangeType;
    private static String queueName;
    private static String routingPattern;

    @Value("${rabbitMQ.topic-exchange-name}")
    private String exchangeNameEnv;

    @Value("${rabbitMQ.queue-name}")
    private String queueNameEnv;

    @Value("${rabbitMQ.routing-key}")
    private String routingKeyEnv;

    @Value("${rabbitMQ.exchange-type}")
    private String exchangeTypeEnv;

    void setDataEnv() {
        exchangeName = exchangeNameEnv;
        exchangeType = exchangeTypeEnv;
        queueName = queueNameEnv;
        routingPattern = routingKeyEnv;
    }

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
    public void initConnection() {
        setDataEnv();
        try {
            connection = getConnectionFactory().newConnection();
            int i = 0;
            while (i < 3 && connection == null) {
                connection = getConnectionFactory().newConnection();
                i++;
            }
            if (connection == null) {
                throw new GeneralException("C999", "Rabbit MQ can't connect");
            }

            logger.info("Token [{}] : Create rabbit connection successful with connection id is {} ",
                    ThreadContext.get("token"),
                    connection.getAddress());

        } catch (IOException ioException) {

            logger.error("Token [{}]  IOException: ",
                    ThreadContext.get("token"),
                    ioException);

        } catch (TimeoutException timeoutException) {

            logger.error("Token [{}]  TimeoutException: ",
                    ThreadContext.get("token"),
                    timeoutException);
        }
    }

    /**
     * dùng để tạo channels
     *
     * @return channel of rabbit mq
     * @throws Exception ex
     */
    @Override
    public Channel create() throws Exception {

        if (connection != null) {
            Channel channel = connection.createChannel();
            // 3 params of exchange: name , type, unable auto delete
            channel.exchangeDeclare(exchangeName, exchangeType, true);

            // 4 params of queue: name, is persistence (hàng đợi bền), is monopoly (độc quyền),
            // auto delete (tu dong xoa) not info map
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, exchangeName, routingPattern);

            logger.info("Created channel rabbit mq: {}", channel.getChannelNumber());
            return channel;
        } else {
            throw new GeneralException("C404", "Rabbit MQ can't connect");
        }
    }

    /**
     * dùng để tạo ra 1 PoolObject
     *
     * @param channel channel
     * @return PoolObject
     */
    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<>(channel);
    }
}
