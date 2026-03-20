package com.supermarket.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequest {
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "1.00", message = "充值金额必须大于0")
    private BigDecimal amount;
}
