package vn.vnpay.notspringdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.stereotype.Service;
import vn.vnpay.notspringdemo.config.RedisService;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;
import vn.vnpay.notspringdemo.dto.PageDTO;
import vn.vnpay.notspringdemo.dto.QrTerminalDTO;
import vn.vnpay.notspringdemo.exception.GeneralException;
import vn.vnpay.notspringdemo.service.QrTerminalService;
import vn.vnpay.notspringdemo.util.Constant;
import vn.vnpay.notspringdemo.util.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QrTerminalServiceImpl implements QrTerminalService
{

    private final Logger logger = LoggerFactory.getLogger(QrTerminalServiceImpl.class);

    @Value("${rabbitMQ.topicExchangeName}")
    private String topicExchangeName;

    @Value("${rabbitMQ.routingKeyOne}")
    private String routingKeyOne;

    @Autowired
    private QrTerminalDAO qrTerminalDAO;

    @Autowired
    private RedisService redisService;

    /**
     * khong the cast result nhan dc trong  result.get("RESULTS") thanh resultset vi ket qua nhan dc la kieu ArrayList();
     *
     * @param pageNo   number page
     * @param pageSize item per page
     * @return PageDTO
     */
    @Override
    @SuppressWarnings("unchecked")
    public PageDTO searchQrTerminal(Long pageNo, Long pageSize) {

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("PI_PAGE_NO", pageNo);
        inParams.put("PI_PAGE_SIZE", pageSize);
        Map<String, Object> result = qrTerminalDAO.searchQrTerminalByCondition(inParams);

        List<Map<String, Object>> listMap = (List<Map<String, Object>>) result.get("RESULTS");
        final long totalResult = Long.parseLong(result.get("PO_TOTAL").toString());

        List<QrTerminalDTO> qrTerminalDTOS = new ArrayList<>();
        for (Map<String, Object> mapResult : listMap) {

            QrTerminalDTO qrTerminalDTO = new QrTerminalDTO();
            qrTerminalDTO.convertQrTerminal(mapResult);
            qrTerminalDTOS.add(qrTerminalDTO);
            try {
                Boolean checkSetKey = redisService
                        .setKeyCustom(qrTerminalDTO.getTerminalId() + qrTerminalDTO.getMerchantId(), qrTerminalDTO);
                int i = 0;
                while (i < 3 && checkSetKey == Boolean.FALSE) {
                    checkSetKey = redisService
                            .setKeyCustom(qrTerminalDTO.getTerminalId() + qrTerminalDTO.getMerchantId(), qrTerminalDTO);
                    i++;
                }
                if (checkSetKey == Boolean.TRUE) {
                    //su dung cho exchange type: direct, fanout, topicExchange
                    logger.info("Push Rabbit MQ Queue: exchangeName: {} routing-key: {} value: [{}]",
                            topicExchangeName, routingKeyOne, GsonUtil.toJson(qrTerminalDTO));
                }
            } catch (Exception ex) {
                logger.error("Exception: ", ex);
            }
        }
        try {
            Thread.sleep(5000);
        } catch (Exception ex) {

        }
        logger.info("Result PageDTO: [totalItem: {}, content: {} ]", totalResult, qrTerminalDTOS);
        return PageDTO.builder()
                .total(totalResult)
                .content(qrTerminalDTOS)
                .build();
    }

    @Override
    public Object searchQrTerminalOnRedis(String key) {

        if (!redisService.hasKey(key)) {
            throw new GeneralException(Constant.RESPONSE.CODE.C404, Constant.RESPONSE.MESSAGE.DATA_NOT_EXISTS);
        }
        QrTerminalDTO qrTerminalDTO = GsonUtil.fromJson(redisService.getKey(key).toString(), QrTerminalDTO.class);
        return PageDTO.builder()
                .total(1L)
                .content(qrTerminalDTO)
                .build();
    }

}


// su dung cho exchange type: header
//                        MessageProperties messageProperties = new MessageProperties();
//                        messageProperties.setHeader("name-control","yourself");
//                        MessageConverter messageConverter = new SimpleMessageConverter();
//                        Message message = messageConverter.toMessage(qrTerminal, messageProperties);
//                        rabbitTemplate.send(message);