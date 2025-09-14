package com.vcampus.common.dto;

import java.io.Serializable; // 【修复】导入 Serializable

public class UserBorrowStatus implements Serializable { // 【修复】实现 Serializable 接口

    private static final long serialVersionUID = 1L; // 【推荐】添加 serialVersionUID

    private String userId;
    private String name;
    private String identity; // 身份
    private String borrowedBook1;
    private String borrowedBook2;
    private String borrowedBook3;

    public UserBorrowStatus(String userId, String name, String identity, String borrowedBook1, String borrowedBook2, String borrowedBook3) {
        // ... 构造函数是好的，无需修改
        this.userId = userId;
        this.name = name;
        this.identity = identity;
        this.borrowedBook1 = borrowedBook1;
        this.borrowedBook2 = borrowedBook2;
        this.borrowedBook3 = borrowedBook3;
    }

    // ... 所有 Getters 和 Setters 都是好的，无需修改
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIdentity() { return identity; }
    public void setIdentity(String identity) { this.identity = identity; }
    public String getBorrowedBook1() { return borrowedBook1; }
    public void setBorrowedBook1(String borrowedBook1) { this.borrowedBook1 = borrowedBook1; }
    public String getBorrowedBook2() { return borrowedBook2; }
    public void setBorrowedBook2(String borrowedBook2) { this.borrowedBook2 = borrowedBook2; }
    public String getBorrowedBook3() { return borrowedBook3; }
    public void setBorrowedBook3(String borrowedBook3) { this.borrowedBook3 = borrowedBook3; }
}