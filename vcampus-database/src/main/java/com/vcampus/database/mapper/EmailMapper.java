package com.vcampus.database.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.vcampus.common.dto.Email;
import com.vcampus.common.enums.EmailStatus;

/**
 * 邮件数据访问接口
 * 实现邮件相关的数据库操作
 * 编写人：谌宣羽
 */
public interface EmailMapper {

    // ==================== 邮件基本操作 ====================

    /**
     * 创建邮件
     * @param email 邮件对象
     * @return 是否创建成功
     */
    int createEmail(Email email);

    /**
     * 根据邮件ID获取邮件
     * @param emailId 邮件ID
     * @return 邮件对象
     */
    Email getEmailById(@Param("emailId") String emailId);

    /**
     * 更新邮件信息
     * @param email 邮件对象
     * @return 是否更新成功
     */
    int updateEmail(Email email);

    /**
     * 删除邮件（直接从数据库删除）
     * @param emailId 邮件ID
     * @return 是否删除成功
     */
    int deleteEmail(@Param("emailId") String emailId);

    // ==================== 邮件查询操作 ====================

    /**
     * 获取用户的收件箱邮件
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邮件列表
     */
    List<Email> getInboxEmails(@Param("userId") String userId, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * 获取用户的发件箱邮件
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邮件列表
     */
    List<Email> getSentEmails(@Param("userId") String userId, 
                             @Param("offset") int offset, 
                             @Param("limit") int limit);

    /**
     * 获取用户的草稿箱邮件
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邮件列表
     */
    List<Email> getDraftEmails(@Param("userId") String userId, 
                              @Param("offset") int offset, 
                              @Param("limit") int limit);

    /**
     * 获取用户收件箱邮件总数
     * @param userId 用户ID
     * @return 收件箱邮件总数
     */
    int getInboxCount(@Param("userId") String userId);

    /**
     * 获取用户发件箱邮件总数
     * @param userId 用户ID
     * @return 发件箱邮件总数
     */
    int getSentCount(@Param("userId") String userId);

    /**
     * 获取用户草稿箱邮件总数
     * @param userId 用户ID
     * @return 草稿箱邮件总数
     */
    int getDraftCount(@Param("userId") String userId);

    /**
     * 根据状态获取邮件
     * @param userId 用户ID
     * @param status 邮件状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邮件列表
     */
    List<Email> getEmailsByStatus(@Param("userId") String userId, 
                                 @Param("status") EmailStatus status, 
                                 @Param("offset") int offset, 
                                 @Param("limit") int limit);

    /**
     * 搜索邮件（按主题）
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 邮件列表
     */
    List<Email> searchEmails(@Param("userId") String userId, 
                            @Param("keyword") String keyword, 
                            @Param("offset") int offset, 
                            @Param("limit") int limit);

    /**
     * 获取搜索邮件总数
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    int searchEmailsCount(@Param("userId") String userId, 
                         @Param("keyword") String keyword);

    // ==================== 邮件状态操作 ====================

    /**
     * 标记邮件为已读
     * @param emailId 邮件ID
     * @return 是否标记成功
     */
    int markAsRead(@Param("emailId") String emailId);

    /**
     * 标记邮件为未读
     * @param emailId 邮件ID
     * @return 是否标记成功
     */
    int markAsUnread(@Param("emailId") String emailId);

    /**
     * 更新邮件状态
     * @param emailId 邮件ID
     * @param status 新状态
     * @return 是否更新成功
     */
    int updateEmailStatus(@Param("emailId") String emailId, 
                         @Param("status") EmailStatus status);

    // ==================== 邮件管理员功能 ====================

    /**
     * 获取所有邮件（管理员权限）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 所有邮件列表
     */
    List<Email> getAllEmails(@Param("offset") int offset, 
                            @Param("limit") int limit);

    /**
     * 根据用户ID获取该用户的所有邮件（管理员权限）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 该用户的邮件列表
     */
    List<Email> getUserAllEmails(@Param("userId") String userId, 
                                @Param("offset") int offset, 
                                @Param("limit") int limit);

    /**
     * 搜索所有邮件（管理员权限）
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 搜索结果
     */
    List<Email> searchAllEmails(@Param("keyword") String keyword, 
                               @Param("offset") int offset, 
                               @Param("limit") int limit);

    /**
     * 获取所有邮件总数（管理员权限）
     * @return 邮件总数
     */
    int getAllEmailsCount();

    /**
     * 获取用户邮件总数（管理员权限）
     * @param userId 用户ID
     * @return 该用户的邮件总数
     */
    int getUserAllEmailsCount(@Param("userId") String userId);

    /**
     * 搜索所有邮件总数（管理员权限）
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    int searchAllEmailsCount(@Param("keyword") String keyword);

    /**
     * 从CSV文件加载邮件数据
     * @param filePath CSV文件路径
     */
    void loadEmailsFromCsv(@Param("filePath") String filePath);
}
