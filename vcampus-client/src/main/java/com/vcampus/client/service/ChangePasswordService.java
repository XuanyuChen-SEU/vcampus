package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.ChangePassword;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 修改密码服务类
 * 处理密码修改的复杂验证逻辑和网络通信
 * 编写人：Assistant
 */
public class ChangePasswordService {
    
    private final SocketClient socketClient;
    
    // 密码验证规则常量
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$";
    
    public ChangePasswordService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 验证新旧密码是否相同
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 验证结果
     */
    public PasswordValidationResult validatePasswordDifference(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            return new PasswordValidationResult(false, "密码不能为空");
        }
        
        if (oldPassword.trim().equals(newPassword.trim())) {
            return new PasswordValidationResult(false, "新密码不能与原密码相同");
        }
        
        return new PasswordValidationResult(true, "密码验证通过");
    }
    
    /**
     * 验证密码确认
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @return 验证结果
     */
    public PasswordValidationResult validatePasswordConfirmation(String newPassword, String confirmPassword) {
        if (newPassword == null || confirmPassword == null) {
            return new PasswordValidationResult(false, "密码不能为空");
        }
        
        if (!newPassword.trim().equals(confirmPassword.trim())) {
            return new PasswordValidationResult(false, "新密码与确认密码不一致");
        }
        
        return new PasswordValidationResult(true, "密码确认通过");
    }
    
    /**
     * 检查用户登录状态
     * @return 验证结果
     */
    public PasswordValidationResult validateUserSession() {
        UserSession userSession = MainApp.getGlobalUserSession();
        if (!userSession.isLoggedIn()) {
            return new PasswordValidationResult(false, "用户未登录，请先登录");
        }
        
        String userId = userSession.getCurrentUserId();
        if (userId == null || userId.trim().isEmpty()) {
            return new PasswordValidationResult(false, "用户ID无效");
        }
        
        return new PasswordValidationResult(true, "用户会话有效");
    }
    
    /**
     * 执行密码修改
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    public ChangePasswordResult changePassword(String oldPassword, String newPassword) {
        try {
            // 1. 验证用户会话
            PasswordValidationResult sessionResult = validateUserSession();
            if (!sessionResult.isValid()) {
                return new ChangePasswordResult(false, sessionResult.getMessage());
            }
            
            // 2. 验证密码差异
            PasswordValidationResult differenceResult = validatePasswordDifference(oldPassword, newPassword);
            if (!differenceResult.isValid()) {
                return new ChangePasswordResult(false, differenceResult.getMessage());
            }
            
            // 3. 获取用户ID
            UserSession userSession = MainApp.getGlobalUserSession();
            String userId = userSession.getCurrentUserId();
            
            // 4. 创建修改密码请求对象
            ChangePassword changePasswordRequest = new ChangePassword(userId, oldPassword.trim(), newPassword.trim());
            
            // 5. 验证请求数据
            if (!changePasswordRequest.isValid()) {
                return new ChangePasswordResult(false, "请求数据不完整");
            }
            
            // 6. 发送网络请求
            Message requestMessage = new Message(ActionType.CHANGE_PASSWORD, changePasswordRequest);
            Message responseMessage = socketClient.sendMessage(requestMessage);
            
            // 7. 处理响应
            if (responseMessage != null && responseMessage.isSuccess()) {
                return new ChangePasswordResult(true, "密码修改申请成功！");
            } else {
                String errorMsg = responseMessage != null ? responseMessage.getMessage() : "网络连接失败";
                return new ChangePasswordResult(false, "密码修改失败：" + errorMsg);
            }
            
        } catch (Exception e) {
            return new ChangePasswordResult(false, "修改密码时发生错误：" + e.getMessage());
        } finally {
            // 清空敏感数据
            if (oldPassword != null) {
                // 注意：这里无法真正清空字符串，因为Java字符串是不可变的
                // 实际应用中可以考虑使用char[]来存储密码
            }
        }
    }
    
    /**
     * 密码验证结果类
     */
    public static class PasswordValidationResult {
        private final boolean valid;
        private final String message;
        
        public PasswordValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * 密码修改结果类
     */
    public static class ChangePasswordResult {
        private final boolean success;
        private final String message;
        
        public ChangePasswordResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
