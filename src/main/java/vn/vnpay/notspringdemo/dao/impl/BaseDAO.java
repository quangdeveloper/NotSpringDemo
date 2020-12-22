package vn.vnpay.notspringdemo.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.util.Map;

public class BaseDAO {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected DataSource dataSource;

    protected final String PK_TEST = "PKG_QUANGNV";

    protected Map<String, Object> callFunction(String packageName, String functionName, Map<String, Object> inParams) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withCatalogName(packageName);
        return simpleJdbcCall.withFunctionName(functionName).execute(inParams);
    }

    protected Map<String, Object> callProcedure(String packageName, String procedureName, Map<String, Object> inParams) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withCatalogName(packageName);
        return simpleJdbcCall.withProcedureName(procedureName).execute(inParams);
    }
}
