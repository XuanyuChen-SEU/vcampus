package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 用户数据传输对象（DTO）
 * 用于客户端与服务端之间的用户信息传递
 * 编写人：谌宣羽
 *
 * 注意事项
 *   用户ID（userId）必须为7位字符串，设置时若不符合格式会抛出 IllegalArgumentException
 *   密码（password）字段仅存储加密后的密文，需通过com.vcampus.common.util.EncryptUtil处理后传入
 *   角色（role）使用com.vcampus.common.enums.Role枚举
 *   反序列化时依赖默认构造方法，请勿删除或修改访问权限
 *
 *   Getter方法：可安全获取用户ID、加密密码、角色信息用于展示或业务判断</li>
 *   构造方法：支持通过用户ID、加密密码、角色直接创建实例</li>
 */

public class User implements Serializable {//数据传输对象

    private static final long serialVersionUID = 1L;

    // 七位用户ID（使用String避免首位0丢失）
    private String userId;

    // 加密后的密码（传输为明文和存储为密文）
    private String password;

    // --- 为商店功能新增的字段 ---
    private Double balance;     // 一卡通余额

    // 默认构造方法（反序列化必需）
    public User() {}

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User(String userId, String cardNumber, Double balance) {
        this.setUserId(userId); // 使用setter
        this.balance = balance;
    }

    // Getter & Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        // 简单校验
        if (userId != null && userId.matches("\\d{7}")) {
            this.userId = userId;
        } else {
            throw new IllegalArgumentException("用户ID必须为7位纯数字字符串");
        }
    }

    public String getPassword() {
        return password;
    }

    // 注意，这里密码是明文
    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    /**
     * 重写toString方法，用于打印User对象的信息
     * @return 包含userId和password的字符串
     */
    @Override
    public String toString() {
        return "User{userId='" + userId + "', password='" + password + "'}";
    }
}