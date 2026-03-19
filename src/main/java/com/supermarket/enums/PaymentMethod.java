package com.supermarket.enums;

public enum PaymentMethod {
    ONLINE_PAYMENT("线上支付"),
    IN_STORE_PAYMENT("到店付款");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}