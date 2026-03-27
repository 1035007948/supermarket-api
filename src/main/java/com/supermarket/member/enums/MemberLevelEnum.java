package com.supermarket.member.enums;

import lombok.Getter;

@Getter
public enum MemberLevelEnum {
    NORMAL(0, "普通会员", 0, 999, 0.95),
    SILVER(1, "银卡会员", 1000, 4999, 0.90),
    GOLD(2, "金卡会员", 5000, Integer.MAX_VALUE, 0.85);

    private final Integer code;
    private final String name;
    private final Integer minPoints;
    private final Integer maxPoints;
    private final Double discountRate;

    MemberLevelEnum(Integer code, String name, Integer minPoints, Integer maxPoints, Double discountRate) {
        this.code = code;
        this.name = name;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.discountRate = discountRate;
    }

    public static MemberLevelEnum getByCode(Integer code) {
        for (MemberLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return NORMAL;
    }

    public static MemberLevelEnum getByPoints(Integer points) {
        for (MemberLevelEnum level : values()) {
            if (points >= level.getMinPoints() && points <= level.getMaxPoints()) {
                return level;
            }
        }
        return NORMAL;
    }
}
