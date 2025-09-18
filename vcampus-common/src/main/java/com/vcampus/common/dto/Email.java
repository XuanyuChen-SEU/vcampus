package com.vcampus.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.vcampus.common.enums.EmailStatus;

/**
 * 邮件数据传输对象（简化版）
 * 用于邮件系统的基本邮件信息传递
 * 编写人：AI Assistant
 */
public class Email implements Serializable {
    private static final long serialVersionUID = 1L;

    private String emailId;              // 邮件ID（主键）
    private String senderId;             // 发送者用户ID
    private String recipientId;          // 接收者用户ID
    private String subject;               // 邮件主题
    private String content;              // 邮件内容
    private LocalDateTime sendTime;       // 发送时间
    private EmailStatus status;           // 邮件状态（DRAFT, SENT, READ）
    private boolean hasAttachment;       // 是否有附件

    // 默认构造方法（反序列化必需）
    public Email() {}

    // 完整构造方法
    public Email(String emailId, String senderId, String recipientId, 
                 String subject, String content, EmailStatus status) {
        this.emailId = emailId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.sendTime = LocalDateTime.now();
        this.hasAttachment = false;
    }

    // Getter & Setter
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    /**
     * 判断邮件是否已读
     * @return true表示已读，false表示未读
     */
    public boolean isRead() {
        return status == EmailStatus.READ;
    }

    /**
     * 设置邮件已读状态
     * @param read true表示已读，false表示未读
     */
    public void setRead(boolean read) {
        this.status = read ? EmailStatus.READ : EmailStatus.SENT;
    }

    @Override
    public String toString() {
        return "Email{" +
                "emailId='" + emailId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", subject='" + subject + '\'' +
                ", sendTime=" + sendTime +
                ", status=" + status +
                '}';
    }
}
