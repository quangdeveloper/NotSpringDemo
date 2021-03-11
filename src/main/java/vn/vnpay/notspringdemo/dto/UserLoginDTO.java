package vn.vnpay.notspringdemo.dto;

import lombok.Data;

@Data
public class UserLoginDTO {

    private String username;
    private String password;
    private Integer type;
}
