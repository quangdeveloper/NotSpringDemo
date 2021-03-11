package vn.vnpay.notspringdemo.dto;

import lombok.Data;

@Data
public class UserLogout {

    private String username;
    private String token;
    private Integer type;
}
