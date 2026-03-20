package com.supermarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RechargeDTO {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    private BigDecimal amount;
}
