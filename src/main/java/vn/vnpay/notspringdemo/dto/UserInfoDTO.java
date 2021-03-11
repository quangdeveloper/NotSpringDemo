package vn.vnpay.notspringdemo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDTO {

    private String username;
    @Builder.Default
    private String token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    @Builder.Default
    private String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private String fullname ;
}
