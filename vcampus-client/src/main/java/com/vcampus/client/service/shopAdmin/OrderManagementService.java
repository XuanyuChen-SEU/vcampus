package com.vcampus.client.service.shopAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 订单管理服务类
 * 负责处理订单管理相关的客户端业务逻辑
 * 编写人：AI Assistant
 */
public class OrderManagementService {
    
    private final SocketClient socketClient;
    
    public OrderManagementService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 获取所有订单
     * @return Message 发送结果消息
     */
    public Message getAllOrders() {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_ADMIN_GET_ALL_ORDERS, "网络连接未建立");
            }
            
            Message request = new Message(ActionType.SHOP_ADMIN_GET_ALL_ORDERS, null);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送获取所有订单请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_ADMIN_GET_ALL_ORDERS, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID搜索订单
     * @param userId 用户ID
     * @return Message 发送结果消息
     */
    public Message searchOrdersByUserId(String userId) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, "网络连接未建立");
            }
            
            Message request = new Message(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, userId);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送根据用户ID搜索订单请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, "发送请求失败：" + e.getMessage());
        }
    }
}
