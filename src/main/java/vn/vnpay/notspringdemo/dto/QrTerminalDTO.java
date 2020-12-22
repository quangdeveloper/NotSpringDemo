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

    public void convertQrTerminal(Map map) {
        setId(Long.parseLong(map.get("ID").toString()));
        setTerminalId(map.get("TERMINAL_ID").toString());
        setMerchantId(map.get("MERCHANT_ID").toString());
        setTerminalName(map.get("TERMINAL_NAME") == null ? " " : map.get("TERMINAL_NAME").toString());
        setTerminalAddress(map.get("TERMINAL_ADDRESS") == null ? " " : map.get("TERMINAL_ADDRESS").toString());
    }

    public void convertQrTerminal(ResultSet resultSet) throws SQLException{

            setId(Long.parseLong(resultSet.getString("ID")));
            setTerminalId(resultSet.getString("TERMINAL_ID"));
            setMerchantId(resultSet.getString("MERCHANT_ID"));
            setTerminalName(resultSet.getString("TERMINAL_NAME"));
            setTerminalAddress(resultSet.getString("TERMINAL_ADDRESS"));
    }

}
