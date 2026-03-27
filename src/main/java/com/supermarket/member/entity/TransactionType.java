package com.supermarket.member.entity;

public enum TransactionType {
    CONSUMPTION("消费"),
    RECHARGE("充值"),
    POINTS_EARN("积分获取"),
    POINTS_USE("积分抵扣"),
    GIFT("赠品");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
