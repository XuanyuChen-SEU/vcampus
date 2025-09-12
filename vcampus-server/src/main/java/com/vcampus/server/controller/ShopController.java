package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.ShopService;

import java.util.List;

/**
 * 商店模块的控制器 (ShopController) - 服务端 (已优化异常处理)
 * 负责接收 MessageController 路由过来的商店相关请求，
 * 调用 ShopService 处理业务逻辑，并将结果打包成 Message 返回。
 */
public class ShopController {

    private final ShopService shopService = new ShopService();

    /**
     * 处理“获取所有商品”的请求
     * @param message 客户端请求 (data 部分应为 null)
     * @return 包含商品列表的响应 Message
     */
    public Message handleGetAllProducts(Message message) {
        try {
            List<Product> products = shopService.getAllProducts();
            return Message.success(ActionType.SHOP_GET_ALL_PRODUCTS, products, "获取商品列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_GET_ALL_PRODUCTS, "服务器获取商品失败: " + e.getMessage());
        }
    }

    /**
     * 处理“搜索商品”的请求
     * @param message 客户端请求 (data 部分为 String 类型的关键词)
     * @return 包含搜索结果的响应 Message
     */
    public Message handleSearchProducts(Message message) {
        try {
            // 1. 验证传入的数据是否是 String 类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_SEARCH_PRODUCTS, "无效的请求数据：搜索关键词必须为字符串。");
            }
            String keyword = (String) message.getData();

            List<Product> products = shopService.searchProducts(keyword);
            return Message.success(ActionType.SHOP_SEARCH_PRODUCTS, products, "搜索成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_SEARCH_PRODUCTS, "搜索失败: " + e.getMessage());
        }
    }

    /**
     * 处理“获取我的订单”的请求
     * @param message 客户端请求 (data 部分为 String 类型的 userId)
     * @return 包含订单列表的响应 Message
     */
    public Message handleGetMyOrders(Message message) {
        try {
            // 1. 验证传入的数据是否是 String 类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_GET_MY_ORDERS, "无效的请求数据：用户ID必须为字符串。");
            }
            String userId = (String) message.getData();

            // 2. (可选) 验证 userId 是否为空
            if (userId == null || userId.trim().isEmpty()) {
                return Message.failure(ActionType.SHOP_GET_MY_ORDERS, "无效的请求数据：用户ID不能为空。");
            }

            List<ShopTransaction> orders = shopService.getMyOrders(userId);
            return Message.success(ActionType.SHOP_GET_MY_ORDERS, orders, "获取订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_GET_MY_ORDERS, "获取订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理“获取我的收藏”的请求
     * @param message 客户端请求 (data 部分为 String 类型的 userId)
     * @return 包含收藏列表的响应 Message
     */
    public Message handleGetMyFavorites(Message message) {
        try {
            // 1. 验证传入的数据是否是 String 类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_GET_MY_FAVORITES, "无效的请求数据：用户ID必须为字符串。");
            }
            String userId = (String) message.getData();

            // 2. (可选) 验证 userId 是否为空
            if (userId == null || userId.trim().isEmpty()) {
                return Message.failure(ActionType.SHOP_GET_MY_FAVORITES, "无效的请求数据：用户ID不能为空。");
            }

            List<ShopTransaction> favorites = shopService.getMyFavorites(userId);
            return Message.success(ActionType.SHOP_GET_MY_FAVORITES, favorites, "获取收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_GET_MY_FAVORITES, "获取收藏失败: " + e.getMessage());
        }
    }


}