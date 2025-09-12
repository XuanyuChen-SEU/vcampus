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
            // 根据ActionType调用对应的控制器
            switch (request.getAction()) {//需要什么服务  自己加上）
                // --- 用户登录相关 ---
                case LOGIN:
                    return userController.handleLogin(request);
                case FORGET_PASSWORD:
                    return userController.handleForgetPassword(request);
                case CHANGE_PASSWORD:
                    return userController.handleChangePassword(request);

                // --- 学籍相关 ---
                case INFO_STUDENT:
                    return studentController.handle(request);
                case UPDATE_STUDENT:
                    return studentController.updateStudent(request);

                // --- 用户管理员相关 ---
                case SEARCH_USERS:
                    return userController.handleSearchUsers(request);
                case DELETE_USER:
                    return userController.handleDeleteUser(request);
                case RESET_USER_PASSWORD:
                    return userController.handleResetUserPassword(request);
                case CREATE_USER:
                    return userController.handleCreateUser(request);
                case GET_FORGET_PASSWORD_TABLE:
                    return userController.handleGetForgetPasswordTable(request);
                case APPROVE_FORGET_PASSWORD_APPLICATION:
                    return userController.handleApproveForgetPasswordApplication(request);
                case REJECT_FORGET_PASSWORD_APPLICATION:
                    return userController.handleRejectForgetPasswordApplication(request);

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