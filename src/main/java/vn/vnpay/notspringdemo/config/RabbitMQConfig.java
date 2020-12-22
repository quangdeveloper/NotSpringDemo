package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


@Configuration
public class RabbitMQConfig {

    Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${rabbitMQ.queueName}")
    private String queueName;

    @Value("${rabbitMQ.exchangeName}")
    private String topicExchangeName;

    @Value("${rabbitMQ.routingKey}")
    private String routingKey;

    @Bean
    ConnectionFactory connectionFactory() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.22.7.88");
        factory.setPort(6789);
        factory.setUsername("qrcode");
        factory.setPassword("qrcode");
        factory.setVirtualHost("qrcode");
        factory.setRequestedHeartbeat(45);
        return factory;
    }

    void sendMessage(String mess) {

        try {
            Channel channel = connectionFactory().newConnection().createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("",queueName,null, mess.getBytes(StandardCharsets.UTF_8));
            logger.info("Success: Send message to Queue {} value [{}] ", queueName);

        } catch (IOException ioException) {
            logger.error("IOException: ");
            ioException.printStackTrace();
        } catch (TimeoutException timeoutException) {
            logger.error("TimeOutException: ");
            timeoutException.printStackTrace();
        }
    }
}
