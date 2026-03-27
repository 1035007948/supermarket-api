package com.supermarket.member.entity;

public enum MemberLevel {
    NORMAL("普通会员", 1.0, 1),
    SILVER("银卡会员", 0.95, 2),
    GOLD("金卡会员", 0.9, 3);

    private final String name;
    private final double discount;
    private final int pointRate;

    MemberLevel(String name, double discount, int pointRate) {
        this.name = name;
        this.discount = discount;
        this.pointRate = pointRate;
    }

    public String getName() {
        return name;
    }

    public double getDiscount() {
        return discount;
    }

    public int getPointRate() {
        return pointRate;
    }
}
