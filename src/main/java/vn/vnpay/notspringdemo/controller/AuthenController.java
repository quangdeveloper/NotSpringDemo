package vn.vnpay.notspringdemo.controller;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vnpay.notspringdemo.dto.*;
import vn.vnpay.notspringdemo.util.Constant;

@RestController
@RequestMapping("")
public class AuthenController {

    private final Logger logger = LoggerFactory.getLogger(AuthenController.class);

    @PostMapping("/login")
    public ResponseEntity<Object> login() {

        logger.info("Token [{}]: [ PostMapping  path = /login  with request body:  {}]",
                ThreadContext.get("token"),
                null);
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .fullname("Nguyen Quang")
                .username("quangnv")
                .build();

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(userInfoDTO)
                        .build());
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDTO registerDTO) {

        logger.info("Token [{}]: [ PostMapping  path = /register  with request body:  {}]",
                ThreadContext.get("token"),
                registerDTO);

        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .fullname(registerDTO.getFullname())
                .username(registerDTO.getUsername())
                .build();

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(userInfoDTO)
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestBody UserLogout userLogout) {

        logger.info("Token [{}]: [ PostMapping  path = /logout  with request body:  {}]",
                ThreadContext.get("token"),
                userLogout);

        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .code(Constant.RESPONSE.CODE.OK)
                        .message(Constant.RESPONSE.MESSAGE.OK)
                        .value(null)
                        .build());
    }

}
