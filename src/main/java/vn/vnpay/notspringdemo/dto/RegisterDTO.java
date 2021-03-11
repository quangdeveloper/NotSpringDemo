package vn.vnpay.notspringdemo.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    private String username;
    private String email;
    private String password;
    private String fullname;
    private Integer type;
}
