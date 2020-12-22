package vn.vnpay.notspringdemo.config;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


@Service
public class RabbitService {

    Logger logger = LoggerFactory.getLogger(RabbitService.class);

    public void send(String queue, String topic, String exchange, Object obj) {

    }

}
