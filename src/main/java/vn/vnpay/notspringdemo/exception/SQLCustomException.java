package vn.vnpay.notspringdemo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SQLCustomException extends RuntimeException{

    private String code;
    private String message;

}
