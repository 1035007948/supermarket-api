package com.supermarket.entity;

import com.supermarket.enums.OrderStatus;
import com.supermarket.enums.PaymentMethod;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;

    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal payAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "ship_time")
    private LocalDateTime shipTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(length = 500)
    private String remark;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;
}