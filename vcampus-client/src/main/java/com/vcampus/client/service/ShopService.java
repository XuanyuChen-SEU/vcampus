package com.vcampus.client.service;

import com.vcampus.client.MainApp; // 假设您的主应用类在这个包下
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 商店模块的服务层 (ShopService)
 * 精准匹配 ShopController 的当前功能需求，提供后台网络通信支持。
 */
public class ShopService {

    private final SocketClient socketClient;

    /**
     * 构造函数，通过 MainApp 获取全局唯一的 SocketClient 实例。
     */
    public ShopService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }

    /**
     * 【为 initialize() 服务】
     * 获取初始显示的商品列表。
     * @return 包含商品列表的响应 Message 对象。
     */
    public Message getAllProducts() {
        // 创建一个不带数据的请求消息，因为服务器知道这个指令是获取所有商品
        Message request = new Message(ActionType.SHOP_GET_ALL_PRODUCTS, null);

        // 发送请求并同步等待服务器的响应
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 handleSearch() 服务】
     * 根据用户输入的关键词搜索商品。
     * @param keyword 搜索关键词 (String)。
     * @return 包含搜索结果商品列表的响应 Message 对象。
     */
    public Message searchProducts(String keyword) {
        // 将关键词作为数据发送给服务器
        Message request = new Message(ActionType.SHOP_SEARCH_PRODUCTS, keyword);
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 handleShowMyOrders() 服务】
     * 获取当前登录用户的所有订单。
     * @param userId 当前登录用户的ID (String)。
     * @return 包含该用户订单列表的响应 Message 对象。
     */
    public Message getMyOrders(String userId) {
        // 将用户ID作为数据发送，告诉服务器要查询谁的订单
        Message request = new Message(ActionType.SHOP_GET_MY_ORDERS, userId);
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 handleShowMyFavorites() 服务】
     * 获取当前登录用户的所有收藏。
     * @param userId 当前登录用户的ID (String)。
     * @return 包含该用户收藏列表的响应 Message 对象。
     */
    public Message getMyFavorites(String userId) {
        // 将用户ID作为数据发送，告诉服务器要查询谁的收藏
        Message request = new Message(ActionType.SHOP_GET_MY_FAVORITES, userId);
        return socketClient.sendMessage(request);
    }

    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    public void getProductDetail(String productId) {
        // 【核心修正】只发送消息，不等待响应。
        // 我们假设发送方法是 getGlobalSocketClient().send(message)
        // 请根据您项目中其他 Service 的写法，确认是 send 还是 sendMessage
        Message request = new Message(ActionType.SHOP_GET_PRODUCT_DETAIL, productId);
        getGlobalSocketClient().sendMessage(request);
    }
}