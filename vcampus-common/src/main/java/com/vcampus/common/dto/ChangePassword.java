package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 修改密码请求数据传输对象
 * 用于客户端向服务器发送修改密码请求
 * 编写人：Assistant
 */
public class ChangePassword implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 原密码
     */
    private String oldPassword;
    
    /**
     * 新密码
     */
    private String newPassword;
    
    /**
     * 默认构造函数
     */
    public ChangePassword() {
    }
    
    /**
     * 带参数的构造函数
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    public ChangePassword(String userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
    
    /**
     * 获取用户ID
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * 获取原密码
     * @return 原密码
     */
    public String getOldPassword() {
        return oldPassword;
    }
    
    /**
     * 设置原密码
     * @param oldPassword 原密码
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    /**
     * 获取新密码
     * @return 新密码
     */
    public String getNewPassword() {
        return newPassword;
    }
    
    /**
     * 设置新密码
     * @param newPassword 新密码
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    /**
     * 验证请求数据是否完整
     * @return true表示数据完整，false表示数据不完整
     */
    public boolean isValid() {
        return userId != null && !userId.trim().isEmpty() &&
               oldPassword != null && !oldPassword.trim().isEmpty() &&
               newPassword != null && !newPassword.trim().isEmpty();
    }
    
    /**
     * 清空敏感信息（密码）
     */
    public void clearSensitiveData() {
        this.oldPassword = null;
        this.newPassword = null;
    }
    
    @Override
    public String toString() {
        return "ChangePassword{" +
                "userId='" + userId + '\'' +
                ", oldPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                '}';
    }
}
