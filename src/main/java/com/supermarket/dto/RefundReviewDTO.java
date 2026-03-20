package com.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundReviewDTO {
    @NotNull(message = "退款记录ID不能为空")
    private Long refundId;

    @NotBlank(message = "审核结果不能为空")
    private String status;

    private String reviewRemark;
}
