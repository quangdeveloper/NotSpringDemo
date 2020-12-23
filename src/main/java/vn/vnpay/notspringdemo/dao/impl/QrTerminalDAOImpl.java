package vn.vnpay.notspringdemo.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;

import java.sql.ResultSet;
import java.util.Map;

@Component
public class QrTerminalDAOImpl extends BaseDAO implements QrTerminalDAO {

    Logger logger = LoggerFactory.getLogger(QrTerminalDAOImpl.class);

    @Override
    public Map<String, Object> searchQrTerminalByCondition(Map<String, Object> inParams) {

        String sqlQuery = "{call PKG_QUANGNV.SEARCH_QRTERMINAL(?, ?, ?, ?, ?)}";

        logger.info("Thread Id {} : SQL query [{}] param {}",
                Thread.currentThread().getId(),
                sqlQuery,
                inParams.toString());

        return callProcedure(sqlQuery, inParams);
    }
}
