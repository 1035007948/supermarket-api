package com.supermarket.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 交易单号
     */
    @Column(unique = true, nullable = false, length = 32)
    private String transactionNo;

    /**
     * 会员ID
     */
    @Column(nullable = false)
    private Long memberId;

    /**
     * 交易类型：RECHARGE-充值, CONSUMPTION-消费, POINTS_EXCHANGE-积分兑换, POINTS_DEDUCTION-积分抵扣
     */
    @Column(nullable = false, length = 30)
    private String type;

    /**
     * 交易金额
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * 积分变动（正数为增加，负数为减少）
     */
    @Column(nullable = false)
    private Integer pointsChange = 0;

    /**
     * 储值余额变动（正数为增加，负数为减少）
     */
    @Column(nullable = false)
    private Double balanceChange = 0.0;

    /**
     * 交易后积分余额
     */
    @Column(nullable = false)
    private Integer pointsAfter;

    /**
     * 交易后储值余额
     */
    @Column(nullable = false)
    private Double balanceAfter;

    /**
     * 备注
     */
    @Column(length = 200)
    private String remark;

    /**
     * 交易时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 交易类型枚举
     */
    public enum TransactionType {
        RECHARGE("充值"),
        CONSUMPTION("消费"),
        POINTS_EXCHANGE("积分兑换"),
        POINTS_DEDUCTION("积分抵扣"),
        GIFT("赠品");

        private final String description;

        TransactionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
