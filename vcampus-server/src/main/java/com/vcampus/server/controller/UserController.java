package com.vcampus.server.controller;

import java.util.Map;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.UserService;

/**
 * 用户控制器类
 * 处理客户端发送的用户相关请求
 * 编写人：谌宣羽
 */
public class UserController implements IController {
    
    private final UserService userService;
    
    public UserController() {
        this.userService = new UserService();
    }
    
    @Override
    public Message handleRequest(Message message) {
        ActionType action = message.getAction();
        
        switch (action) {
            case LOGIN:
                return handleLogin(message);
                
            case LOGOUT:
                return handleLogout(message);
                
            default:
                return Message.failure(action, "不支持的操作类型");
        }
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{"LOGIN", "LOGOUT"};
    }
    
    /**
     * 处理登录请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    private Message handleLogin(Message message) {
        try {
            // 验证消息格式
            if (message == null || message.getData() == null) {
                return Message.failure(ActionType.LOGIN, "请求格式错误");
            }
            
            // 解析登录数据
            Object data = message.getData();
            if (!(data instanceof Map)) {
                return Message.failure(ActionType.LOGIN, "数据格式错误");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, String> loginData = (Map<String, String>) data;
            
            String userId = loginData.get("userId");
            String password = loginData.get("password");
            
            // 参数验证
            if (userId == null || password == null) {
                return Message.failure(ActionType.LOGIN, "用户名或密码不能为空");
            }
            
            // 调用业务逻辑进行登录验证
            User user = userService.login(userId, password);
            
            if (user != null) {
                // 登录成功
                // 设置当前连接的用户ID（这里需要获取当前连接）
                // TODO: 需要从消息中获取连接信息或通过其他方式设置
                
                // 创建返回数据（不包含密码）
                Map<String, Object> userData = Map.of(
                    "userId", user.getUserId(),
                    "phone", user.getPhone(),
                    "role", user.getRole().getDesc()
                );
                
                return Message.success(ActionType.LOGIN, userData, "登录成功");
            } else {
                // 登录失败
                return Message.failure(ActionType.LOGIN, "用户名或密码错误");
            }
            
        } catch (Exception e) {
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
    
    /**
     * 处理登出请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    private Message handleLogout(Message message) {
        try {
            // 登出逻辑相对简单，主要是客户端清理本地状态
            return Message.success(ActionType.LOGOUT, "登出成功");
            
        } catch (Exception e) {
            return Message.failure(ActionType.LOGOUT, "服务器内部错误");
        }
    }
}
