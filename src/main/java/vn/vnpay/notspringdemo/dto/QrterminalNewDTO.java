package vn.vnpay.notspringdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrterminalNewDTO {


    private Long id;

    private String terminalId;

    private String terminalName;

    private String terminalAddress;

    private String merchantId;
}
