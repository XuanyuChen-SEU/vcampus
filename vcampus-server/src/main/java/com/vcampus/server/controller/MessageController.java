package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 消息控制器
 * 负责消息路由和参数验证
 * 编写人：谌宣羽
 */
public class MessageController {
    
    private final UserController userController;
    private final StudentController studentController;
    
    public MessageController() {
        this.userController = new UserController();
        this.studentController=new StudentController();//自上而下  连续的好几个构造方法    client不用是因为  （小问题）
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
                case LOGIN:
                    return userController.handleLogin(request);
                case FORGET_PASSWORD:
                    return userController.handleForgetPassword(request);
                case INFO_STUDENT:
                    return studentController.handle(request);
                case CHANGE_PASSWORD:
                    return userController.handleChangePassword(request);
                case UPDATE_STUDENT:
                    return studentController.updateStudent(request);
                default:
                    return Message.failure(request.getAction(), "不支持的操作类型: " + request.getAction());
            }
            
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
            return Message.failure(request.getAction(), "服务器内部错误");
        }
    }
}
