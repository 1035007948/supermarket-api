package com.supermarket.member.enums;

import lombok.Getter;

@Getter
public enum MemberStatusEnum {
    ACTIVE(1, "正常"),
    FROZEN(2, "冻结"),
    EXPIRED(3, "过期");

    private final Integer code;
    private final String name;

    MemberStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MemberStatusEnum getByCode(Integer code) {
        for (MemberStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return ACTIVE;
    }
}
