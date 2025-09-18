package com.vcampus.server.controller;

import java.util.List;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.ShopService;
import com.vcampus.common.entity.Balance;

/**
 * 商店模块的控制器 (ShopController) - 服务端 (已优化异常处理)
 * 负责接收 MessageController 路由过来的商店相关请求，
 * 调用 ShopService 处理业务逻辑，并将结果打包成 Message 返回。
 */
public class ShopController {

    private final ShopService shopService = new ShopService();

    /**
     * 处理“获取所有商品”的请求
     *
     * @param message 客户端请求 (data 部分应为 null)
     * @return 包含商品列表的响应 Message
     */
    public Message handleGetAllProducts(Message message) {
        try {
            List<Product> products = shopService.getAllProducts();
            System.out.println("=== 服务端返回商品列表，共 " + products.size() + " 个商品 ===");
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                System.out.println("  位置 " + i + ": " + product.getName() + " (ID: " + product.getId() + ")");
            }
            System.out.println("=== 服务端商品列表处理完成 ===");
            return Message.success(ActionType.SHOP_GET_ALL_PRODUCTS, products, "获取商品列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_GET_ALL_PRODUCTS, "服务器获取商品失败: " + e.getMessage());
        }
    }

    /**
     * 处理“搜索商品”的请求
     *
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
     *
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
     *
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
            System.out.println("服务端收到商品详情请求，商品ID: " + productId);

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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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

    /**
     * 【已增强】处理“创建订单”的请求，能捕获并返回更精确的错误信息。
     * @param message 客户端请求
     * @return 响应 Message
     */
    public Message handleCreateOrder(Message message) {
        try {
            ShopTransaction orderRequest = (ShopTransaction) message.getData();
            // 现在这个调用可能会抛出我们自定义的异常
            ShopTransaction createdOrder = shopService.createOrder(orderRequest);

            // 如果代码能执行到这里，说明一切成功
            return Message.success(ActionType.SHOP_CREATE_ORDER, createdOrder, "订单创建成功");

        } catch (RuntimeException e) {
            // 【核心】捕获来自 Service 层的业务逻辑异常
            e.printStackTrace(); // 在服务器控制台打印，方便调试
            // 将 Service 层抛出的精确错误信息，返回给客户端
            return Message.failure(ActionType.SHOP_CREATE_ORDER, e.getMessage());
        } catch (Exception e) {
            // 捕获其他所有意料之外的异常
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_CREATE_ORDER, "服务器内部未知错误");
        }
    }
    /**
     * 处理“添加收藏”的请求
     * @param message 客户端请求
     * @return 包含操作结果的响应 Message
     */
    public Message handleAddFavorite(Message message) {
        try {
            ShopTransaction favoriteRequest = (ShopTransaction) message.getData();
            boolean success = shopService.addFavorite(favoriteRequest);

            if (success) {
                return Message.success(ActionType.SHOP_ADD_FAVORITE, null, "收藏成功");
            } else {
                return Message.failure(ActionType.SHOP_ADD_FAVORITE, "已收藏或添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_ADD_FAVORITE, "服务器内部异常: " + e.getMessage());
        }
    }

    /**
     * 处理“取消收藏”的请求
     * @param message 客户端请求
     * @return 包含操作结果的响应 Message
     */
    public Message handleRemoveFavorite(Message message) {
        try {
            String favoriteId = (String) message.getData();
            boolean success = shopService.removeFavorite(favoriteId);

            if (success) {
                return Message.success(ActionType.SHOP_REMOVE_FAVORITE, null, "取消收藏成功");
            } else {
                return Message.failure(ActionType.SHOP_REMOVE_FAVORITE, "取消收藏失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_REMOVE_FAVORITE, "服务器内部异常: " + e.getMessage());
        }
    }

    /**
     * 【已修正】处理“获取余额”的请求。
     * 捕获来自 Service 层的业务异常，并返回统一格式的响应。
     * @param request 客户端请求
     * @return 包含操作结果的响应 Message
     */
    public Message handleGetBalance(Message request) {
        try {
            // 1. 从请求中解析出 userId
            String userId = (String) request.getData();

            // 2. 调用 Service 层处理业务逻辑
            Balance balance = shopService.getBalance(userId);

            // 3. 如果成功，返回一个成功的 Message
            // 注意：这里不再需要 new Message(true, balance, "...") 的旧写法
            return Message.success(ActionType.SHOP_GET_BALANCE, balance, "获取余额成功");

        } catch (Exception e) {
            // 4. 【核心】捕获所有来自 Service 层的异常
            e.printStackTrace(); // 在服务器控制台打印完整错误，方便调试
            // 将异常信息封装成一个失败的 Message 返回给客户端
            return Message.failure(ActionType.SHOP_GET_BALANCE, e.getMessage());
        }
    }

    /**
     * 【已修正】处理“充值”的请求。
     * 捕获来自 Service 层的业务异常，并返回统一格式的响应。
     * @param request 客户端请求
     * @return 包含操作结果的响应 Message
     */
    public Message handleRecharge(Message request) {
        try {
            // 1. 从请求中解析出包含充值信息的 Balance 对象
            Balance rechargeData = (Balance) request.getData();

            // 2. 调用 Service 层处理业务逻辑
            Balance updatedBalance = shopService.recharge(rechargeData);

            // 3. 如果成功，返回一个成功的 Message，其中包含最新的余额信息
            return Message.success(ActionType.SHOP_RECHARGE, updatedBalance, "充值成功");

        } catch (Exception e) {
            // 4. 【核心】捕获所有来自 Service 层的异常
            e.printStackTrace();
            // 将异常信息（如“金额必须大于0”、“用户不存在”）返回给客户端
            return Message.failure(ActionType.SHOP_RECHARGE, e.getMessage());
        }
    }

    public Message handlePayForOrder(Message message) {
        try {
            ShopTransaction orderToPay = (ShopTransaction) message.getData();

            // 1. 调用 Service 层，执行核心业务逻辑，获取更新后的 Balance 对象
            Balance updatedBalance = shopService.payForOrder(orderToPay);

            // 2. 【核心修正】创建一个成功的 Message 对象
            //    a. 先创建一个只包含 ActionType 和 成功数据(updatedBalance) 的消息
            Message successResponse = new Message(ActionType.SHOP_PAY_FOR_ORDER, updatedBalance);

            //    b. 手动设置状态和成功信息
            successResponse.setStatus(true);
            successResponse.setMessage("支付成功！");

            // 3. 返回这个构造完美的成功响应
            return successResponse;

        } catch (Exception e) {
            e.printStackTrace();

            // 【核心修正】对于失败情况，我们使用只包含 ActionType, 状态 和 错误信息 的构造函数
            // 这通常是所有 Message 类都会有的构造函数
            return new Message(ActionType.SHOP_PAY_FOR_ORDER, false, e.getMessage());
        }
    }

    /**
     * 【新增】处理“删除订单”的请求。
     * @param request 客户端请求，data 字段为 String 类型的订单ID。
     * @return 包含操作结果的响应 Message。
     */
    public Message handleDeleteOrder(Message request) {
        try {
            String orderId = (String) request.getData();
            shopService.deleteOrder(orderId);
            return Message.success(ActionType.SHOP_DELETE_ORDER, "订单删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_DELETE_ORDER, e.getMessage());
        }
    }

    /**
     * 【新增】处理“为未支付订单付款”的请求。
     * @param request 客户端请求，data 字段为 ShopTransaction 对象。
     * @return 包含最新余额或错误信息的响应 Message。
     */
    public Message handlePayForUnpaidOrder(Message request) {
        try {
            ShopTransaction orderToPay = (ShopTransaction) request.getData();
            Balance updatedBalance = shopService.payForUnpaidOrder(orderToPay);
            return Message.success(ActionType.SHOP_PAY_FOR_UNPAID_ORDER, updatedBalance, "支付成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SHOP_PAY_FOR_UNPAID_ORDER, e.getMessage());
        }
    }

}
