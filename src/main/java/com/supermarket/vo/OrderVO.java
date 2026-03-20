package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private Integer pointsDeduct;
    private BigDecimal payableAmount;
    private BigDecimal payAmount;
    private String payType;
    private String payTypeName;
    private String payStatus;
    private String payStatusName;
    private String status;
    private String statusName;
    private String deliveryType;
    private String deliveryTypeName;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private String cancelReason;
    private LocalDateTime paidTime;
    private LocalDateTime preparedTime;
    private LocalDateTime shippedTime;
    private LocalDateTime completedTime;
    private LocalDateTime cancelledTime;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}
