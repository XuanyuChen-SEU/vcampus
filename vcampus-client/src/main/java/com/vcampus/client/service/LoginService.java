package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;

/**
 * 登录服务类
 * 编写人：谌宣羽
 */
public class LoginService {
    
    private final SocketClient socketClient;
    
    public LoginService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 验证输入格式（客户端基础验证）
     * @param userId 用户ID
     * @param password 密码
     * @return 验证结果
     */
    public boolean validateInputFormat(String userId, String password) {
        return userId != null && !userId.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * 执行登录（同步模式）
     * @param username 用户名
     * @param password 密码
     * @return 登录结果消息
     */
    public Message login(String username, String password) {
        // 基础验证
        if (!validateInputFormat(username, password)) {
            return Message.failure(ActionType.LOGIN, "用户名和密码不能为空");
        }
        
        // 检查连接状态
        if (!socketClient.isConnected()) {
            return Message.failure(ActionType.LOGIN, "网络连接未建立");
        }
        
        // 创建用户对象
        User loginUser = new User(username, password);
        
        // 创建登录消息
        Message loginMessage = new Message(ActionType.LOGIN, loginUser);
        
        // 发送登录请求，响应会由SocketClient中的MessageController自动处理
        Message response = socketClient.sendMessage(loginMessage);
        
        return response;
    }
    
    /**
     * 提交密码重置申请
     * @param username 用户名
     * @param oldPassword 原密码
     * @return 密码重置结果消息
     */
    public Message submitPasswordResetRequest(String username, String oldPassword) {
        // 基础验证
        if (!validateInputFormat(username, oldPassword)) {
            return Message.failure(ActionType.FORGET_PASSWORD, "用户名和原密码不能为空");
        }
        
        // 检查连接状态
        if (!socketClient.isConnected()) {
            return Message.failure(ActionType.FORGET_PASSWORD, "网络连接未建立");
        }
        
        // 创建用户对象
        User resetUser = new User(username, oldPassword);
        
        // 创建密码重置消息
        Message resetMessage = new Message(ActionType.FORGET_PASSWORD, resetUser);
        
        // 发送密码重置请求，响应会由SocketClient中的MessageController自动处理
        Message response = socketClient.sendMessage(resetMessage);
        
        return response;
    }
    
    /**
     * 获取全局Socket客户端
     * @return SocketClient实例
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }
    
    /**
     * 获取登录数据用于显示（调试用）
     * @param userId 用户ID
     * @param password 密码
     * @return 格式化的登录信息
     */
    public String getLoginInfoForDisplay(String userId, String password) {
        return String.format("用户名: %s, 密码: %s", userId, password);
    }
}
