package vn.vnpay.notspringdemo.dao.impl;

import oracle.jdbc.internal.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import vn.vnpay.notspringdemo.model.ParameterORA;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDAO extends PackageDAO {

    Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    @Autowired
    protected DataSource dataSource;

    protected Map<String, Object> callProcedure(String sqlQuery, List<ParameterORA> parameterORAs) {

        logger.info("Thead Id {}: [ Call procedure: {}]", Thread.currentThread().getId(), sqlQuery);
        Map<String, Object> mapResult = new HashMap<>();
        Connection connection = null;
        CallableStatement callableStatement;
        try {
            connection = dataSource.getConnection();

            logger.info("Thread Id {}: get Connection  {}", Thread.currentThread().getId(), connection.toString());

            callableStatement = connection.prepareCall(sqlQuery);

            setParameters(parameterORAs, callableStatement);
            callableStatement.registerOutParameter("RESULTS", OracleTypes.CURSOR);
            callableStatement.registerOutParameter("PO_CODE", Types.NVARCHAR);
            callableStatement.registerOutParameter("PO_TOTAL", Types.NUMERIC);
            callableStatement.execute();

            mapResult.put("PO_CODE", callableStatement.getObject("PO_CODE"));
            mapResult.put("PO_TOTAL", callableStatement.getObject("PO_TOTAL"));
            ResultSet resultSet = (ResultSet) callableStatement.getObject("RESULTS");
            mapResult.put("RESULTS", resultSet);
            return mapResult;

        } catch (SQLException sqlException) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                logger.error("Thread Id {} : SQLException ", Thread.currentThread().getId());
                e.printStackTrace();
            }
            logger.error("Thread Id {}: SQLException ", Thread.currentThread().getId());
            sqlException.printStackTrace();
            return null;
        }
    }

    public void setParameters(List<ParameterORA> parameterORAs, CallableStatement callableStatement) {

        logger.info("Thread Id {} : Convert input param [{}] ",
                Thread.currentThread().getId(),
                parameterORAs);
        try {
            for (ParameterORA parameterORA : parameterORAs) {

                if (parameterORA.getType().toUpperCase().equals("LONG")) {

                    long value = Long.parseLong(parameterORA.getValue().toString());
                    callableStatement.setLong(parameterORA.getName(), value);

                } else if (parameterORA.getType().toUpperCase().equals("INTEGER")) {

                    int value = Integer.parseInt(parameterORA.getValue().toString());
                    callableStatement.setInt(parameterORA.getName(), value);

                } else if (parameterORA.getType().toUpperCase().equals("STRING")) {

                    String value = parameterORA.getValue().toString();
                    callableStatement.setString(parameterORA.getName(), value);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

}
