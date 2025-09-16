package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.ActionType;

/**
 * 商店模块的服务层 (ShopService) - 客户端
 * 【异步非阻塞模型版本】
 * 负责将 ShopController 的请求异步发送给服务器，不等待响应。
 * 所有方法都返回 void。
 */
public class ShopService {

    private final SocketClient socketClient = MainApp.getGlobalSocketClient();

    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    // --- 商品浏览相关 ---

    public void getAllProducts() {
        socketClient.sendMessage(new Message(ActionType.SHOP_GET_ALL_PRODUCTS, null));
    }

    public void searchProducts(String keyword) {
        socketClient.sendMessage(new Message(ActionType.SHOP_SEARCH_PRODUCTS, keyword));
    }

    public void getProductDetail(String productId) {
        socketClient.sendMessage(new Message(ActionType.SHOP_GET_PRODUCT_DETAIL, productId));
    }

    // --- 订单与收藏相关 ---

    public void getMyOrders(String userId) {
        socketClient.sendMessage(new Message(ActionType.SHOP_GET_MY_ORDERS, userId));
    }

    public void getMyFavorites(String userId) {
        socketClient.sendMessage(new Message(ActionType.SHOP_GET_MY_FAVORITES, userId));
    }

    // --- 【异步】的购买与收藏方法 ---

    /**
     * 【异步】发送“创建订单”的请求。
     * @param orderRequest 包含用户ID和商品信息的 ShopTransaction 对象。
     */
    public void createOrder(ShopTransaction orderRequest) {
        System.out.println("客户端 ShopService：异步发送创建订单请求...");
        socketClient.sendMessage(new Message(ActionType.SHOP_CREATE_ORDER, orderRequest));
    }

    /**
     * 【异步】发送“添加收藏”的请求。
     * @param favoriteRequest 包含用户ID和商品信息的 ShopTransaction 对象。
     */
    public void addFavorite(ShopTransaction favoriteRequest) {
        System.out.println("客户端 ShopService：异步发送添加收藏请求...");
        socketClient.sendMessage(new Message(ActionType.SHOP_ADD_FAVORITE, favoriteRequest));
    }

    /**
     * 【异步】发送“取消收藏”的请求。
     * @param favoriteId 要取消的收藏记录在数据库中的唯一ID。
     */
    public void removeFavorite(String favoriteId) {
        System.out.println("客户端 ShopService：异步发送取消收藏请求 (ID: " + favoriteId + ")...");
        socketClient.sendMessage(new Message(ActionType.SHOP_REMOVE_FAVORITE, favoriteId));
    }
}