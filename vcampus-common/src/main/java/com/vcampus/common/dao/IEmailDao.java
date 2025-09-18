package com.vcampus.common.dao;

import java.util.List;

import com.vcampus.common.dto.Email;
import com.vcampus.common.enums.EmailStatus;

/**
 * 邮件数据访问接口（简化版）
 * 定义邮件相关的数据库操作
 * 编写人：AI Assistant
 */
public interface IEmailDao {

    // ==================== 邮件基本操作 ====================

    /**
     * 创建邮件
     * @param email 邮件对象
     * @return 是否创建成功
     */
    boolean createEmail(Email email);

    /**
     * 根据邮件ID获取邮件
     * @param emailId 邮件ID
     * @return 邮件对象
     */
    Email getEmailById(String emailId);

    /**
     * 更新邮件信息
     * @param email 邮件对象
     * @return 是否更新成功
     */
    boolean updateEmail(Email email);

    /**
     * 删除邮件（直接从数据库删除）
     * @param emailId 邮件ID
     * @return 是否删除成功
     */
    boolean deleteEmail(String emailId);

    // ==================== 邮件查询操作 ====================

    /**
     * 获取用户的收件箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    List<Email> getInboxEmails(String userId, int page, int pageSize);

    /**
     * 获取用户的发件箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    List<Email> getSentEmails(String userId, int page, int pageSize);

    /**
     * 获取用户的草稿箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    List<Email> getDraftEmails(String userId, int page, int pageSize);

    /**
     * 获取用户收件箱邮件总数
     * @param userId 用户ID
     * @return 收件箱邮件总数
     */
    int getInboxCount(String userId);

    /**
     * 获取用户发件箱邮件总数
     * @param userId 用户ID
     * @return 发件箱邮件总数
     */
    int getSentCount(String userId);

    /**
     * 获取用户草稿箱邮件总数
     * @param userId 用户ID
     * @return 草稿箱邮件总数
     */
    int getDraftCount(String userId);

    /**
     * 根据状态获取邮件
     * @param userId 用户ID
     * @param status 邮件状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    List<Email> getEmailsByStatus(String userId, EmailStatus status, int page, int pageSize);

    /**
     * 搜索邮件（按主题、内容、发送者）
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    List<Email> searchEmails(String userId, String keyword, int page, int pageSize);

    /**
     * 获取搜索邮件总数
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    int searchEmailsCount(String userId, String keyword);

    // ==================== 邮件状态操作 ====================

    /**
     * 标记邮件为已读
     * @param emailId 邮件ID
     * @return 是否标记成功
     */
    boolean markAsRead(String emailId);

    /**
     * 标记邮件为未读
     * @param emailId 邮件ID
     * @return 是否标记成功
     */
    boolean markAsUnread(String emailId);

    /**
     * 更新邮件状态
     * @param emailId 邮件ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateEmailStatus(String emailId, EmailStatus status);

    // ==================== 邮件管理员功能 ====================

    /**
     * 获取所有邮件（管理员权限）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 所有邮件列表
     */
    List<Email> getAllEmails(int page, int pageSize);

    /**
     * 根据用户ID获取该用户的所有邮件（管理员权限）
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 该用户的邮件列表
     */
    List<Email> getUserAllEmails(String userId, int page, int pageSize);

}
