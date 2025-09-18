package com.vcampus.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.EmailService;

/**
 * 邮件控制器
 * 处理邮件相关的网络请求
 * 编写人：谌宣羽
 */
public class EmailController {

    private final EmailService emailService;

    public EmailController() {
        this.emailService = new EmailService();
    }

    /**
     * 处理邮件相关请求
     * @param message 请求消息
     * @return 响应消息
     */
    public Message handleRequest(Message message) {
        ActionType actionType = message.getAction();
        
        try {
            switch (actionType) {
                // ==================== 用户邮件操作 ====================
                case EMAIL_SEND:
                    return handleSendEmail(message);
                case EMAIL_SAVE_DRAFT:
                    return handleSaveDraft(message);
                case EMAIL_GET_INBOX:
                    return handleGetInbox(message);
                case EMAIL_GET_SENT:
                    return handleGetSent(message);
                case EMAIL_GET_DRAFT:
                    return handleGetDraft(message);
                case EMAIL_READ:
                    return handleReadEmail(message);
                case EMAIL_DELETE:
                    return handleDeleteEmail(message);
                case EMAIL_MARK_READ:
                    return handleMarkAsRead(message);
                case EMAIL_MARK_UNREAD:
                    return handleMarkAsUnread(message);
                case EMAIL_SEARCH:
                    return handleSearchEmails(message);
                case EMAIL_BATCH_MARK_READ:
                    return handleBatchMarkAsRead(message);
                case EMAIL_BATCH_DELETE:
                    return handleBatchDelete(message);
                
                // ==================== 管理员操作 ====================
                case EMAIL_ADMIN_GET_ALL:
                    return handleAdminGetAllEmails(message);
                case EMAIL_ADMIN_SEARCH_ALL:
                    return handleAdminSearchAllEmails(message);
                case EMAIL_ADMIN_SEARCH_BY_USER:
                    return handleAdminSearchByUser(message);
                case EMAIL_ADMIN_GET_USER_EMAILS:
                    return handleAdminGetUserEmails(message);
                case EMAIL_ADMIN_DELETE:
                    return handleAdminDeleteEmail(message);
                case EMAIL_ADMIN_GET_STATISTICS:
                    return handleAdminGetStatistics(message);
                
                default:
                    return Message.failure(ActionType.ERROR, "不支持的操作类型: " + actionType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.ERROR, "服务器内部错误: " + e.getMessage());
        }
    }

    // ==================== 用户邮件操作处理 ====================

    /**
     * 处理发送邮件请求
     */
    private Message handleSendEmail(Message message) {
        try {
            Email email = (Email) message.getData();
            
            if (email == null || email.getSenderId() == null || email.getRecipientId() == null || 
                email.getSubject() == null || email.getContent() == null) {
                return Message.failure(ActionType.EMAIL_SEND, "邮件数据不完整");
            }
            
            boolean result = emailService.sendEmail(email.getSenderId(), email.getRecipientId(), 
                email.getSubject(), email.getContent());
            return result ? 
                Message.success(ActionType.EMAIL_SEND, "邮件发送成功") : 
                Message.failure(ActionType.EMAIL_SEND, "邮件发送失败");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_SEND, "发送邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理保存草稿请求
     */
    private Message handleSaveDraft(Message message) {
        try {
            Email email = (Email) message.getData();
            
            if (email == null || email.getSenderId() == null) {
                return Message.failure(ActionType.EMAIL_SAVE_DRAFT, "草稿数据不完整");
            }
            
            boolean result = emailService.saveDraft(email.getSenderId(), email.getRecipientId(), 
                email.getSubject(), email.getContent());
            return result ? 
                Message.success(ActionType.EMAIL_SAVE_DRAFT, "草稿保存成功") : 
                Message.failure(ActionType.EMAIL_SAVE_DRAFT, "草稿保存失败");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_SAVE_DRAFT, "保存草稿失败: " + e.getMessage());
        }
    }

    /**
     * 处理获取收件箱请求
     */
    private Message handleGetInbox(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String userId = (String) data.get("userId");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (userId == null) {
                return Message.failure(ActionType.EMAIL_GET_INBOX, "用户ID不能为空");
            }
            
            List<Email> emails = emailService.getInbox(userId, page, pageSize);
            int totalCount = emailService.getInboxCount(userId);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", totalPages);
            
            return new Message(ActionType.EMAIL_GET_INBOX, responseData, true, "获取收件箱成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_GET_INBOX, "获取收件箱失败: " + e.getMessage());
        }
    }

    /**
     * 处理获取发件箱请求
     */
    private Message handleGetSent(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String userId = (String) data.get("userId");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (userId == null) {
                return Message.failure(ActionType.EMAIL_GET_SENT, "用户ID不能为空");
            }
            
            List<Email> emails = emailService.getSentBox(userId, page, pageSize);
            int totalCount = emailService.getSentCount(userId);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", totalPages);
            
            return new Message(ActionType.EMAIL_GET_SENT, responseData, true, "获取发件箱成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_GET_SENT, "获取发件箱失败: " + e.getMessage());
        }
    }

    /**
     * 处理获取草稿箱请求
     */
    private Message handleGetDraft(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String userId = (String) data.get("userId");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (userId == null) {
                return Message.failure(ActionType.EMAIL_GET_DRAFT, "用户ID不能为空");
            }
            
            List<Email> emails = emailService.getDraftBox(userId, page, pageSize);
            int totalCount = emailService.getDraftCount(userId);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", totalPages);
            
            return new Message(ActionType.EMAIL_GET_DRAFT, responseData, true, "获取草稿箱成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_GET_DRAFT, "获取草稿箱失败: " + e.getMessage());
        }
    }

    /**
     * 处理阅读邮件请求
     */
    private Message handleReadEmail(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String emailId = (String) data.get("emailId");
            String userId = (String) data.get("userId");
            
            if (emailId == null || userId == null) {
                return Message.failure(ActionType.EMAIL_READ, "参数不完整");
            }
            
            Email email = emailService.readEmail(emailId, userId);
            return new Message(ActionType.EMAIL_READ, email != null ? email : "邮件不存在", email != null, "阅读邮件成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_READ, "阅读邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理删除邮件请求
     */
    private Message handleDeleteEmail(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String emailId = (String) data.get("emailId");
            String userId = (String) data.get("userId");
            
            if (emailId == null || userId == null) {
                return Message.failure(ActionType.EMAIL_DELETE, "参数不完整");
            }
            
            boolean result = emailService.deleteEmail(emailId, userId);
            return result ? 
                Message.success(ActionType.EMAIL_DELETE, "删除成功") : 
                Message.failure(ActionType.EMAIL_DELETE, "删除失败");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_DELETE, "删除邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理标记已读请求
     */
    private Message handleMarkAsRead(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String emailId = (String) data.get("emailId");
            String userId = (String) data.get("userId");
            
            if (emailId == null || userId == null) {
                return Message.failure(ActionType.EMAIL_MARK_READ, "参数不完整");
            }
            
            boolean result = emailService.markAsRead(emailId, userId);
            return result ? 
                Message.success(ActionType.EMAIL_MARK_READ, "标记成功") : 
                Message.failure(ActionType.EMAIL_MARK_READ, "标记失败");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_MARK_READ, "标记已读失败: " + e.getMessage());
        }
    }

    /**
     * 处理搜索邮件请求
     */
    private Message handleSearchEmails(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String userId = (String) data.get("userId");
            String keyword = (String) data.get("keyword");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (userId == null || keyword == null) {
                return Message.failure(ActionType.EMAIL_SEARCH, "参数不完整");
            }
            
            List<Email> emails = emailService.searchEmails(userId, keyword, page, pageSize);
            return new Message(ActionType.EMAIL_SEARCH, emails != null ? emails : List.of(), true, "搜索邮件成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_SEARCH, "搜索邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理标记未读请求
     */
    private Message handleMarkAsUnread(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String emailId = (String) data.get("emailId");
            String userId = (String) data.get("userId");
            
            if (emailId == null || userId == null) {
                return Message.failure(ActionType.EMAIL_MARK_UNREAD, "参数不完整");
            }
            
            boolean result = emailService.markAsUnread(emailId, userId);
            return result ? 
                Message.success(ActionType.EMAIL_MARK_UNREAD, "标记成功") : 
                Message.failure(ActionType.EMAIL_MARK_UNREAD, "标记失败");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_MARK_UNREAD, "标记未读失败: " + e.getMessage());
        }
    }

    /**
     * 处理批量标记已读请求
     */
    private Message handleBatchMarkAsRead(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            @SuppressWarnings("unchecked")
            List<String> emailIds = (List<String>) data.get("emailIds");
            String userId = (String) data.get("userId");
            
            if (emailIds == null || emailIds.isEmpty() || userId == null) {
                return Message.failure(ActionType.EMAIL_BATCH_MARK_READ, "参数不完整");
            }
            
            int successCount = emailService.batchMarkAsRead(emailIds, userId);
            return Message.success(ActionType.EMAIL_BATCH_MARK_READ, 
                String.format("成功标记 %d/%d 封邮件为已读", successCount, emailIds.size()));
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_BATCH_MARK_READ, "批量标记失败: " + e.getMessage());
        }
    }

    /**
     * 处理批量删除请求
     */
    private Message handleBatchDelete(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            @SuppressWarnings("unchecked")
            List<String> emailIds = (List<String>) data.get("emailIds");
            String userId = (String) data.get("userId");
            
            if (emailIds == null || emailIds.isEmpty() || userId == null) {
                return Message.failure(ActionType.EMAIL_BATCH_DELETE, "参数不完整");
            }
            
            int successCount = emailService.batchDeleteEmails(emailIds, userId);
            return Message.success(ActionType.EMAIL_BATCH_DELETE, 
                String.format("成功删除 %d/%d 封邮件", successCount, emailIds.size()));
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_BATCH_DELETE, "批量删除失败: " + e.getMessage());
        }
    }

    // ==================== 管理员操作处理 ====================

    /**
     * 处理管理员获取所有邮件请求
     */
    private Message handleAdminGetAllEmails(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            List<Email> emails = emailService.getAllEmails(page, pageSize);
            int totalCount = emailService.getAllEmailsCount();
            
            // 创建包含邮件列表和总数信息的响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
            
            return new Message(ActionType.EMAIL_ADMIN_GET_ALL, responseData, true, "获取所有邮件成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_ADMIN_GET_ALL, "获取所有邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理管理员搜索所有邮件请求
     */
    private Message handleAdminSearchAllEmails(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String keyword = (String) data.get("keyword");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (keyword == null || keyword.trim().isEmpty()) {
                return Message.failure(ActionType.EMAIL_ADMIN_SEARCH_ALL, "搜索关键词不能为空");
            }
            
            List<Email> emails = emailService.searchAllEmails(keyword, page, pageSize);
            int totalCount = emailService.searchAllEmailsCount(keyword);
            
            // 创建包含邮件列表和总数信息的响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
            responseData.put("keyword", keyword);
            
            return new Message(ActionType.EMAIL_ADMIN_SEARCH_ALL, responseData, true, "搜索邮件成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_ADMIN_SEARCH_ALL, "搜索邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理管理员获取用户邮件请求
     */
    private Message handleAdminGetUserEmails(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String userId = (String) data.get("userId");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (userId == null) {
                return Message.failure(ActionType.EMAIL_ADMIN_GET_USER_EMAILS, "用户ID不能为空");
            }
            
            List<Email> emails = emailService.getUserAllEmails(userId, page, pageSize);
            int totalCount = emailService.getUserAllEmailsCount(userId);
            
            // 创建包含邮件列表和总数信息的响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
            responseData.put("userId", userId);
            
            return new Message(ActionType.EMAIL_ADMIN_GET_USER_EMAILS, responseData, true, "获取用户邮件成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_ADMIN_GET_USER_EMAILS, "获取用户邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理管理员按用户搜索邮件请求
     */
    private Message handleAdminSearchByUser(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String userId = (String) data.get("userId");
            int page = data.get("page") != null ? (Integer) data.get("page") : 1;
            int pageSize = data.get("pageSize") != null ? (Integer) data.get("pageSize") : 10;
            
            if (userId == null || userId.trim().isEmpty()) {
                return Message.failure(ActionType.EMAIL_ADMIN_SEARCH_BY_USER, "用户ID不能为空");
            }
            
            List<Email> emails = emailService.getUserAllEmails(userId, page, pageSize);
            int totalCount = emailService.getUserAllEmailsCount(userId);
            
            // 创建包含邮件列表和总数信息的响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("emails", emails != null ? emails : List.of());
            responseData.put("totalCount", totalCount);
            responseData.put("currentPage", page);
            responseData.put("pageSize", pageSize);
            responseData.put("totalPages", (int) Math.ceil((double) totalCount / pageSize));
            responseData.put("userId", userId);
            
            return new Message(ActionType.EMAIL_ADMIN_SEARCH_BY_USER, responseData, true, "按用户搜索邮件成功");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_ADMIN_SEARCH_BY_USER, "按用户搜索邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理管理员删除邮件请求
     */
    private Message handleAdminDeleteEmail(Message message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) message.getData();
            
            String emailId = (String) data.get("emailId");
            
            if (emailId == null) {
                return Message.failure(ActionType.EMAIL_ADMIN_DELETE, "邮件ID不能为空");
            }
            
            boolean result = emailService.adminDeleteEmail(emailId);
            return result ? 
                Message.success(ActionType.EMAIL_ADMIN_DELETE, "删除成功") : 
                Message.failure(ActionType.EMAIL_ADMIN_DELETE, "删除失败");
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_ADMIN_DELETE, "删除邮件失败: " + e.getMessage());
        }
    }

    /**
     * 处理管理员获取统计信息请求
     */
    private Message handleAdminGetStatistics(Message message) {
        try {
            String statistics = emailService.getEmailStatistics();
            return Message.success(ActionType.EMAIL_ADMIN_GET_STATISTICS, statistics);
        } catch (Exception e) {
            return Message.failure(ActionType.EMAIL_ADMIN_GET_STATISTICS, "获取统计信息失败: " + e.getMessage());
        }
    }
}