package vn.vnpay.notspringdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ActionDTO {

    private Map<String, Object> value;
}
