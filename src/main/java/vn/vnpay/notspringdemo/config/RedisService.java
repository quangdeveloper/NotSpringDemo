package vn.vnpay.notspringdemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.vnpay.notspringdemo.util.GsonUtil;

@Service
public class RedisService {

    private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setKey(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, GsonUtil.toJson(value));
            logger.info("Set key {} successful. Value is: {}", key, value);
        } catch (Exception exception) {
            logger.error("Set key {} failed: {} because: {}", key, value, exception.getMessage());
        }
    }

    public Boolean setKeyCustom(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, GsonUtil.toJson(value));
            logger.info("Set key {} successful. Value is: {}", key, value);
            return Boolean.TRUE;
        } catch (Exception exception) {
            logger.error("Set key {} failed: {} because: {}", key, value, exception.getMessage());
            return Boolean.FALSE;
        }
    }

    public Object getKey(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            logger.info("Redis get key = {} value = ({})", key,redisTemplate.opsForValue().get(key));
            return redisTemplate.opsForValue().get(key);

        } else {
            logger.warn("Key {}  not exists!!!!", key);
            return null;
        }
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
