package com.vcampus.common.enums;

import java.io.Serializable;

/**
 * 商店申请状态枚举 (如退货申请)
 */
// 变化 1: 枚举名从 ApplicationStatus 改为 ShopApplicationStatus
public enum ShopApplicationStatus implements Serializable {
    PENDING("待审核"),
    APPROVED("已批准"),
    REJECTED("已拒绝");

    private final String description;

    // 变化 2: 构造函数名也跟着改变
    ShopApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}