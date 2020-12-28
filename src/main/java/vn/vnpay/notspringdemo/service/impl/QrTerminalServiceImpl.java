package vn.vnpay.notspringdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.vnpay.notspringdemo.config.RabbitService;
import vn.vnpay.notspringdemo.config.RedisClientService;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;
import vn.vnpay.notspringdemo.dto.PageDTO;
import vn.vnpay.notspringdemo.dto.QrTerminalDTO;
import vn.vnpay.notspringdemo.exception.GeneralException;
import vn.vnpay.notspringdemo.model.ParameterORA;
import vn.vnpay.notspringdemo.service.QrTerminalService;
import vn.vnpay.notspringdemo.util.Constant;
import vn.vnpay.notspringdemo.util.GsonUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class QrTerminalServiceImpl implements QrTerminalService {

    private final Logger logger = LoggerFactory.getLogger(QrTerminalServiceImpl.class);

    @Value("${rabbitMQ.topicExchangeName}")
    private String topicExchangeName;

    @Value("${rabbitMQ.routingKeyOne}")
    private String routingKeyOne;

    @Autowired
    private QrTerminalDAO qrTerminalDAO;

    @Autowired
    private RedisClientService redisService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * khong the cast result nhan dc trong  result.get("RESULTS") thanh resultset vi ket qua nhan dc la kieu ArrayList();
     *
     * @param pageNo   number page
     * @param pageSize item per page
     * @return PageDTO
     */
    @Override
    public PageDTO searchQrTerminal(Long pageNo, Long pageSize) {

        List<ParameterORA> parameterORAs = new ArrayList<>();
        ParameterORA parameterORA1 = new ParameterORA("PI_PAGE_NO", pageNo, "LONG");
        ParameterORA parameterORA2 = new ParameterORA("PI_PAGE_SIZE", pageSize, "LONG");
        parameterORAs.add(parameterORA1);
        parameterORAs.add(parameterORA2);
        Map<String, Object> resultMap = qrTerminalDAO.searchQrTerminalByCondition(parameterORAs);

        ResultSet resultSet = (ResultSet) resultMap.get("RESULTS");
        final long totalResult = Long.parseLong(resultMap.get("PO_TOTAL").toString());

        List<QrTerminalDTO> qrTerminalDTOS = new ArrayList<>();
        try {
            while (resultSet.next()) {
                QrTerminalDTO qrTerminalDTO = new QrTerminalDTO();
                qrTerminalDTO.convertQrTerminal(resultSet);
                qrTerminalDTOS.add(qrTerminalDTO);
                try {
                    Boolean isSetKey = redisService
                            .setKey(qrTerminalDTO.getTerminalId() + qrTerminalDTO.getMerchantId(), GsonUtil.toJson(qrTerminalDTO));
                    int i = 0;
                    while (i < 3 && isSetKey == Boolean.FALSE) {
                        isSetKey = redisService
                                .setKey(qrTerminalDTO.getTerminalId() + qrTerminalDTO.getMerchantId(), GsonUtil.toJson(qrTerminalDTO));
                        i++;
                    }
                    if (isSetKey == Boolean.TRUE) {

                        logger.info("Thread Id {} : [Push Rabbit MQ Queue: exchangeName: {} routing-key: {} value: [{}] ]",
                                Thread.currentThread().getId(),
                                topicExchangeName,
                                routingKeyOne,
                                GsonUtil.toJson(qrTerminalDTO));

                        rabbitService.sendMessage(GsonUtil.toJson(qrTerminalDTO), routingKeyOne);
                    }
                } catch (Exception ex) {
                    logger.error("Thread Id {} : Exception ", Thread.currentThread().getId());
                    ex.printStackTrace();
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        logger.info("Thread Id {} : [Result PageDTO: [totalItem: {}, content: {} ]]",
                Thread.currentThread().getId(),
                totalResult,
                qrTerminalDTOS);

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

        String value = redisService.getKey(key).toString();
        QrTerminalDTO qrTerminalDTO = GsonUtil.fromJson(value, QrTerminalDTO.class);

        logger.info("Thread Id {} : [Result Object:  content: {} ]",
                Thread.currentThread().getId(),
                value);

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