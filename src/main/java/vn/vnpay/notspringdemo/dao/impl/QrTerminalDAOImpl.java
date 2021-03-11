package vn.vnpay.notspringdemo.dao.impl;

import org.springframework.stereotype.Component;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;
import vn.vnpay.notspringdemo.mapper.RowMapper;
import vn.vnpay.notspringdemo.model.ParameterORA;

import java.util.List;
import java.util.Map;

@Component
public class QrTerminalDAOImpl<T> extends BaseDAO<T> implements QrTerminalDAO<T> {


    @Override
    public Map<String, Object> searchQrTerminal(List<ParameterORA> parameterORAs, RowMapper<T> rowMapper) {

        String sqlQuery = "{call PKG_QUANGNV.SEARCH_QRTERMINAL(?, ?, ?, ?, ?)}";
        return callProcedure(sqlQuery, parameterORAs, rowMapper);
    }
}
