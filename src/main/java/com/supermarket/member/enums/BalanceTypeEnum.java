package com.supermarket.member.enums;

import lombok.Getter;

@Getter
public enum BalanceTypeEnum {
    RECHARGE(1, "充值"),
    CONSUME(2, "消费"),
    REFUND(3, "退款");

    private final Integer code;
    private final String name;

    BalanceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BalanceTypeEnum getByCode(Integer code) {
        for (BalanceTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
