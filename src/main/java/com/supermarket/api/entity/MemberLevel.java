package com.supermarket.api.entity;

/**
 * 会员等级枚举
 */
public enum MemberLevel {
    /**
     * 普通会员
     */
    REGULAR("普通会员", 0, 0.0, 1.0),
    /**
     * 银卡会员
     */
    SILVER("银卡会员", 1000, 0.05, 1.2),
    /**
     * 金卡会员
     */
    GOLD("金卡会员", 5000, 0.1, 1.5);

    private final String description;
    private final int minBalance;
    private final double discountRate;
    private final double pointMultiplier;

    MemberLevel(String description, int minBalance, double discountRate, double pointMultiplier) {
        this.description = description;
        this.minBalance = minBalance;
        this.discountRate = discountRate;
        this.pointMultiplier = pointMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public int getMinBalance() {
        return minBalance;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public double getPointMultiplier() {
        return pointMultiplier;
    }

    /**
     * 根据储值余额计算会员等级
     */
    public static MemberLevel calculateLevel(double balance) {
        if (balance >= GOLD.minBalance) {
            return GOLD;
        } else if (balance >= SILVER.minBalance) {
            return SILVER;
        } else {
            return REGULAR;
        }
    }
}
