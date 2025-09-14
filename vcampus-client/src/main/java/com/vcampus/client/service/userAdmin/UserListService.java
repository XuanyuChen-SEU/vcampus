package com.vcampus.client.service.userAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.UserSearch;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 用户列表服务类
 * 负责处理用户列表相关的客户端业务逻辑
 * 编写人：AI Assistant
 */
public class UserListService {
    
    private final SocketClient socketClient;
    
    public UserListService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 搜索用户（统一搜索方法）
     * @param searchText 搜索关键词（为空表示搜索所有）
    * @param selectedRole 角色
     * @return Message 发送结果消息（只确认是否成功发送）
     */
    public Message search(String searchText, String selectedRole) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SEARCH_USERS, "网络连接未建立");
            }
            UserSearch userSearch = new UserSearch(searchText.trim(), selectedRole);
            Message request = new Message(ActionType.SEARCH_USERS, userSearch);
            Message response = socketClient.sendMessage(request);
            return response; // 只返回发送结果，不处理响应数据
            
        } catch (Exception e) {
            System.err.println("发送搜索请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SEARCH_USERS, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return Message 发送结果消息（只确认是否成功发送）
     */
    public Message deleteUser(String userId) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.DELETE_USER, "网络连接未建立");
            }
            
            Message request = new Message(ActionType.DELETE_USER, userId);
            Message response = socketClient.sendMessage(request);
            return response; // 只返回发送结果，不处理响应数据
            
        } catch (Exception e) {
            System.err.println("发送删除用户请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.DELETE_USER, "发送请求失败：" + e.getMessage());
        }
    }
}