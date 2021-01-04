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

        StringBuilder stringBuilder = new StringBuilder();
        parameterORAs.forEach(
                param -> stringBuilder.append(param.getName() + ": " + param.getValue() + "\t"));

        logger.info("Thread Id {} : SQL query {} input param {}",
                Thread.currentThread().getId(),
                sqlQuery,
                stringBuilder);

        return callProcedure(sqlQuery, parameterORAs);
    }
}
