package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.UserService;


/**
 * 用户控制器
 * 处理用户相关的请求，包括登录、忘记密码、重置密码等
 * 编写人：谌宣羽
 */
public class UserController {
    
    private final UserService userService;
    
    public UserController()  {
        this.userService = new UserService();
    }
    
    /**
     * 处理登录请求
     * @param message 登录请求消息
     * @return 登录响应消息
     */
    public Message handleLogin(Message message) {
        try {
            // 获取用户信息
            User loginUser = (User) message.getData();
            
            // 调用用户服务进行登录验证
            Message result = userService.validateLogin(loginUser);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.LOGIN, result.getData(), result.getMessage());
            } else {
                return Message.failure(ActionType.LOGIN, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理登录请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
    
    /**
     * 处理忘记密码请求
     * @param message 忘记密码请求消息
     * @return 忘记密码响应消息
     */
    public Message handleForgetPassword(Message message) {
        try {
            // 获取用户信息
            User user = (User) message.getData();
            
            // 调用用户服务处理忘记密码申请
            Message result = userService.handleForgetPassword(user);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.FORGET_PASSWORD, result.getMessage());
            } else {
                return Message.failure(ActionType.FORGET_PASSWORD, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理忘记密码请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.FORGET_PASSWORD, "服务器内部错误");
        }
    }
}
