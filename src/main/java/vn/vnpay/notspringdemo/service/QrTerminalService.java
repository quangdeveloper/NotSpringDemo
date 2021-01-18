package vn.vnpay.notspringdemo.service;


import vn.vnpay.notspringdemo.dto.PageDTO;

public interface QrTerminalService {

    <T> PageDTO searchQrTerminal(Long pageNo, Long pageSize, String keyword);

    Object searchQrTerminalOnRedis(String key);
}
