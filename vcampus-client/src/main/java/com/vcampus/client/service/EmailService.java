package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件服务类
 * 负责向服务端请求邮件数据，并向 Controller 提供接口
 * 编写人：谌宣羽
 */
public class EmailService {

    private final SocketClient socketClient;

    public EmailService() {
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

    // ==================== 邮件获取 ====================

    /**
     * 获取收件箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     */
    public void getInboxEmails(String userId, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        Message request = new Message(ActionType.EMAIL_GET_INBOX, data);
        socketClient.sendMessage(request);
    }

    /**
     * 获取发件箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     */
    public void getSentEmails(String userId, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        Message request = new Message(ActionType.EMAIL_GET_SENT, data);
        socketClient.sendMessage(request);
    }

    /**
     * 获取草稿箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     */
    public void getDraftEmails(String userId, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        Message request = new Message(ActionType.EMAIL_GET_DRAFT, data);
        socketClient.sendMessage(request);
    }

    /**
     * 读取邮件详情
     * @param emailId 邮件ID
     * @param userId 用户ID
     */
    public void readEmail(String emailId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailId", emailId);
        data.put("userId", userId);
        
        Message request = new Message(ActionType.EMAIL_READ, data);
        socketClient.sendMessage(request);
    }

    // ==================== 邮件搜索 ====================

    /**
     * 搜索邮件
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     */
    public void searchEmails(String userId, String keyword, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("keyword", keyword);
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        Message request = new Message(ActionType.EMAIL_SEARCH, data);
        socketClient.sendMessage(request);
    }

    // ==================== 邮件状态管理 ====================

    /**
     * 标记邮件为已读
     * @param emailId 邮件ID
     * @param userId 用户ID
     */
    public void markAsRead(String emailId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailId", emailId);
        data.put("userId", userId);
        
        Message request = new Message(ActionType.EMAIL_MARK_READ, data);
        socketClient.sendMessage(request);
    }

    /**
     * 标记邮件为未读
     * @param emailId 邮件ID
     * @param userId 用户ID
     */
    public void markAsUnread(String emailId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailId", emailId);
        data.put("userId", userId);
        
        Message request = new Message(ActionType.EMAIL_MARK_UNREAD, data);
        socketClient.sendMessage(request);
    }

    /**
     * 删除邮件
     * @param emailId 邮件ID
     * @param userId 用户ID
     */
    public void deleteEmail(String emailId, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailId", emailId);
        data.put("userId", userId);
        
        Message request = new Message(ActionType.EMAIL_DELETE, data);
        socketClient.sendMessage(request);
    }

    // ==================== 统计信息 ====================

    // ==================== 批量操作 ====================

    /**
     * 批量标记为已读
     * @param emailIds 邮件ID列表
     * @param userId 用户ID
     */
    public void batchMarkAsRead(String[] emailIds, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailIds", emailIds);
        data.put("userId", userId);
        
        Message request = new Message(ActionType.EMAIL_BATCH_MARK_READ, data);
        socketClient.sendMessage(request);
    }

    /**
     * 批量删除邮件
     * @param emailIds 邮件ID列表
     * @param userId 用户ID
     */
    public void batchDeleteEmails(String[] emailIds, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailIds", emailIds);
        data.put("userId", userId);
        
        Message request = new Message(ActionType.EMAIL_BATCH_DELETE, data);
        socketClient.sendMessage(request);
    }

    // ==================== 管理员功能 ====================

    /**
     * 管理员获取所有邮件
     * @param page 页码
     * @param pageSize 每页大小
     */
    public void adminGetAllEmails(int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        Message request = new Message(ActionType.EMAIL_ADMIN_GET_ALL, data);
        socketClient.sendMessage(request);
    }

    /**
     * 管理员获取指定用户的邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     */
    public void adminGetUserEmails(String userId, int page, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        Message request = new Message(ActionType.EMAIL_ADMIN_GET_USER_EMAILS, data);
        socketClient.sendMessage(request);
    }

    /**
     * 管理员删除邮件
     * @param emailId 邮件ID
     */
    public void adminDeleteEmail(String emailId) {
        Map<String, Object> data = new HashMap<>();
        data.put("emailId", emailId);
        
        Message request = new Message(ActionType.EMAIL_ADMIN_DELETE, data);
        socketClient.sendMessage(request);
    }

    /**
     * 管理员获取邮件统计信息
     */
    public void adminGetStatistics() {
        Message request = new Message(ActionType.EMAIL_ADMIN_GET_STATISTICS, null);
        socketClient.sendMessage(request);
    }

    // ==================== 响应处理方法 ====================

    /**
     * 处理服务端返回的邮件列表
     * @param message 服务端返回消息
     * @return List<Email>，失败返回 null
     */
    @SuppressWarnings("unchecked")
    public java.util.List<Email> handleEmailListResponse(Message message) {
        if (message.isSuccess() && message.getData() != null) {
            return (java.util.List<Email>) message.getData();
        }
        return null;
    }

    /**
     * 处理服务端返回的单个邮件
     * @param message 服务端返回消息
     * @return Email 对象，失败返回 null
     */
    public Email handleEmailResponse(Message message) {
        if (message.isSuccess() && message.getData() != null) {
            return (Email) message.getData();
        }
        return null;
    }
}
