package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.entity.Balance;
import java.math.BigDecimal; // 使用 BigDecimal 处理金额，更精确

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

    /**
     * 【异步】发送“获取余额”的请求。
     * @param userId 用户ID
     */
    public void getBalance(String userId) {
        System.out.println("客户端 ShopService：异步发送获取余额请求...");
        socketClient.sendMessage(new Message(ActionType.SHOP_GET_BALANCE, userId));
    }

    /**
     * 【异步】发送“充值”的请求。
     * @param userId 用户ID
     * @param amount 充值金额
     */
    public void recharge(String userId, BigDecimal amount) {
        System.out.println("客户端 ShopService：异步发送充值请求...");
        Balance rechargeData = new Balance();
        rechargeData.setUserId(userId);
        rechargeData.setBalance(amount);
        socketClient.sendMessage(new Message(ActionType.SHOP_RECHARGE, rechargeData));
    }

    // 在 ShopService.java (客户端) 中添加
    public void payForOrder(ShopTransaction order) {
        System.out.println("客户端 ShopService：异步发送支付请求...");
        // 假设你已经在 ActionType 枚举中添加了 SHOP_PAY_FOR_ORDER
        socketClient.sendMessage(new Message(ActionType.SHOP_PAY_FOR_ORDER, order));
    }

    /**
     * 【新增】发送“删除订单”的请求。
     * @param orderId 要删除的订单ID。
     */
    public void deleteOrder(String orderId) {
        System.out.println("客户端 ShopService：异步发送删除订单请求 (ID: " + orderId + ")...");
        socketClient.sendMessage(new Message(ActionType.SHOP_DELETE_ORDER, orderId));
    }

    /**
     * 【新增】发送“为未支付订单付款”的请求。
     * @param order 包含订单ID和用户ID的 ShopTransaction 对象。
     */
    public void payForUnpaidOrder(ShopTransaction order) {
        System.out.println("客户端 ShopService：异步发送支付未支付订单的请求...");
        socketClient.sendMessage(new Message(ActionType.SHOP_PAY_FOR_UNPAID_ORDER, order));
    }
}