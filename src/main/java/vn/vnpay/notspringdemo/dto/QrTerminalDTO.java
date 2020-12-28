package vn.vnpay.notspringdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrTerminalDTO {

    private Long id;

    private String terminalId;

    private String terminalName;

    private String terminalAddress;

    private String merchantId;

    public void convertQrTerminal(ResultSet resultSet) throws SQLException{

            setId(Long.parseLong(resultSet.getString("ID")));
            setTerminalId(resultSet.getString("TERMINAL_ID"));
            setMerchantId(resultSet.getString("MERCHANT_ID"));
            setTerminalName(resultSet.getString("TERMINAL_NAME"));
            setTerminalAddress(resultSet.getString("TERMINAL_ADDRESS"));
    }

}
