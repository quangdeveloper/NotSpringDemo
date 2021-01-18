package vn.vnpay.notspringdemo.controller;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vnpay.notspringdemo.dto.QrterminalNewDTO;
import vn.vnpay.notspringdemo.dto.ResponseDTO;
import vn.vnpay.notspringdemo.service.QrTerminalService;
import vn.vnpay.notspringdemo.util.Constant;

@RestController
@RequestMapping("/qrterminal")
public class QrTerminalController {

    Logger logger = LoggerFactory.getLogger(QrTerminalController.class);

    @Autowired
    private QrTerminalService qrTerminalService;

    @GetMapping()
    public ResponseEntity<Object> searchQrTerminal(@RequestParam Long pageNo,
                                                   @RequestParam Long pageSize,
                                                   @RequestParam String keyword) {

        logger.info("Token [{}]: [ GetMapping  path = /qr-terminal  with RequestParam: [pageNo: {}, pageSize: {}]]",
                ThreadContext.get("token"),
                pageNo,
                pageSize);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(qrTerminalService.searchQrTerminal(pageNo, pageSize, keyword))
                        .build());
    }

    @GetMapping("/{key}")
    public ResponseEntity<Object> searchQrTerminalOnRedis(@PathVariable("key") String key) {

        logger.info("Token [{}]: [ GetMapping  path = /qr-terminal/{key}  with PathVariable: [key: {}] ]",
                ThreadContext.get("token"),
                key);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(qrTerminalService.searchQrTerminalOnRedis(key))
                        .build());
    }

    @PostMapping("")
    public ResponseEntity<Object> createQrTerminal(@RequestBody QrterminalNewDTO qrterminalNewDTO) {

        logger.info("Token [{}]: [ PostMapping  path = /qr-terminal  with request body:  {}]",
                ThreadContext.get("token"),
                qrterminalNewDTO);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value("Success")
                        .build());
    }

    @PutMapping("")
    public ResponseEntity<Object> updateQrTerminal(@RequestBody QrterminalNewDTO qrterminalNewDTO) {

        logger.info("Token [{}]: [ PutMapping  path = /qr-terminal  with request body:  {}]",
                ThreadContext.get("token"),
                qrterminalNewDTO);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value("Success")
                        .build());
    }


}
