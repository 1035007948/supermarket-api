package com.supermarket.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stored_value_card")
public class StoredValueCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_no", unique = true, nullable = false, length = 32)
    private String cardNo;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "total_recharge", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRecharge = BigDecimal.ZERO;

    @Column(name = "total_consumption", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;
}