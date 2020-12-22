package vn.vnpay.notspringdemo.dao.impl;

import org.springframework.stereotype.Component;
import vn.vnpay.notspringdemo.dao.QrTerminalDAO;

import java.util.Map;

@Component
public class QrTerminalDAOImpl extends BaseDAO implements QrTerminalDAO {

    @Override
    public Map<String, Object> searchQrTerminalByCondition(Map<String,Object> inParams) {
        return  callProcedure(PK_TEST, "SEARCH_BY_CONDITION", inParams);
    }
}
