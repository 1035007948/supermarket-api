package com.supermarket.enums;

public enum ProductCategory {
    FOOD("食品"),
    CLOTHING("服饰"),
    HOME("家居"),
    DIGITAL("数码"),
    BEAUTY("美妆");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}