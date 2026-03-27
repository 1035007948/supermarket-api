package com.supermarket.api.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 消费DTO
 */
@Data
public class ConsumptionDTO {

    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @NotNull(message = "消费金额不能为空")
    @DecimalMin(value = "0.01", message = "消费金额必须大于0")
    private BigDecimal amount;

    /**
     * 是否使用储值余额支付
     */
    private Boolean useStoredBalance = false;

    /**
     * 使用积分抵扣的积分数
     */
    private Integer usePoints = 0;

    private String remark;
}
