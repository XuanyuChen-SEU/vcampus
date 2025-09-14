package com.vcampus.common.enums;

import java.io.Serializable;
public enum LibAdminView implements Serializable {
    ALL_BOOKS("管理员端书籍列表"),
    BORROW_HISTORY("管理员端借阅历史"),
    ALL_USERS_STATUS("管理员端所有用户状态");

    private final String description;

    LibAdminView(String description) {
        this.description = description;
    }
    public String getDescription() {return this.description;}
}
