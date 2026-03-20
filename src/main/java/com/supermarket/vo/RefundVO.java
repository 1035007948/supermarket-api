package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundVO {
    private Long id;
    private String refundNo;
    private Long orderId;
    private String orderNo;
    private Long memberId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String statusName;
    private Long reviewerId;
    private String reviewerName;
    private String reviewRemark;
    private LocalDateTime reviewTime;
    private LocalDateTime createTime;
}
