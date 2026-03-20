package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
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
    private String payStatus;
    private String status;
    private String deliveryType;
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
    private LocalDateTime notifyTime;
    private Integer notifyStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
