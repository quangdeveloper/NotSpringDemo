package vn.vnpay.notspringdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;

@Data
@Builder
@AllArgsConstructor
public class ResponseDTO {

    private String code;
    private String message;
    private Object value;

    public ResponseDTO(){
        this.code = "200";
        this.message = "Thực hiện thành công";
        this.value = Collections.emptyList();
    }
}
