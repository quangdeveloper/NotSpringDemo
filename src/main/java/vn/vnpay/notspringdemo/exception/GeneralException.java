package vn.vnpay.notspringdemo.exception;

import lombok.Data;

@Data
public class GeneralException extends RuntimeException {

    private String code;

    private String message;

    private Object value;

    public GeneralException(){

    }
    public GeneralException(String code, String message){
        this.code= code;
        this.message = message;

    }
    public GeneralException(String code, String message, Object value){
        this.code= code;
        this.message = message;
        this.value = value;

    }
}
