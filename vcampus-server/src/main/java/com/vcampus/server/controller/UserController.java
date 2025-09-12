package com.vcampus.server.controller;

import com.vcampus.common.dto.ChangePassword;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.dto.UserSearch;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.UserService;


/**
 * 用户控制器
 * 处理用户相关的请求，包括登录、忘记密码、重置密码等
 * 编写人：谌宣羽
 */
public class UserController {
    
    private final UserService userService;
    
    public UserController()  {
        this.userService = new UserService();
    }
    
    /**
     * 处理登录请求
     * @param message 登录请求消息
     * @return 登录响应消息
     */
    public Message handleLogin(Message message) {
        try {
            // 获取用户信息
            User loginUser = (User) message.getData();
            
            // 调用用户服务进行登录验证
            Message result = userService.validateLogin(loginUser);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.LOGIN, result.getData(), result.getMessage());
            } else {
                return Message.failure(ActionType.LOGIN, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理登录请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
    
    /**
     * 处理忘记密码请求
     * @param message 忘记密码请求消息
     * @return 忘记密码响应消息
     */
    public Message handleForgetPassword(Message message) {
        try {
            // 获取用户信息
            User user = (User) message.getData();
            
            // 调用用户服务处理忘记密码申请
            Message result = userService.handleForgetPassword(user);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.FORGET_PASSWORD, result.getMessage());
            } else {
                return Message.failure(ActionType.FORGET_PASSWORD, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理忘记密码请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.FORGET_PASSWORD, "服务器内部错误");
        }
    }

    /**
     * 处理修改密码请求
     * @param message
     * @return
     */
    public Message handleChangePassword(Message message) {
        try {
            // 获取用户信息
            ChangePassword changePassword = (ChangePassword) message.getData();
            // 调用用户服务处理修改密码申请
            Message result = userService.handleChangePassword(changePassword);
            if (result.isSuccess()) {
                return Message.success(ActionType.CHANGE_PASSWORD, result.getMessage());
            } else {
                return Message.failure(ActionType.CHANGE_PASSWORD, result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("处理修改密码请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.CHANGE_PASSWORD, "服务器内部错误");
        }
    }

    /** 
     * 处理搜索用户请求
     * @param message 搜索用户请求消息
     * @return 搜索用户响应消息
     */
    public Message handleSearchUsers(Message message) {
        try {
            // 获取搜索关键词
            UserSearch userSearch = (UserSearch) message.getData();
            
            // 调用用户服务进行搜索
            Message result = userService.searchUsers(userSearch);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.SEARCH_USERS, result.getData(), result.getMessage());
            } else {
                return Message.failure(ActionType.SEARCH_USERS, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理搜索用户请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.SEARCH_USERS, "服务器内部错误");
        }
    }

    /**
     * 处理删除用户请求
     * @param message 删除用户请求消息
     * @return 删除用户响应消息
     */
    public Message handleDeleteUser(Message message) {
        try {
            // 获取用户ID
            String userId = (String) message.getData();
            
            // 调用用户服务进行删除
            Message result = userService.deleteUser(userId);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.DELETE_USER, result.getMessage());
            } else {
                return Message.failure(ActionType.DELETE_USER, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理删除用户请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.DELETE_USER, "服务器内部错误");
        }
    }

    /**
     * 处理重置用户密码请求
     * @param message 重置用户密码请求消息
     * @return 重置用户密码响应消息
     */
    public Message handleResetUserPassword(Message message) {
        try {
            User user = (User) message.getData();   
            
            // 调用用户服务进行密码重置
            Message result = userService.resetUserPassword(user);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.RESET_USER_PASSWORD, result.getMessage());
            } else {
                return Message.failure(ActionType.RESET_USER_PASSWORD, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理重置用户密码请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.RESET_USER_PASSWORD, "服务器内部错误");
        }
    }

    /**
     * 处理创建用户请求
     * @param message 创建用户请求消息
     * @return 创建用户响应消息
     */
    public Message handleCreateUser(Message message) {
        try {
            // 获取用户信息
            User user = (User) message.getData();
            
            // 调用用户服务进行创建
            Message result = userService.createUser(user);
            
            if (result.isSuccess()) {
                return Message.success(ActionType.CREATE_USER, result.getMessage());
            } else {
                return Message.failure(ActionType.CREATE_USER, result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("处理创建用户请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.CREATE_USER, "服务器内部错误");
        }
    }
}
