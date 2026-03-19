package com.supermarket.enums;

public enum OrderStatus {
    PENDING_PAYMENT("待支付"),
    PENDING_PREPARATION("待备货"),
    PENDING_PICKUP("待取货"),
    PENDING_SHIPMENT("待发货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}