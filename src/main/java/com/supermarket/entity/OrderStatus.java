package com.supermarket.entity;

public enum OrderStatus {
    PENDING_PAYMENT,
    PREPARING,
    READY_FOR_PICKUP,
    READY_FOR_DELIVERY,
    COMPLETED,
    CANCELLED,
    REFUNDING,
    REFUNDED
}
