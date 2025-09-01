package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.client.net.MessageDispatcher;
import com.vcampus.common.dto.User;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import javafx.application.Platform;

/**
 * 登录服务类
 * 负责用户相关的客户端逻辑，包括数据验证、采集和网络通信
 * 编写人：cursor
 */
public class LoginService {
    
    private final SocketClient socketClient;
    private LoginCallback loginCallback;
    
    /**
     * 登录回调接口
     */
    public interface LoginCallback {
        void onLoginSuccess(String message);
        void onLoginFailure(String error);
        void onConnectionError(String error);
    }
    
    public LoginService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 设置登录回调
     * @param callback 登录回调接口
     */
    public void setLoginCallback(LoginCallback callback) {
        this.loginCallback = callback;
    }
    
    /**
     * 从表单数据创建用户对象（用于发送给服务端）
     * @param userId 用户名
     * @param password 密码（明文）
     * @return 用户对象
     */
    public User collectLoginData(String userId, String password) {
        // 客户端只负责数据采集，不进行业务验证
        // 密码加密等处理由服务端负责
        return new User(userId, password);
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
     * 执行登录
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password) {
        // 创建用户对象
        User loginUser = collectLoginData(username, password);
        
        // 创建登录消息
        Message loginMessage = new Message(ActionType.LOGIN, loginUser);
        
        // 注册登录消息处理器
        registerLoginHandler();
        
        // 发送登录请求
        Message result = socketClient.sendMessage(loginMessage);
        
        if (!result.isSuccess()) {
            // 发送失败
            if (loginCallback != null) {
                Platform.runLater(() -> 
                    loginCallback.onConnectionError("发送登录请求失败: " + result.getMessage())
                );
            }
        }
    }
    
    /**
     * 注册登录消息处理器
     */
    private void registerLoginHandler() {
        MessageDispatcher.getInstance().registerHandler(ActionType.LOGIN, 
            message -> {
                handleLoginResponse(message);
                // 处理完成后注销处理器
                MessageDispatcher.getInstance().unregisterHandler(ActionType.LOGIN);
            });
    }
    
    /**
     * 处理登录响应
     * @param response 服务端响应
     */
    private void handleLoginResponse(Message response) {
        if (loginCallback != null) {
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    loginCallback.onLoginSuccess(response.getMessage());
                } else {
                    loginCallback.onLoginFailure(response.getMessage());
                }
            });
        }
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
