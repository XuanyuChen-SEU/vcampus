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
            String searchData = (String) message.getData();
            
            // 2. 解析搜索数据：格式为 "关键词|状态"
            String keyword = "";
            final String status;
            if (searchData != null && !searchData.isEmpty()) {
                String[] parts = searchData.split("\\|");
                if (parts.length >= 1) {
                    keyword = parts[0];
                }
                if (parts.length >= 2) {
                    status = parts[1];
                } else {
                    status = "全部";
                }
            } else {
                status = "全部";
            }
            
            System.out.println("搜索商品 - 关键词: '" + keyword + "', 状态: '" + status + "'");

            List<Product> products = shopService.searchProducts(keyword);
            
            // 3. 如果指定了状态筛选，进行额外过滤
            if (!"全部".equals(status) && products != null) {
                products = products.stream()
                    .filter(p -> p.getStatus().toString().equals(status))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            System.out.println("搜索结果数量: " + (products != null ? products.size() : 0));
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

    /**
     * 【新增】处理“获取商品详情”的请求。
     * 这个方法是请求的入口，它调用 Service 层来完成工作。
     *
     * @param message 客户端请求，data 字段为 String 类型的商品ID。
     * @return 包含商品详情或错误信息的响应 Message。
     */
    public Message handleGetProductDetail(Message message) {
        try {
            // 1. 验证传入的数据类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_GET_PRODUCT_DETAIL, "无效的请求数据：商品ID必须为字符串。");
            }
            String productId = (String) message.getData();

            // 2. 调用 Service 层处理业务逻辑
            Product product = shopService.getProductDetail(productId);

            if (product != null) {
                // 3. 如果 Service 层成功返回数据，构建成功的响应
                return Message.success(ActionType.SHOP_GET_PRODUCT_DETAIL, product, "获取商品详情成功");
            } else {
                // 4. 如果 Service 层返回 null (表示DAO没找到)，构建失败的响应
                return Message.failure(ActionType.SHOP_GET_PRODUCT_DETAIL, "商品不存在或已下架。");
            }
            // 5. 捕获 Service 层可能抛出的任何异常（如参数错误）
        } catch (IllegalArgumentException e) {
            return Message.failure(ActionType.SHOP_GET_PRODUCT_DETAIL, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_GET_PRODUCT_DETAIL, "服务器内部错误: " + e.getMessage());
        }
    }


    // ==========================================================
    // 商店管理员相关方法
    // ==========================================================

    /**
     * 处理"添加商品"的请求
     * @param message 客户端请求 (data 部分为 Product 对象)
     * @return 包含操作结果的响应 Message
     */
    public Message handleAddProduct(Message message) {
        try {
            // 1. 验证传入的数据是否是 Product 类型
            if (!(message.getData() instanceof Product)) {
                return Message.failure(ActionType.SHOP_ADMIN_ADD_PRODUCT, "无效的请求数据：商品信息必须为Product对象。");
            }
            Product product = (Product) message.getData();

            // 2. 调用 Service 层处理业务逻辑
            boolean success = shopService.addProduct(product);
            
            if (success) {
                return Message.success(ActionType.SHOP_ADMIN_ADD_PRODUCT, "商品添加成功");
            } else {
                return Message.failure(ActionType.SHOP_ADMIN_ADD_PRODUCT, "商品添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_ADD_PRODUCT, "添加商品失败: " + e.getMessage());
        }
    }

    /**
     * 处理"更新商品"的请求
     * @param message 客户端请求 (data 部分为 Product 对象)
     * @return 包含操作结果的响应 Message
     */
    public Message handleUpdateProduct(Message message) {
        try {
            // 1. 验证传入的数据是否是 Product 类型
            if (!(message.getData() instanceof Product)) {
                return Message.failure(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, "无效的请求数据：商品信息必须为Product对象。");
            }
            Product product = (Product) message.getData();

            // 2. 调用 Service 层处理业务逻辑
            boolean success = shopService.updateProduct(product);
            
            if (success) {
                return Message.success(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, "商品更新成功");
            } else {
                return Message.failure(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, "商品更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_UPDATE_PRODUCT, "更新商品失败: " + e.getMessage());
        }
    }

    /**
     * 处理"删除商品"的请求
     * @param message 客户端请求 (data 部分为 String 类型的商品ID)
     * @return 包含操作结果的响应 Message
     */
    public Message handleDeleteProduct(Message message) {
        try {
            // 1. 验证传入的数据是否是 String 类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_ADMIN_DELETE_PRODUCT, "无效的请求数据：商品ID必须为字符串。");
            }
            String productId = (String) message.getData();

            // 2. 调用 Service 层处理业务逻辑
            boolean success = shopService.deleteProduct(productId);
            
            if (success) {
                return Message.success(ActionType.SHOP_ADMIN_DELETE_PRODUCT, "商品删除成功");
            } else {
                return Message.failure(ActionType.SHOP_ADMIN_DELETE_PRODUCT, "商品删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_DELETE_PRODUCT, "删除商品失败: " + e.getMessage());
        }
    }

    /**
     * 处理"获取所有订单"的请求
     * @param message 客户端请求 (data 部分应为 null)
     * @return 包含订单列表的响应 Message
     */
    public Message handleGetAllOrders(Message message) {
        try {
            List<ShopTransaction> orders = shopService.getAllOrders();
            return Message.success(ActionType.SHOP_ADMIN_GET_ALL_ORDERS, orders, "获取所有订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_GET_ALL_ORDERS, "获取所有订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理"获取所有收藏"的请求
     * @param message 客户端请求 (data 部分应为 null)
     * @return 包含收藏列表的响应 Message
     */
    public Message handleGetAllFavorites(Message message) {
        try {
            List<ShopTransaction> favorites = shopService.getAllFavorites();
            return Message.success(ActionType.SHOP_ADMIN_GET_ALL_FAVORITES, favorites, "获取所有收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_GET_ALL_FAVORITES, "获取所有收藏失败: " + e.getMessage());
        }
    }

    /**
     * 处理"管理员根据用户ID获取订单"的请求
     * @param message 客户端请求 (data 部分为 String 类型的用户ID)
     * @return 包含订单列表的响应 Message
     */
    public Message handleGetOrdersByUser(Message message) {
        try {
            // 1. 验证传入的数据是否是 String 类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, "无效的请求数据：用户ID必须为字符串。");
            }
            String userId = (String) message.getData();

            // 2. 验证 userId 是否为空
            if (userId == null || userId.trim().isEmpty()) {
                return Message.failure(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, "无效的请求数据：用户ID不能为空。");
            }

            List<ShopTransaction> orders = shopService.getOrdersByUserIdForAdmin(userId);
            return Message.success(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, orders, "根据用户ID获取订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_GET_ORDERS_BY_USER, "根据用户ID获取订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理"管理员根据用户ID获取收藏"的请求
     * @param message 客户端请求 (data 部分为 String 类型的用户ID)
     * @return 包含收藏列表的响应 Message
     */
    public Message handleGetFavoritesByUser(Message message) {
        try {
            // 1. 验证传入的数据是否是 String 类型
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.SHOP_ADMIN_GET_FAVORITES_BY_USER, "无效的请求数据：用户ID必须为字符串。");
            }
            String userId = (String) message.getData();

            // 2. 验证 userId 是否为空
            if (userId == null || userId.trim().isEmpty()) {
                return Message.failure(ActionType.SHOP_ADMIN_GET_FAVORITES_BY_USER, "无效的请求数据：用户ID不能为空。");
            }

            List<ShopTransaction> favorites = shopService.getFavoritesByUserIdForAdmin(userId);
            return Message.success(ActionType.SHOP_ADMIN_GET_FAVORITES_BY_USER, favorites, "根据用户ID获取收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADMIN_GET_FAVORITES_BY_USER, "根据用户ID获取收藏失败: " + e.getMessage());
        }
    }
}