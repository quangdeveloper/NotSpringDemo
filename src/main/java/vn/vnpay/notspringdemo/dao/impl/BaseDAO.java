package vn.vnpay.notspringdemo.dao.impl;

import oracle.jdbc.OracleType;
import oracle.jdbc.internal.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseDAO extends PackageDAO {

    Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    @Autowired
    protected DataSource dataSource;


    protected Map<String, Object> callProcedure(String sqlQuery, Map<String, Object> inParams) {

        logger.info("Thead Id {}: [ Call procedure: {}]", Thread.currentThread().getId(), sqlQuery);
        Map<String, Object> mapResult = new HashMap<>();
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = dataSource.getConnection();
            logger.info("Thread Id {}: get Connection  {}", Thread.currentThread().getId(), connection.toString());
            callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.setInt("PI_PAGE_NO",Integer.parseInt(inParams.get("PI_PAGE_NO").toString()));
            callableStatement.setInt("PI_PAGE_SIZE",Integer.parseInt(inParams.get("PI_PAGE_SIZE").toString()));
            callableStatement.registerOutParameter("RESULTS", OracleTypes.CURSOR);
            callableStatement.registerOutParameter("PO_CODE", Types.NVARCHAR);
            callableStatement.registerOutParameter("PO_TOTAL", Types.NUMERIC);
            callableStatement.execute();

            mapResult.put("PO_CODE", callableStatement.getObject("PO_CODE"));
            mapResult.put("PO_TOTAL", callableStatement.getObject("PO_TOTAL"));
            mapResult.put("RESULTS", callableStatement.getObject("RESULTS"));
            return mapResult;

        } catch (SQLException sqlException) {
            logger.error("Thread Id {}: SQLException ", Thread.currentThread().getId());
            sqlException.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                logger.error("Thread Id {} : SQLException ", Thread.currentThread().getId());
                e.printStackTrace();
            } catch (NullPointerException nullPointerException) {
                logger.error("Thread Id {} : NullPointerException ", Thread.currentThread().getId());
                nullPointerException.printStackTrace();
            }
            return null;
        }
    }


    private void setParameters(Map<String, Object> map, CallableStatement callableStatement) {

        Set<String> keys = map.keySet();
        for (String key : keys) {
            try {
                if (StringUtils.startsWithIgnoreCase(key, "PI")) {
                    if (map.get(key) instanceof Integer) {
                        callableStatement.setInt(key, Integer.parseInt(map.get(key).toString()));
                    } else if (map.get(key) instanceof String) {
                        callableStatement.setString(key, map.get(key).toString());
                    } else if (map.get(key) instanceof BigDecimal) {
                        long kt = Long.parseLong(map.get(key).toString());
                        callableStatement.setBigDecimal(key, BigDecimal.valueOf(kt));
                    }
                }
            } catch (SQLException sqlException) {
                logger.error("Thread Id {}: SQLException ", Thread.currentThread().getId());
                sqlException.printStackTrace();
            }
        }
    }

    private void setParameter(PreparedStatement statement, Object... parameter) {
        try {
            for (int i = 0; i < parameter.length; i++) {
                Object para = parameter[i];
                int index = i + 1;
                //kiem tra neu tham so la kieu Long
                if (para instanceof Long) {
                    statement.setLong(index, (Long) para);
                }
                //kiem tra neu tham so la kieu Integer
                else if (para instanceof Integer) {
                    statement.setInt(index, (Integer) para);
                }
                //kiem tra new tham so truyen vao la kieu Double
                else if (para instanceof Double) {
                    statement.setDouble(index, (Double) para);
                }
                //kiem tra neu tham so la kieu String
                else if (para instanceof String) {
                    statement.setString(index, (String) para);
                }
                //kiem tra neu tham so la kieu Timestamp
                else if (para instanceof Timestamp) {//
                    statement.setTimestamp(index, (Timestamp) para);
                }
                //kiem tra neu tham so la kieu Boolean
                else if (para instanceof Boolean) {
                    statement.setBoolean(index, (Boolean) para);
                }
                //neu tham so la null
                else {
                    statement.setNull(index, 0);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void close(Connection con, PreparedStatement pre, ResultSet rs) {
        try {
            if (con != null) {
                con.close();
            }
            if (pre != null) {
                pre.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
