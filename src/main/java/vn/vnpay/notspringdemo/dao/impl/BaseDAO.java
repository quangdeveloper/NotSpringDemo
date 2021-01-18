package vn.vnpay.notspringdemo.dao.impl;

import com.zaxxer.hikari.HikariDataSource;
import oracle.jdbc.internal.OracleTypes;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vn.vnpay.notspringdemo.exception.GeneralException;
import vn.vnpay.notspringdemo.mapper.RowMapper;
import vn.vnpay.notspringdemo.model.ParameterORA;
import vn.vnpay.notspringdemo.util.Constant;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class BaseDAO<T> extends PackageDAO {

    Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    @Autowired
    protected DataSource dataSource;

    protected  Map<String, Object> callProcedure(String sqlQuery,
                                                List<ParameterORA> parameterORAs,
                                                RowMapper<T> rowMapper) {

        logger.info("Token [{}]: [ Call procedure: {}]", ThreadContext.get("token"), sqlQuery);
        Map<String, Object> mapResult = new HashMap<>();
        Connection connection = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.dataSource.getConnection();

            logger.info("Token [{}] : get Connection  {}", ThreadContext.get("token"), connection.toString());

            callableStatement = connection.prepareCall(sqlQuery);

            setParameters(parameterORAs, callableStatement);
            callableStatement.registerOutParameter("RESULTS", OracleTypes.CURSOR);
            callableStatement.registerOutParameter("PO_CODE", Types.NVARCHAR);
            callableStatement.registerOutParameter("PO_TOTAL", Types.NUMERIC);
            callableStatement.execute();

            mapResult.put("PO_CODE", callableStatement.getObject("PO_CODE"));
            mapResult.put("PO_TOTAL", callableStatement.getObject("PO_TOTAL"));
            resultSet = (ResultSet) callableStatement.getObject("RESULTS");

            mapResult.put("RESULTS", getData(rowMapper, resultSet));
            return mapResult;

        } catch (SQLException sqlException) {

            try {
                if (connection != null) {
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException exception1) {
                logger.error("Token [{}]  : SQLException ", ThreadContext.get("token"), exception1);
            }
            logger.error("Token [{}] : SQLException ", ThreadContext.get("token"), sqlException);
            return null;
        } finally {
            close(connection, callableStatement, resultSet);
        }
    }

    public List<T> getData(RowMapper<T> rowMapper, ResultSet resultSet) {
        List<T> results = new ArrayList<>();
        try {
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
        } catch (SQLException sqlException) {
            logger.error("Token [{}] SQL exception: ", ThreadContext.get("token") ,sqlException);
        }
        return results;
    }


    private void close(Connection connection, CallableStatement callableStatement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (callableStatement != null) {
                callableStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            UUID uuid = UUID.randomUUID();
            logger.error("SQL exception: ", sqlException);
        }
    }

    public void setParameters(List<ParameterORA> parameterORAs, CallableStatement callableStatement) {

        try {
            for (ParameterORA parameterORA : parameterORAs) {

                if (parameterORA.getType().equalsIgnoreCase("LONG")) {

                    long value = Long.parseLong(parameterORA.getValue().toString());
                    callableStatement.setLong(parameterORA.getName(), value);

                } else if (parameterORA.getType().equalsIgnoreCase("INTEGER")) {

                    int value = Integer.parseInt(parameterORA.getValue().toString());
                    callableStatement.setInt(parameterORA.getName(), value);

                } else if (parameterORA.getType().equalsIgnoreCase("STRING")) {

                    String value = parameterORA.getValue().toString();
                    callableStatement.setString(parameterORA.getName(), value);
                } else {
                    String str = String.format("Type param %s not supported", parameterORA.getName());
                    throw new GeneralException(Constant.RESPONSE.CODE.C405_PARAMETER, str);
                }
            }
        } catch (SQLException sqlException) {
            logger.error("Token [{}] : SQLException ", ThreadContext.get("token"), sqlException);
        }
    }

}
