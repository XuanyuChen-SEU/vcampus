package com.vcampus.client.service.userAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.Role;

/**
 * 用户创建服务类
 * 负责处理用户创建相关的客户端业务逻辑
 * 编写人：AI Assistant
 */
public class UserCreateService {
    
    private final SocketClient socketClient;
    
    public UserCreateService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 创建新用户
     * @param userId 用户ID
     * @param password 初始密码
     * @return Message 发送结果消息（只确认是否成功发送）
     */
    public Message createUser(String userId, String password) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.CREATE_USER, "网络连接未建立");
            }
            
            // 创建用户对象
            User newUser = new User(userId.trim(), password);
            
            // 发送请求到服务器
            Message request = new Message(ActionType.CREATE_USER, newUser);
            Message response = socketClient.sendMessage(request);
            return response; // 只返回发送结果，不处理响应数据
            
        } catch (Exception e) {
            System.err.println("发送创建用户请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.CREATE_USER, "发送请求失败：" + e.getMessage());
        }
    }
}
