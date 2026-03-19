package com.supermarket.enums;

public enum ProductStatus {
    ON_SALE("在售"),
    OFF_SHELF("下架"),
    OUT_OF_STOCK("缺货"),
    PRE_SALE("预售");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}