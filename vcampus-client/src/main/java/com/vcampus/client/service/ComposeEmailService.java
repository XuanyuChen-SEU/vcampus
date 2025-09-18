package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

/**
 * 写邮件服务类
 * 负责向服务端请求邮件发送和草稿保存，并向 Controller 提供接口
 * 编写人：AI Assistant
 */
public class ComposeEmailService {

    private final SocketClient socketClient;

    public ComposeEmailService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }

    /**
     * 获取全局的 Socket 客户端，用于注册 Controller
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    // ==================== 邮件发送和保存 ====================

    /**
     * 发送邮件
     * @param email 邮件对象
     */
    public void sendEmail(Email email) {
        Message request = new Message(ActionType.EMAIL_SEND, email);
        socketClient.sendMessage(request);
    }

    /**
     * 保存草稿
     * @param email 邮件对象
     */
    public void saveDraft(Email email) {
        Message request = new Message(ActionType.EMAIL_SAVE_DRAFT, email);
        socketClient.sendMessage(request);
    }

    // ==================== 响应处理方法 ====================

    /**
     * 处理服务端返回的发送结果
     * @param message 服务端返回消息
     * @return 是否成功
     */
    public boolean handleSendResponse(Message message) {
        return message.isSuccess();
    }

    /**
     * 处理服务端返回的保存草稿结果
     * @param message 服务端返回消息
     * @return 是否成功
     */
    public boolean handleSaveDraftResponse(Message message) {
        return message.isSuccess();
    }

    /**
     * 获取错误信息
     * @param message 服务端返回消息
     * @return 错误信息，成功时返回null
     */
    public String getErrorMessage(Message message) {
        if (message.isSuccess()) {
            return null;
        }
        return message.getMessage();
    }
}
