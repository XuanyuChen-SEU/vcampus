package com.vcampus.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 密码重置申请 DTO
 * 用于记录用户密码重置申请信息
 * 编写人：谌宣羽
 * 
 * 字段说明：
 *   - userId: 申请密码重置的用户ID（7位数字，关联 User）
 *   - newPasswordId: 新密码
 *   - submitTime: 用户提交申请的时间
 */
public class PasswordResetApplication implements Serializable {
    private static final long serialVersionUID = 1L;    
    
    /**
     * 申请密码重置的用户ID
     */
    private String userId;
    
    /**
     * 新密码ID
     */
    private String newPassword;
    
    /**
     * 用户提交申请的时间
     */
    private LocalDateTime submitTime;
    
    /**
     * 默认构造函数
     */
    public PasswordResetApplication() {
    }
    
    /**
     * 带参数的构造函数
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    public PasswordResetApplication(String userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
        this.submitTime = LocalDateTime.now();
    }
    
    /**
     * 完整构造函数
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param submitTime 提交时间
     */
    public PasswordResetApplication(String userId, String newPassword, LocalDateTime submitTime) {
        this.userId = userId;
        this.newPassword = newPassword;
        this.submitTime = submitTime;
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
     * 获取提交时间
     * @return 提交时间
     */
    public LocalDateTime getSubmitTime() {
        return submitTime;
    }
    
    /**
     * 设置提交时间
     * @param submitTime 提交时间
     */
    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }
    
    @Override
    public String toString() {
        return "PasswordResetApplication{" +
                "userId='" + userId + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", submitTime=" + submitTime +
                '}';
    }
}