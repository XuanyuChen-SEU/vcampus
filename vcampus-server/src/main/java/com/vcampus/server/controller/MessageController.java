package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 消息控制器
 * 负责消息路由和参数验证
 * (已合并 Course 和 Shop 模块)
 */
public class MessageController {

    // --- 1. 合并字段声明 ---
    // 我们需要保留所有的控制器实例
    private final UserController userController;
    private final StudentController studentController;
    private final CourseController courseController; // 来自远程的修改
    private final ShopController shopController;     // 来自您的修改

    // --- 2. 合并构造函数 ---
    // 在构造函数中，我们需要实例化所有的控制器
    public MessageController() {
        this.userController = new UserController();
        this.studentController = new StudentController();
        this.courseController = new CourseController(); // 保留
        this.shopController = new ShopController();     // 保留
    }

    /**
     * 处理客户端消息
     * @param request 客户端请求消息
     * @return 服务端响应消息
     */
    public Message handleMessage(Message request) {
        try {
            // 验证消息格式
            if (request == null || request.getAction() == null) {
                return Message.failure(ActionType.LOGIN, "无效的消息格式");
            }

            // --- 3. 合并 switch 路由逻辑 ---
            // 将所有 case 都放到同一个 switch 语句中
            switch (request.getAction()) {
                // --- 用户相关 (已有) ---
                case LOGIN:
                    return userController.handleLogin(request);
                case FORGET_PASSWORD:
                    return userController.handleForgetPassword(request);
                case CHANGE_PASSWORD:
                    return userController.handleChangePassword(request);

                // --- 课程相关 ---
                case GET_ALL_COURSES:
                    return courseController.handleGetAllCourses(request);
                case SELECT_COURSE:
                    return courseController.handleSelectCourse(request);
                case DROP_COURSE:
                    return courseController.handleDropCourse(request);

                // --- 商店相关 ---
                case SHOP_GET_ALL_PRODUCTS:
                    return shopController.handleGetAllProducts(request);
                case SHOP_SEARCH_PRODUCTS:
                    return shopController.handleSearchProducts(request);
                case SHOP_GET_MY_ORDERS:
                    return shopController.handleGetMyOrders(request);
                case SHOP_GET_MY_FAVORITES:
                    return shopController.handleGetMyFavorites(request);
                // 如果您还有 removeFavorite, 也应该加在这里
                // case SHOP_REMOVE_FAVORITE:
                //     return shopController.handleRemoveFavorite(request);

                // --- 默认处理 ---
                default:
                    return Message.failure(request.getAction(), "不支持的操作类型: " + request.getAction());
            }
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
            // 尝试返回带有 action 的错误，如果 request 本身是 null 则返回 null
            ActionType action = (request != null) ? request.getAction() : null;
            return Message.failure(action, "服务器内部错误");
        }
    }
}