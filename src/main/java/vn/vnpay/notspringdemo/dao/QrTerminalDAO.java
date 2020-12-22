package vn.vnpay.notspringdemo.dao;
import java.util.Map;

public interface QrTerminalDAO {

    Map<String, Object> searchQrTerminalByCondition(Map<String, Object> inParams);

}
