package com.supermarket.member.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ConsumeDTO {

    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "消费金额不能为空")
    @DecimalMin(value = "0.01", message = "消费金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    private Integer usePoints;

    private String description;
}
