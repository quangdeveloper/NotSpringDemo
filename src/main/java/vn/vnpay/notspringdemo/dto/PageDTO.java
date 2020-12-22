package vn.vnpay.notspringdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;

@Builder
@Data
public class PageDTO {

    @JsonProperty("Result")
    @Builder.Default
    private Object content = Collections.emptyList();

    @JsonProperty("Total")
    private long total = 0;
}
