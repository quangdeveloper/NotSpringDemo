package vn.vnpay.notspringdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vnpay.notspringdemo.dto.ResponseDTO;
import vn.vnpay.notspringdemo.service.QrTerminalService;
import vn.vnpay.notspringdemo.util.Constant;

@RestController
@RequestMapping("/qr-terminal")
public class QrTerminalController {

    Logger logger = LoggerFactory.getLogger(QrTerminalController.class);

    @Autowired
    private QrTerminalService qrTerminalService;

    @GetMapping()
    public ResponseEntity<Object> searchQrTerminal(@RequestParam Long pageNo, @RequestParam Long pageSize) {

        logger.info("Thead Id {}: [ GetMapping  path = /qr-terminal  with RequestParam: [pageNo: {}, pageSize: {}]]",
                Thread.currentThread().getId(),
                pageNo,
                pageSize);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(qrTerminalService.searchQrTerminal(pageNo, pageSize))
                        .build());
    }

    @GetMapping("/{key}")
    public ResponseEntity<Object> searchQrTerminalOnRedis(@PathVariable("key") String key) {

        logger.info("Thread Id {}: [ GetMapping  path = /qr-terminal/{key}  with PathVariable: [key: {}] ]",
                Thread.currentThread().getId(),
                key);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(qrTerminalService.searchQrTerminalOnRedis(key))
                        .build());
    }

}
