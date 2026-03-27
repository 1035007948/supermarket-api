package com.supermarket.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 会员实体类
 */
@Data
@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会员卡号
     */
    @Column(unique = true, nullable = false, length = 20)
    private String cardNumber;

    /**
     * 会员姓名
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 手机号码
     */
    @Column(unique = true, nullable = false, length = 11)
    private String phone;

    /**
     * 密码
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * 会员等级
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberLevel level = MemberLevel.REGULAR;

    /**
     * 积分余额
     */
    @Column(nullable = false)
    private Integer points = 0;

    /**
     * 储值余额
     */
    @Column(nullable = false)
    private Double storedBalance = 0.0;

    /**
     * 累计充值金额
     */
    @Column(nullable = false)
    private Double totalRecharge = 0.0;

    /**
     * 累计消费金额
     */
    @Column(nullable = false)
    private Double totalConsumption = 0.0;

    /**
     * 会员状态：0-禁用，1-启用
     */
    @Column(nullable = false)
    private Integer status = 1;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    /**
     * 更新会员等级（根据累计充值金额）
     */
    public void updateLevelByTotalRecharge() {
        this.level = MemberLevel.calculateLevel(this.totalRecharge);
    }

    /**
     * 计算折扣后金额
     */
    public double calculateDiscountedAmount(double originalAmount) {
        return originalAmount * (1 - level.getDiscountRate());
    }

    /**
     * 计算应获得积分
     */
    public int calculatePoints(double amount) {
        return (int) (amount * level.getPointMultiplier());
    }

    /**
     * 添加积分
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * 使用积分
     */
    public boolean usePoints(int points) {
        if (this.points >= points) {
            this.points -= points;
            return true;
        }
        return false;
    }

    /**
     * 储值充值
     */
    public void recharge(double amount) {
        this.storedBalance += amount;
        this.totalRecharge += amount;
        updateLevelByTotalRecharge();
    }

    /**
     * 使用储值消费
     */
    public boolean useStoredBalance(double amount) {
        if (this.storedBalance >= amount) {
            this.storedBalance -= amount;
            this.totalConsumption += amount;
            return true;
        }
        return false;
    }
}
