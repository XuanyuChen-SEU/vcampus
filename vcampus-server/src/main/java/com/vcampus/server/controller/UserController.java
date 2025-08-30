package com.vcampus.server.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.UserService;

/**
 * 用户控制器类
 * 处理客户端发送的用户相关请求
 * 编写人：谌宣羽
 */
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    public UserController() {
        this.userService = new UserService();
    }
    
    /**
     * 处理登录请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    public Message handleLogin(Message message) {
        logger.info("处理登录请求");
        
        try {
            // 验证消息格式
            if (message == null || message.getData() == null) {
                logger.warn("登录请求消息格式错误");
                return Message.failure(ActionType.LOGIN, "请求格式错误");
            }
            
            // 解析登录数据
            Object data = message.getData();
            if (!(data instanceof Map)) {
                logger.warn("登录数据格式错误");
                return Message.failure(ActionType.LOGIN, "数据格式错误");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, String> loginData = (Map<String, String>) data;
            
            String userId = loginData.get("userId");
            String password = loginData.get("password");
            
            // 参数验证
            if (userId == null || password == null) {
                logger.warn("登录参数缺失");
                return Message.failure(ActionType.LOGIN, "用户名或密码不能为空");
            }
            
            // 调用业务逻辑进行登录验证
            User user = userService.login(userId, password);
            
            if (user != null) {
                // 登录成功
                logger.info("用户登录成功: {}, 角色: {}", userId, user.getRole().getDesc());
                
                // 创建返回数据（不包含密码）
                Map<String, Object> userData = Map.of(
                    "userId", user.getUserId(),
                    "phone", user.getPhone(),
                    "role", user.getRole().getDesc()
                );
                
                return Message.success(ActionType.LOGIN, userData, "登录成功");
            } else {
                // 登录失败
                logger.warn("用户登录失败: {}", userId);
                return Message.failure(ActionType.LOGIN, "用户名或密码错误");
            }
            
        } catch (Exception e) {
            logger.error("处理登录请求异常: {}", e.getMessage(), e);
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
    
    /**
     * 处理登出请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    public Message handleLogout(Message message) {
        logger.info("处理登出请求");
        
        try {
            // 登出逻辑相对简单，主要是客户端清理本地状态
            logger.info("用户登出成功");
            return Message.success(ActionType.LOGOUT, "登出成功");
            
        } catch (Exception e) {
            logger.error("处理登出请求异常: {}", e.getMessage(), e);
            return Message.failure(ActionType.LOGOUT, "服务器内部错误");
        }
    }
    
    /**
     * 处理获取用户信息请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    public Message handleGetUserInfo(Message message) {
        logger.info("处理获取用户信息请求");
        
        try {
            // 验证消息格式
            if (message == null || message.getData() == null) {
                return Message.failure(ActionType.LOGIN, "请求格式错误");
            }
            
            String userId = (String) message.getData();
            
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                return Message.failure(ActionType.LOGIN, "用户ID不能为空");
            }
            
            // 获取用户信息
            User user = userService.getUserById(userId);
            
            if (user != null) {
                // 创建返回数据（不包含密码）
                Map<String, Object> userData = Map.of(
                    "userId", user.getUserId(),
                    "phone", user.getPhone(),
                    "role", user.getRole().getDesc()
                );
                
                return Message.success(ActionType.LOGIN, userData, "获取用户信息成功");
            } else {
                return Message.failure(ActionType.LOGIN, "用户不存在");
            }
            
        } catch (Exception e) {
            logger.error("处理获取用户信息请求异常: {}", e.getMessage(), e);
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
    
    /**
     * 处理更新密码请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    public Message handleUpdatePassword(Message message) {
        logger.info("处理更新密码请求");
        
        try {
            // 验证消息格式
            if (message == null || message.getData() == null) {
                return Message.failure(ActionType.LOGIN, "请求格式错误");
            }
            
            // 解析更新密码数据
            Object data = message.getData();
            if (!(data instanceof Map)) {
                return Message.failure(ActionType.LOGIN, "数据格式错误");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, String> passwordData = (Map<String, String>) data;
            
            String userId = passwordData.get("userId");
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            
            // 参数验证
            if (userId == null || oldPassword == null || newPassword == null) {
                return Message.failure(ActionType.LOGIN, "参数不完整");
            }
            
            // 更新密码
            boolean success = userService.updatePassword(userId, oldPassword, newPassword);
            
            if (success) {
                return Message.success(ActionType.LOGIN, "密码更新成功");
            } else {
                return Message.failure(ActionType.LOGIN, "密码更新失败，请检查旧密码是否正确");
            }
            
        } catch (Exception e) {
            logger.error("处理更新密码请求异常: {}", e.getMessage(), e);
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
}
