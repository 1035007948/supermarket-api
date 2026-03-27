package com.supermarket.member.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ConsumptionRequest {

    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "消费金额不能为空")
    @Positive(message = "消费金额必须为正数")
    private BigDecimal amount;

    private Integer pointsToUse;

    private String description;
}
