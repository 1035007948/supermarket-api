package com.supermarket.enums;

public enum MemberLevel {
    REGULAR("普通", 0, 1.0, 0),
    SILVER("银卡", 1000, 0.95, 5),
    GOLD("金卡", 5000, 0.9, 10);

    private final String description;
    private final Integer minPoints;
    private final Double discountRate;
    private final Integer bonusPointsRate;

    MemberLevel(String description, Integer minPoints, Double discountRate, Integer bonusPointsRate) {
        this.description = description;
        this.minPoints = minPoints;
        this.discountRate = discountRate;
        this.bonusPointsRate = bonusPointsRate;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public Double getDiscountRate() {
        return discountRate;
    }

    public Integer getBonusPointsRate() {
        return bonusPointsRate;
    }
}