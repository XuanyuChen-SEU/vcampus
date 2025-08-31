package com.vcampus.server.controller;

import java.util.Map;

import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.net.ConnectionManager;

/**
 * 消息控制器类
 * 处理客户端发送的消息相关请求
 * 编写人：谌宣羽
 */
public class MessageController implements IController {
    
    private final ConnectionManager connectionManager;
    
    public MessageController() {
        this.connectionManager = ConnectionManager.getInstance();
    }
    
    @Override
    public Message handleRequest(Message message) {
        ActionType action = message.getAction();
        
        switch (action) {
            case LOGIN: // 这里应该使用消息相关的ActionType
                return handleSendToUser(message);
                
            case LOGOUT: // 这里应该使用消息相关的ActionType
                return handleSendToClient(message);
                
            default:
                return Message.failure(action, "不支持的操作类型");
        }
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[]{"SEND_TO_USER", "SEND_TO_CLIENT", "BROADCAST"};
    }
    
    /**
     * 处理向指定用户发送消息的请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    private Message handleSendToUser(Message message) {
        try {
            // 解析消息数据
            Object data = message.getData();
            if (!(data instanceof Map)) {
                return Message.failure(ActionType.LOGIN, "数据格式错误");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> messageData = (Map<String, Object>) data;
            
            String targetUserId = (String) messageData.get("targetUserId");
            String content = (String) messageData.get("content");
            
            if (targetUserId == null || content == null) {
                return Message.failure(ActionType.LOGIN, "目标用户ID或消息内容为空");
            }
            
            // 创建要发送的消息
            Message targetMessage = Message.success(ActionType.LOGIN, content, "收到新消息");
            
            // 向指定用户发送消息
            boolean success = connectionManager.sendToUser(targetUserId, targetMessage);
            
            if (success) {
                return Message.success(ActionType.LOGIN, "消息发送成功");
            } else {
                return Message.failure(ActionType.LOGIN, "用户不在线或消息发送失败");
            }
            
        } catch (Exception e) {
            return Message.failure(ActionType.LOGIN, "消息发送异常");
        }
    }
    
    /**
     * 处理向指定客户端发送消息的请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    private Message handleSendToClient(Message message) {
        try {
            // 解析消息数据
            Object data = message.getData();
            if (!(data instanceof Map)) {
                return Message.failure(ActionType.LOGOUT, "数据格式错误");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> messageData = (Map<String, Object>) data;
            
            String connectionId = (String) messageData.get("connectionId");
            String content = (String) messageData.get("content");
            
            if (connectionId == null || content == null) {
                return Message.failure(ActionType.LOGOUT, "连接ID或消息内容为空");
            }
            
            // 创建要发送的消息
            Message targetMessage = Message.success(ActionType.LOGOUT, content, "收到新消息");
            
            // 向指定客户端发送消息
            boolean success = connectionManager.sendToClient(connectionId, targetMessage);
            
            if (success) {
                return Message.success(ActionType.LOGOUT, "消息发送成功");
            } else {
                return Message.failure(ActionType.LOGOUT, "客户端连接不存在或消息发送失败");
            }
            
        } catch (Exception e) {
            return Message.failure(ActionType.LOGOUT, "消息发送异常");
        }
    }
    
    /**
     * 获取当前连接信息
     * @return 连接信息消息
     */
    public Message getConnectionInfo() {
        try {
            int connectionCount = connectionManager.getConnectionCount();
            java.util.Set<String> connectionIds = connectionManager.getAllConnectionIds();
            
            Map<String, Object> info = Map.of(
                "connectionCount", connectionCount,
                "connectionIds", connectionIds
            );
            
            return Message.success(ActionType.LOGIN, info, "获取连接信息成功");
            
        } catch (Exception e) {
            return Message.failure(ActionType.LOGIN, "获取连接信息失败");
        }
    }
}
