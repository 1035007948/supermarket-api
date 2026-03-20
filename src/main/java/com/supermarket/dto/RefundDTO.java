package com.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RefundDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "退款金额不能为空")
    private BigDecimal amount;

    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
