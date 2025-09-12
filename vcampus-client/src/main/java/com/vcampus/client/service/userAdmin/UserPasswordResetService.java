package com.vcampus.client.service.userAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;

/**
 * 用户密码重置服务类
 * 负责处理用户密码重置相关的客户端业务逻辑
 * 编写人：AI Assistant
 */
public class UserPasswordResetService {
    
    private final SocketClient socketClient;
    
    public UserPasswordResetService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return Message 发送结果消息（只确认是否成功发送）
     */
    public Message resetUserPassword(String userId, String newPassword) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.RESET_USER_PASSWORD, "网络连接未建立");
            }
            
            // 创建User对象
            User user = new User(userId.trim(), newPassword);
            
            // 发送请求到服务器
            Message request = new Message(ActionType.RESET_USER_PASSWORD, user);
            Message response = socketClient.sendMessage(request);
            return response; // 只返回发送结果，不处理响应数据
            
        } catch (Exception e) {
            System.err.println("发送重置密码请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.RESET_USER_PASSWORD, "发送请求失败：" + e.getMessage());
        }
    }
}
