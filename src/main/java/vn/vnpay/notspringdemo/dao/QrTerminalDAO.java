package vn.vnpay.notspringdemo.dao;
import vn.vnpay.notspringdemo.mapper.RowMapper;
import vn.vnpay.notspringdemo.model.ParameterORA;
import java.util.List;
import java.util.Map;

public interface QrTerminalDAO <T> {

     Map<String, Object> searchQrTerminal(List<ParameterORA> parameterORAs, RowMapper<T> rowMapper);

}
