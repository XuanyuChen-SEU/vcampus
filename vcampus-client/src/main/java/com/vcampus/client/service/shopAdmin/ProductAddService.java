package com.vcampus.client.service.shopAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.enums.ActionType;

/**
 * 商品添加服务类
 * 负责处理商品添加相关的客户端业务逻辑
 * 编写人：AI Assistant
 */
public class ProductAddService {
    
    private final SocketClient socketClient;
    
    public ProductAddService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 添加新商品
     * @param product 商品信息
     * @return Message 发送结果消息
     */
    public Message addProduct(Product product) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_ADMIN_ADD_PRODUCT, "网络连接未建立");
            }
            
            // 发送请求到服务器
            Message request = new Message(ActionType.SHOP_ADMIN_ADD_PRODUCT, product);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送添加商品请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_ADMIN_ADD_PRODUCT, "发送请求失败：" + e.getMessage());
        }
    }
}
