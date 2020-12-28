package vn.vnpay.notspringdemo.dao;
import vn.vnpay.notspringdemo.model.ParameterORA;
import java.util.List;
import java.util.Map;

public interface QrTerminalDAO {

    Map<String, Object> searchQrTerminalByCondition(List<ParameterORA> parameterORAs);

}
