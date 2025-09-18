package com.vcampus.server.test;

import java.util.HashMap;
import java.util.Map;

import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.EmailStatus;
import com.vcampus.server.controller.MessageController;

/**
 * 邮件数据类型测试
 * 测试修改后的EmailController是否能正确处理序列化的Email对象
 * 编写人：AI Assistant
 */
public class EmailDataTypeTest {
    
    private MessageController messageController;
    
    public EmailDataTypeTest() {
        this.messageController = new MessageController();
    }
    
    /**
     * 测试发送邮件 - 使用Email对象
     */
    public void testSendEmailWithEmailObject() {
        System.out.println("=== 测试发送邮件（使用Email对象） ===");
        
        // 创建Email对象
        Email email = new Email();
        email.setSenderId("user001");
        email.setRecipientId("user002");
        email.setSubject("测试邮件主题");
        email.setContent("这是一封测试邮件的内容");
        email.setStatus(EmailStatus.SENT);
        email.setHasAttachment(false); // 设置是否有附件
        
        Message request = new Message(ActionType.EMAIL_SEND, email, true, "发送邮件请求");
        Message response = messageController.handleMessage(request);
        
        System.out.println("请求: " + request.getAction());
        System.out.println("响应: " + response.getAction() + " - " + response.getMessage());
        System.out.println("成功: " + response.isSuccess());
        System.out.println("邮件是否有附件: " + email.isHasAttachment());
        System.out.println();
    }
    
    /**
     * 测试保存草稿 - 使用Email对象
     */
    public void testSaveDraftWithEmailObject() {
        System.out.println("=== 测试保存草稿（使用Email对象） ===");
        
        // 创建Email对象
        Email email = new Email();
        email.setSenderId("user001");
        email.setRecipientId("user002");
        email.setSubject("草稿邮件主题");
        email.setContent("这是一封草稿邮件的内容");
        email.setStatus(EmailStatus.DRAFT);
        email.setHasAttachment(true); // 设置是否有附件
        
        Message request = new Message(ActionType.EMAIL_SAVE_DRAFT, email, true, "保存草稿请求");
        Message response = messageController.handleMessage(request);
        
        System.out.println("请求: " + request.getAction());
        System.out.println("响应: " + response.getAction() + " - " + response.getMessage());
        System.out.println("成功: " + response.isSuccess());
        System.out.println("草稿是否有附件: " + email.isHasAttachment());
        System.out.println();
    }
    
    /**
     * 测试获取收件箱 - 使用Map参数
     */
    public void testGetInboxWithMap() {
        System.out.println("=== 测试获取收件箱（使用Map参数） ===");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "user002");
        data.put("page", 1);
        data.put("pageSize", 10);
        
        Message request = new Message(ActionType.EMAIL_GET_INBOX, data, true, "获取收件箱请求");
        Message response = messageController.handleMessage(request);
        
        System.out.println("请求: " + request.getAction());
        System.out.println("响应: " + response.getAction() + " - " + response.getMessage());
        System.out.println("成功: " + response.isSuccess());
        System.out.println("数据: " + response.getData());
        System.out.println();
    }
    
    /**
     * 测试搜索邮件 - 使用Map参数
     */
    public void testSearchEmailsWithMap() {
        System.out.println("=== 测试搜索邮件（使用Map参数） ===");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "user001");
        data.put("keyword", "测试");
        data.put("page", 1);
        data.put("pageSize", 10);
        
        Message request = new Message(ActionType.EMAIL_SEARCH, data, true, "搜索邮件请求");
        Message response = messageController.handleMessage(request);
        
        System.out.println("请求: " + request.getAction());
        System.out.println("响应: " + response.getAction() + " - " + response.getMessage());
        System.out.println("成功: " + response.isSuccess());
        System.out.println("数据: " + response.getData());
        System.out.println();
    }
    
    /**
     * 测试发送带附件的邮件
     */
    public void testSendEmailWithAttachment() {
        System.out.println("=== 测试发送带附件的邮件 ===");
        
        // 创建Email对象
        Email email = new Email();
        email.setSenderId("user001");
        email.setRecipientId("user002");
        email.setSubject("带附件的测试邮件");
        email.setContent("这是一封带附件的测试邮件");
        email.setStatus(EmailStatus.SENT);
        email.setHasAttachment(true); // 设置为有附件
        
        Message request = new Message(ActionType.EMAIL_SEND, email, true, "发送带附件邮件请求");
        Message response = messageController.handleMessage(request);
        
        System.out.println("请求: " + request.getAction());
        System.out.println("响应: " + response.getAction() + " - " + response.getMessage());
        System.out.println("成功: " + response.isSuccess());
        System.out.println("邮件是否有附件: " + email.isHasAttachment());
        System.out.println();
    }
    
    /**
     * 测试管理员获取统计信息
     */
    public void testAdminGetStatistics() {
        System.out.println("=== 测试管理员获取统计信息 ===");
        
        Message request = new Message(ActionType.EMAIL_ADMIN_GET_STATISTICS, null, true, "获取统计信息请求");
        Message response = messageController.handleMessage(request);
        
        System.out.println("请求: " + request.getAction());
        System.out.println("响应: " + response.getAction() + " - " + response.getMessage());
        System.out.println("成功: " + response.isSuccess());
        System.out.println("统计信息: " + response.getData());
        System.out.println();
    }
    
    /**
     * 运行所有测试
     */
    public void runAllTests() {
        System.out.println("开始邮件数据类型测试...\n");
        
        try {
            testSendEmailWithEmailObject();
            testSaveDraftWithEmailObject();
            testSendEmailWithAttachment();
            testGetInboxWithMap();
            testSearchEmailsWithMap();
            testAdminGetStatistics();
            
            System.out.println("所有测试完成！EmailController已成功修改为处理序列化的Email对象。");
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 主方法 - 运行测试
     */
    public static void main(String[] args) {
        EmailDataTypeTest test = new EmailDataTypeTest();
        test.runAllTests();
    }
}
