package vn.vnpay.notspringdemo.dao.impl;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;
import vn.vnpay.notspringdemo.mapper.RowMapper;
import vn.vnpay.notspringdemo.model.ParameterORA;

import java.util.List;
import java.util.Map;

@Component
public class QrTerminalDAOImpl<T> extends BaseDAO implements QrTerminalDAO<T> {

    Logger logger = LoggerFactory.getLogger(QrTerminalDAOImpl.class);

    @Override
    public Map<String, Object> searchQrTerminal(List<ParameterORA> parameterORAs, RowMapper<T> rowMapper) {

        String sqlQuery = "{call PKG_QUANGNV.SEARCH_QRTERMINAL(?, ?, ?, ?, ?)}";

//        StringBuilder stringBuilder = new StringBuilder();
//        parameterORAs.forEach(
//                param -> stringBuilder.append(param.getName() + ": " + param.getValue() + "\t"));
//
//        logger.info("Token [{}] : SQL query {} input param {}",
//                ThreadContext.get("token"),
//                sqlQuery,
//                stringBuilder);

        return callProcedure(sqlQuery, parameterORAs, rowMapper);
    }
}
