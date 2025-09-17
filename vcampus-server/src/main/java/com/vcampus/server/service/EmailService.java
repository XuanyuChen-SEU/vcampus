package com.vcampus.server.service;

import java.time.LocalDateTime;
import java.util.List;

import com.vcampus.common.dto.Email;
import com.vcampus.common.enums.EmailStatus;
import com.vcampus.server.dao.impl.EmailDao;

/**
 * 邮件服务类
 * 提供邮件相关的业务逻辑处理
 * 编写人：谌宣羽
 */
public class EmailService {

    private EmailDao emailDao;

    public EmailService() {
        this.emailDao = new EmailDao();
    }

    // ==================== 用户邮件操作 ====================

    /**
     * 发送邮件
     * @param senderId 发送者ID
     * @param recipientId 接收者ID
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    public boolean sendEmail(String senderId, String recipientId, String subject, String content) {
        try {
            // 创建邮件对象
            Email email = new Email();
            email.setEmailId(generateEmailId());
            email.setSenderId(senderId);
            email.setRecipientId(recipientId);
            email.setSubject(subject);
            email.setContent(content);
            email.setSendTime(LocalDateTime.now());
            email.setStatus(EmailStatus.SENT);
            email.setHasAttachment(false); // 暂时不支持附件

            // 保存邮件
            return emailDao.createEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存草稿
     * @param senderId 发送者ID
     * @param recipientId 接收者ID
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否保存成功
     */
    public boolean saveDraft(String senderId, String recipientId, String subject, String content) {
        try {
            Email email = new Email();
            email.setEmailId(generateEmailId());
            email.setSenderId(senderId);
            email.setRecipientId(recipientId);
            email.setSubject(subject);
            email.setContent(content);
            email.setSendTime(LocalDateTime.now());
            email.setStatus(EmailStatus.DRAFT);
            email.setHasAttachment(false);

            return emailDao.createEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取用户收件箱
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 收件箱邮件列表
     */
    public List<Email> getInbox(String userId, int page, int pageSize) {
        return emailDao.getInboxEmails(userId, page, pageSize);
    }

    /**
     * 获取用户发件箱
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 发件箱邮件列表
     */
    public List<Email> getSentBox(String userId, int page, int pageSize) {
        return emailDao.getSentEmails(userId, page, pageSize);
    }

    /**
     * 获取用户草稿箱
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 草稿箱邮件列表
     */
    public List<Email> getDraftBox(String userId, int page, int pageSize) {
        return emailDao.getDraftEmails(userId, page, pageSize);
    }

    /**
     * 阅读邮件（自动标记为已读）
     * @param emailId 邮件ID
     * @param userId 用户ID
     * @return 邮件对象
     */
    public Email readEmail(String emailId, String userId) {
        Email email = emailDao.getEmailById(emailId);
        if (email != null && email.getRecipientId().equals(userId)) {
            // 如果是收件人阅读邮件，标记为已读
            if (email.getStatus() == EmailStatus.SENT) {
                emailDao.markAsRead(emailId);
                email.setStatus(EmailStatus.READ);
            }
        }
        return email;
    }

    /**
     * 删除邮件
     * @param emailId 邮件ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public boolean deleteEmail(String emailId, String userId) {
        Email email = emailDao.getEmailById(emailId);
        if (email != null && 
            (email.getSenderId().equals(userId) || email.getRecipientId().equals(userId))) {
            return emailDao.deleteEmail(emailId);
        }
        return false;
    }

    /**
     * 搜索邮件
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    public List<Email> searchEmails(String userId, String keyword, int page, int pageSize) {
        return emailDao.searchEmails(userId, keyword, page, pageSize);
    }

    /**
     * 获取未读邮件数量
     * @param userId 用户ID
     * @return 未读邮件数量
     */
    public int getUnreadCount(String userId) {
        return emailDao.getUnreadEmailCount(userId);
    }

    /**
     * 标记邮件为已读
     * @param emailId 邮件ID
     * @param userId 用户ID
     * @return 是否标记成功
     */
    public boolean markAsRead(String emailId, String userId) {
        Email email = emailDao.getEmailById(emailId);
        if (email != null && email.getRecipientId().equals(userId)) {
            return emailDao.markAsRead(emailId);
        }
        return false;
    }

    /**
     * 标记邮件为未读
     * @param emailId 邮件ID
     * @param userId 用户ID
     * @return 是否标记成功
     */
    public boolean markAsUnread(String emailId, String userId) {
        Email email = emailDao.getEmailById(emailId);
        if (email != null && email.getRecipientId().equals(userId)) {
            return emailDao.markAsUnread(emailId);
        }
        return false;
    }

    /**
     * 批量标记为已读
     * @param emailIds 邮件ID列表
     * @param userId 用户ID
     * @return 成功标记的数量
     */
    public int batchMarkAsRead(List<String> emailIds, String userId) {
        int successCount = 0;
        for (String emailId : emailIds) {
            if (markAsRead(emailId, userId)) {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * 批量删除邮件
     * @param emailIds 邮件ID列表
     * @param userId 用户ID
     * @return 成功删除的数量
     */
    public int batchDeleteEmails(List<String> emailIds, String userId) {
        int successCount = 0;
        for (String emailId : emailIds) {
            if (deleteEmail(emailId, userId)) {
                successCount++;
            }
        }
        return successCount;
    }

    // ==================== 管理员功能 ====================

    /**
     * 获取所有邮件（管理员权限）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 所有邮件列表
     */
    public List<Email> getAllEmails(int page, int pageSize) {
        return emailDao.getAllEmails(page, pageSize);
    }

    /**
     * 获取用户的所有邮件（管理员权限）
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户邮件列表
     */
    public List<Email> getUserAllEmails(String userId, int page, int pageSize) {
        return emailDao.getUserAllEmails(userId, page, pageSize);
    }

    /**
     * 搜索所有邮件（管理员权限）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    public List<Email> searchAllEmails(String keyword, int page, int pageSize) {
        return emailDao.searchAllEmails(keyword, page, pageSize);
    }

    /**
     * 管理员删除邮件
     * @param emailId 邮件ID
     * @return 是否删除成功
     */
    public boolean adminDeleteEmail(String emailId) {
        return emailDao.deleteEmail(emailId);
    }

    /**
     * 获取邮件统计信息（管理员权限）
     * @return 统计信息字符串
     */
    public String getEmailStatistics() {
        try {
            // 获取总邮件数
            List<Email> allEmails = emailDao.getAllEmails(1, Integer.MAX_VALUE);
            int totalEmails = allEmails != null ? allEmails.size() : 0;
            
            // 统计各状态邮件数量
            int draftCount = 0;
            int sentCount = 0;
            int readCount = 0;
            
            if (allEmails != null) {
                for (Email email : allEmails) {
                    switch (email.getStatus()) {
                        case DRAFT:
                            draftCount++;
                            break;
                        case SENT:
                            sentCount++;
                            break;
                        case READ:
                            readCount++;
                            break;
                    }
                }
            }
            
            return String.format("总邮件数: %d, 草稿: %d, 已发送: %d, 已读: %d", 
                               totalEmails, draftCount, sentCount, readCount);
        } catch (Exception e) {
            e.printStackTrace();
            return "统计信息获取失败";
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成邮件ID
     * @return 邮件ID
     */
    private String generateEmailId() {
        return "email_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}
