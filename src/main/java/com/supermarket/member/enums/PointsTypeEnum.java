package com.supermarket.member.enums;

import lombok.Getter;

@Getter
public enum PointsTypeEnum {
    EARN(1, "获得积分"),
    DEDUCT(2, "抵扣积分"),
    EXPIRE(3, "积分过期");

    private final Integer code;
    private final String name;

    PointsTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static PointsTypeEnum getByCode(Integer code) {
        for (PointsTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
