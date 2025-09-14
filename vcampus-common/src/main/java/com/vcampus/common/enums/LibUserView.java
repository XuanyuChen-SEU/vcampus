package com.vcampus.common.enums;

import java.io.Serializable;

public enum LibUserView implements Serializable {
    ALL_BOOKS("书籍列表"),
    MY_BORROWS("我的借阅");
    private final String description;
    LibUserView(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

}
