package com.vcampus.server.service;

import com.vcampus.common.dto.User;
import com.vcampus.common.enums.Role;

/**
 * 登录服务类
 * 负责登录业务逻辑，包括密码验证和用户认证
 * 编写人：cursor
 */
public class LoginService {
    
    // 固定的测试账号密码
    private static final String TEST_USER_ID = "1000000";
    private static final String TEST_PASSWORD = "1000000";
    
    /**
     * 验证用户登录
     * @param loginUser 登录用户信息
     * @return 登录结果，包含成功/失败信息和用户数据
     */
    public LoginResult validateLogin(User loginUser) {
        try {
            // 1. 验证输入参数
            if (loginUser == null || loginUser.getUserId() == null || loginUser.getPassword() == null) {
                return new LoginResult(false, "登录信息不完整", null);
            }
            
            String userId = loginUser.getUserId();
            String plainPassword = loginUser.getPassword();
            
            // 2. 验证用户ID格式
            if (!userId.matches("\\d{7}")) {
                return new LoginResult(false, "用户ID格式错误，必须为7位数字", null);
            }
            
            // 3. 验证固定的测试账号密码
            if (!TEST_USER_ID.equals(userId)) {
                return new LoginResult(false, "用户不存在", null);
            }
            
            // 4. 验证密码
            if (!TEST_PASSWORD.equals(plainPassword)) {
                return new LoginResult(false, "密码错误", null);
            }
            
            // 6. 获取用户角色
            Role role = Role.fromUserId(userId);
            
            // 7. 登录成功，返回用户信息（不包含密码）
            User resultUser = new User(userId, ""); // 密码置空，不返回给客户端
            
            return new LoginResult(true, "登录成功，欢迎 " + role.getDesc() + " " + userId, resultUser);
            
        } catch (IllegalArgumentException e) {
            return new LoginResult(false, "用户ID格式错误: " + e.getMessage(), null);
        } catch (Exception e) {
            System.err.println("登录验证过程中发生异常: " + e.getMessage());
            return new LoginResult(false, "服务器内部错误", null);
        }
    }
    
    /**
     * 登录结果内部类
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
    }
}
