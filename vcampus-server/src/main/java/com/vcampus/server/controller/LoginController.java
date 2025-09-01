package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.LoginService;

/**
 * 登录控制器
 * 专门处理登录相关的业务逻辑和参数验证
 * 编写人：cursor
 */
public class LoginController {
    
    private final LoginService loginService;
    
    public LoginController() {
        this.loginService = new LoginService();
    }
    
    /**
     * 处理登录请求
     * @param request 登录请求消息
     * @return 登录响应消息
     */
    public Message handleLogin(Message request) {
        try {
            User loginUser = (User) request.getData();
            LoginService.LoginResult result = loginService.validateLogin(loginUser);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.LOGIN, result.getUser(), result.getMessage());
            } else {
                return Message.failure(ActionType.LOGIN, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理登录请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.LOGIN, "登录处理失败");
        }
    }
    
    /**
     * 获取登录服务实例（用于其他控制器或服务调用）
     * @return 登录服务实例
     */
    public LoginService getLoginService() {
        return loginService;
    }
}
