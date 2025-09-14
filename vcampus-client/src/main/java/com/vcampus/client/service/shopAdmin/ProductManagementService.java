package com.vcampus.client.service.shopAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.enums.ActionType;

/**
 * 商品管理服务类
 * 负责处理商品管理相关的客户端业务逻辑
 * 编写人：AI Assistant
 */
public class ProductManagementService {
    
    private final SocketClient socketClient;
    
    public ProductManagementService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 搜索商品
     * @param searchText 搜索关键词
     * @param selectedStatus 选择的状态
     * @return Message 发送结果消息
     */
    public Message searchProducts(String searchText, String selectedStatus) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_SEARCH_PRODUCTS, "网络连接未建立");
            }
            
            // 创建搜索对象（这里简化处理，实际可能需要更复杂的搜索对象）
            String searchData = searchText + "|" + selectedStatus;
            Message request = new Message(ActionType.SHOP_SEARCH_PRODUCTS, searchData);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送搜索商品请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_SEARCH_PRODUCTS, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品
     * @param product 商品信息
     * @return Message 发送结果消息
     */
    public Message updateProduct(Product product) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, "网络连接未建立");
            }
            
            Message request = new Message(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, product);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送更新商品请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return Message 发送结果消息
     */
    public Message getProductDetail(String productId) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_GET_PRODUCT_DETAIL, "网络连接未建立");
            }
            
            Message request = new Message(ActionType.SHOP_GET_PRODUCT_DETAIL, productId);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送获取商品详情请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_GET_PRODUCT_DETAIL, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品
     * @param productId 商品ID
     * @return Message 发送结果消息
     */
    public Message deleteProduct(String productId) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.SHOP_ADMIN_DELETE_PRODUCT, "网络连接未建立");
            }
            
            Message request = new Message(ActionType.SHOP_ADMIN_DELETE_PRODUCT, productId);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("发送删除商品请求时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SHOP_ADMIN_DELETE_PRODUCT, "发送请求失败：" + e.getMessage());
        }
    }
}
