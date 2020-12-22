package vn.vnpay.notspringdemo.service;


import vn.vnpay.notspringdemo.dto.PageDTO;

public interface QrTerminalService {

    PageDTO searchQrTerminal(Long pageNo, Long pageSize);

    Object searchQrTerminalOnRedis(String key);
}
