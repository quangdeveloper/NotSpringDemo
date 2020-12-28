package vn.vnpay.notspringdemo.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;
import vn.vnpay.notspringdemo.model.ParameterORA;
import java.util.List;
import java.util.Map;

@Component
public class QrTerminalDAOImpl extends BaseDAO implements QrTerminalDAO {

    Logger logger = LoggerFactory.getLogger(QrTerminalDAOImpl.class);

    @Override
    public Map<String, Object> searchQrTerminalByCondition(List<ParameterORA> parameterORAs) {

        String sqlQuery = "{call PKG_QUANGNV.SEARCH_QRTERMINAL(?, ?, ?, ?, ?)}";

        logger.info("Thread Id {} : SQL query [{}] input param {}",
                Thread.currentThread().getId(),
                sqlQuery,
                parameterORAs.toString());

        return callProcedure(sqlQuery, parameterORAs);
    }
}
