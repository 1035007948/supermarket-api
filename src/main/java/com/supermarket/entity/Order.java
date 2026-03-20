package com.supermarket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String orderNo;
    
    @Column(nullable = false)
    private Long memberId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;
    
    private Integer pointsUsed;
    
    private Integer pointsEarned;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
    
    private String deliveryType;
    
    private LocalDateTime paidAt;
    
    private LocalDateTime preparedAt;
    
    private LocalDateTime deliveredAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime cancelledAt;
    
    private String cancelReason;
    
    private String remark;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
