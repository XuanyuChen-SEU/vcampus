package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件管理员服务类
 * 简洁版本：只提供查看所有邮件、搜索和统计功能
 */
public class EmailAdminService {
    
    /**
     * 获取所有邮件
     * @param offset 偏移量
     * @param limit 限制数量
     */
    public void getAllEmails(int offset, int limit) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("page", (offset / limit) + 1);  // 转换为页码
            data.put("pageSize", limit);
            
            Message request = new Message(ActionType.EMAIL_ADMIN_GET_ALL, data);
            MainApp.getGlobalSocketClient().sendMessage(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 搜索邮件
     * @param keyword 搜索关键词（邮件ID或主题）
     * @param offset 偏移量
     * @param limit 限制数量
     */
    public void searchEmails(String keyword, int offset, int limit) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("keyword", keyword);
            data.put("page", (offset / limit) + 1);  // 转换为页码
            data.put("pageSize", limit);
            
            Message request = new Message(ActionType.EMAIL_ADMIN_SEARCH_ALL, data);
            MainApp.getGlobalSocketClient().sendMessage(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 管理员删除邮件
     * @param emailId 邮件ID
     */
    public void adminDeleteEmail(String emailId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("emailId", emailId);
            
            Message request = new Message(ActionType.EMAIL_ADMIN_DELETE, data);
            MainApp.getGlobalSocketClient().sendMessage(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 按用户ID搜索邮件
     * @param userId 用户ID（发送者或收信者）
     * @param offset 偏移量
     * @param limit 限制数量
     */
    public void searchEmailsByUser(String userId, int offset, int limit) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("page", (offset / limit) + 1);  // 转换为页码
            data.put("pageSize", limit);
            
            Message request = new Message(ActionType.EMAIL_ADMIN_SEARCH_BY_USER, data);
            MainApp.getGlobalSocketClient().sendMessage(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
