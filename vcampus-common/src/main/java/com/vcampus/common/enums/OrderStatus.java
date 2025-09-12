package com.vcampus.common.enums;

import java.io.Serializable;

/**
 * 订单状态枚举
 */
public enum OrderStatus implements Serializable {
    UNPAID("未支付"),
    PAID("已支付"),
    CANCELLED("已取消"),
    RETURN_REQUESTED("退货申请中"),
    RETURNED("已退货");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}