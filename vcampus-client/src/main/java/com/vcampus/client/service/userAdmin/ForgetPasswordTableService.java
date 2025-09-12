package com.vcampus.client.service.userAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 忘记密码申请服务类
 * 负责处理忘记密码申请相关的客户端业务逻辑
 * 编写人：谌宣羽
 */
public class ForgetPasswordTableService {
    
    private final SocketClient socketClient;
    
    public ForgetPasswordTableService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }
    
    /**
     * 获取所有忘记密码申请
     * @return Message 包含申请列表的消息
     */
    public Message getAllApplications() {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.GET_FORGET_PASSWORD_TABLE, "网络连接未建立");
            }
            
            // 发送请求到服务器
            Message request = new Message(ActionType.GET_FORGET_PASSWORD_TABLE, null);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("获取忘记密码申请列表时发生异常: " + e.getMessage());
            return Message.failure(ActionType.GET_FORGET_PASSWORD_TABLE, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 批准忘记密码申请
     * @param userId 用户ID
     * @return Message 操作结果消息
     */
    public Message approveApplication(String userId) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.APPROVE_FORGET_PASSWORD_APPLICATION, "网络连接未建立");
            }
            
            // 发送请求到服务器
            Message request = new Message(ActionType.APPROVE_FORGET_PASSWORD_APPLICATION, userId);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("批准忘记密码申请时发生异常: " + e.getMessage());
            return Message.failure(ActionType.APPROVE_FORGET_PASSWORD_APPLICATION, "发送请求失败：" + e.getMessage());
        }
    }
    
    /**
     * 拒绝忘记密码申请
     * @param userId 用户ID
     * @return Message 操作结果消息
     */
    public Message rejectApplication(String userId) {
        try {
            // 检查连接状态
            if (!socketClient.isConnected()) {
                return Message.failure(ActionType.REJECT_FORGET_PASSWORD_APPLICATION, "网络连接未建立");
            }
            
            // 发送请求到服务器
            Message request = new Message(ActionType.REJECT_FORGET_PASSWORD_APPLICATION, userId);
            Message response = socketClient.sendMessage(request);
            return response;
            
        } catch (Exception e) {
            System.err.println("拒绝忘记密码申请时发生异常: " + e.getMessage());
            return Message.failure(ActionType.REJECT_FORGET_PASSWORD_APPLICATION, "发送请求失败：" + e.getMessage());
        }
    }
}
