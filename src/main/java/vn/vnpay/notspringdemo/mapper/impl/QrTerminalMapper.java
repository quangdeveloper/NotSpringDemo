package vn.vnpay.notspringdemo.mapper.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.notspringdemo.dto.QrTerminalDTO;
import vn.vnpay.notspringdemo.mapper.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QrTerminalMapper implements RowMapper<QrTerminalDTO> {

    private final Logger logger = LoggerFactory.getLogger(QrTerminalMapper.class);

    @Override
    public QrTerminalDTO mapRow(ResultSet resultSet) {

        try {
            Long id = resultSet.getLong("ID");
            String terminalId = resultSet.getString("TERMINAL_ID");
            String terminalName = resultSet.getString("TERMINAL_NAME");
            String terminalAddress = resultSet.getString("TERMINAL_ADDRESS");
            String merchantId = resultSet.getString("MERCHANT_ID");
            return new QrTerminalDTO(id, terminalId, terminalName, terminalAddress, merchantId);

        } catch (SQLException ex) {
            logger.error("Thread Id {}: SQLException code {}", Thread.currentThread().getId(), ex);
            return null;
        }
    }
}
