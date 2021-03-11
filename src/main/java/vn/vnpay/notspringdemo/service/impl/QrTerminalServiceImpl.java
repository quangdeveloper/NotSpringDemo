package vn.vnpay.notspringdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
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
import vn.vnpay.notspringdemo.mapper.impl.QrTerminalMapper;
import vn.vnpay.notspringdemo.model.ParameterORA;
import vn.vnpay.notspringdemo.service.QrTerminalService;
import vn.vnpay.notspringdemo.util.Constant;
import vn.vnpay.notspringdemo.util.GsonUtil;

import java.util.*;

@Service
@Slf4j
public class QrTerminalServiceImpl implements QrTerminalService {

    private final Logger logger = LoggerFactory.getLogger(QrTerminalServiceImpl.class);

    @Value("${rabbitMQ.routing-key-one}")
    private String routingKeyOne;

    @Autowired
    private QrTerminalDAO qrTerminalDAO;

    @Autowired
    private RedisClientService redisService;

    @Autowired
    private RabbitService rabbitService;

    /**
     * khong the cast result nhan dc trong  result.get("RESULTS") thanh resultset vi ket qua nhan dc la kieu ArrayList()
     *
     * @param pageNo   number page
     * @param pageSize item per page
     * @return PageDTO
     */
    @Override
    public <T> PageDTO searchQrTerminal(Long pageNo, Long pageSize, String keyword) {

        List<ParameterORA> parameterORAs = new ArrayList<>();
        ParameterORA parameterORA1 = new ParameterORA("PI_PAGE_NO", pageNo, "LONG");
        ParameterORA parameterORA2 = new ParameterORA("PI_PAGE_SIZE", pageSize, "LONG");
        parameterORAs.add(parameterORA1);
        parameterORAs.add(parameterORA2);
        Map<String, Object> resultMap = qrTerminalDAO.searchQrTerminal(parameterORAs, new QrTerminalMapper());

        List<QrTerminalDTO> listResult = (List<QrTerminalDTO>) resultMap.get("RESULTS");
        final long totalResult = Long.parseLong(resultMap.get("PO_TOTAL").toString());

        listResult.forEach(qrterminal -> {

            try {
                Boolean isSetKey = redisService
                        .setKey(qrterminal.getTerminalId() + qrterminal.getMerchantId(),
                                GsonUtil.toJson(qrterminal));
                int i = 0;
                while (i < 3 && isSetKey == Boolean.FALSE) {
                    isSetKey = redisService
                            .setKey(qrterminal.getTerminalId() + qrterminal.getMerchantId(),
                                    GsonUtil.toJson(qrterminal));
                    i++;
                }
                if (isSetKey == Boolean.TRUE) {

                    rabbitService.sendMessage(GsonUtil.toJson(qrterminal), routingKeyOne);
                }
            } catch (Exception exception) {
                logger.error("Token [{}]: Exception ", ThreadContext.get("token"), exception);
            }
        });

        return PageDTO.builder()
                .total(totalResult)
                .content(listResult)
                .build();
    }

    @Override
    public Object searchQrTerminalOnRedis(String key) {

        if (!redisService.hasKey(key)) {
            throw new GeneralException(Constant.RESPONSE.CODE.C404, Constant.RESPONSE.MESSAGE.DATA_NOT_EXISTS);
        }

        String value = redisService.getKey(key).toString();
        QrTerminalDTO qrTerminalDTO = GsonUtil.fromJson(value, QrTerminalDTO.class);

        logger.info("Token [{}] : [Result Object:  content: {} ]",
                ThreadContext.get("token"),
                value);

        return PageDTO.builder()
                .total(1L)
                .content(qrTerminalDTO)
                .build();
    }

}

