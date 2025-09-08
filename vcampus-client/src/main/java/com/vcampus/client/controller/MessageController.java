package com.vcampus.client.controller;

import com.vcampus.client.controller.ChangePasswordController;
import com.vcampus.common.dto.Message;

/**
 * 客户端消息控制器
 * 作为总控制器，根据消息类型实例化并调用各个子控制器
 * 编写人：谌宣羽
 */
public class MessageController {
    
    private LoginController loginController;
    private StudentController studentController;

    private ChangePasswordController changePasswordController;
    /**
     * 设置LoginController实例（由UI层调用）
     * @param controller LoginController实例
     */
    public void setLoginController(LoginController controller) {
        this.loginController = controller;
    }
    public void setChangePasswordController(ChangePasswordController controller) {
        this.changePasswordController = controller;
    }
    public void setStudentController(StudentController controller){
        this.studentController=controller;
    }

    /**
     * 处理服务端消息
     * @param message 服务端发送的消息
     */
    public void handleMessage(Message message) {
        try {
            // 验证消息格式
            if (message == null || message.getAction() == null) {
                System.err.println("接收到无效的消息格式");
                return;
            }
            
            // 根据ActionType调用对应的子控制器
            switch (message.getAction()) {//注意这边  md里都提到了
                case LOGIN:
                    if (loginController != null) {
                        loginController.handleLoginResponse(message);
                    } else {
                        System.err.println("LoginController未设置，无法处理登录响应");
                    }
                    break;
                case FORGET_PASSWORD:
                    if (loginController != null) {
                        loginController.handleForgetPasswordResponse(message);
                    } else {
                        System.err.println("LoginController未设置，无法处理密码重置响应");
                    }
                    break;
                case CHANGE_PASSWORD:
                    if (changePasswordController != null) {
                        changePasswordController.handleChangePasswordResponse(message);
                    } else {
                        System.err.println("ChangePasswordController未设置，无法处理修改密码响应");
                    }
                    break;
                case INFO_STUDENT:
                    if (studentController != null) {
                    studentController.handleStudentInfoResponse(message);
                } else {
                    System.err.println("StudentController未设置，无法处理学生信息获取响应");
                }
                break;
                default:
                    System.out.println("未处理的消息类型: " + message.getAction());
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
        }
    }
}
