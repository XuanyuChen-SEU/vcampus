package com.vcampus.common.enums;

import java.io.Serializable;

/**
 * 商品状态枚举
 */
public enum ProductStatus implements Serializable {
    ON_SALE("在售"),
    OFF_SHELF("已下架");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}