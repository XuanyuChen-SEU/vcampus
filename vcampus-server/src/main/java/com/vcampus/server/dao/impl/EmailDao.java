package com.vcampus.server.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.vcampus.common.dao.IEmailDao;
import com.vcampus.common.dto.Email;
import com.vcampus.common.enums.EmailStatus;
import com.vcampus.database.mapper.EmailMapper;
import com.vcampus.database.utils.MyBatisUtil;

/**
 * 邮件数据访问对象实现类
 * 实现邮件相关的数据库操作
 * 编写人：谌宣羽
 */
public class EmailDao implements IEmailDao {

    // ==================== 邮件基本操作 ====================

    /**
     * 创建邮件
     * @param email 邮件对象
     * @return 是否创建成功
     */
    @Override
    public boolean createEmail(Email email) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int result = mapper.createEmail(email);
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据邮件ID获取邮件
     * @param emailId 邮件ID
     * @return 邮件对象
     */
    @Override
    public Email getEmailById(String emailId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.getEmailById(emailId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新邮件信息
     * @param email 邮件对象
     * @return 是否更新成功
     */
    @Override
    public boolean updateEmail(Email email) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int result = mapper.updateEmail(email);
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除邮件（直接从数据库删除）
     * @param emailId 邮件ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteEmail(String emailId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            // 直接删除邮件
            int result = mapper.deleteEmail(emailId);
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 邮件查询操作 ====================

    /**
     * 获取用户的收件箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    @Override
    public List<Email> getInboxEmails(String userId, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.getInboxEmails(userId, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户的发件箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    @Override
    public List<Email> getSentEmails(String userId, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.getSentEmails(userId, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户的草稿箱邮件
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    @Override
    public List<Email> getDraftEmails(String userId, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.getDraftEmails(userId, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户收件箱邮件总数
     * @param userId 用户ID
     * @return 收件箱邮件总数
     */
    @Override
    public int getInboxCount(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.getInboxCount(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取用户发件箱邮件总数
     * @param userId 用户ID
     * @return 发件箱邮件总数
     */
    @Override
    public int getSentCount(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.getSentCount(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取用户草稿箱邮件总数
     * @param userId 用户ID
     * @return 草稿箱邮件总数
     */
    @Override
    public int getDraftCount(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.getDraftCount(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据状态获取邮件
     * @param userId 用户ID
     * @param status 邮件状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    @Override
    public List<Email> getEmailsByStatus(String userId, EmailStatus status, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.getEmailsByStatus(userId, status, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 搜索邮件（按主题）
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 邮件列表
     */
    @Override
    public List<Email> searchEmails(String userId, String keyword, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.searchEmails(userId, keyword, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取搜索邮件总数
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    @Override
    public int searchEmailsCount(String userId, String keyword) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.searchEmailsCount(userId, keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ==================== 邮件状态操作 ====================

    /**
     * 标记邮件为已读
     * @param emailId 邮件ID
     * @return 是否标记成功
     */
    @Override
    public boolean markAsRead(String emailId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int result = mapper.markAsRead(emailId);
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 标记邮件为未读
     * @param emailId 邮件ID
     * @return 是否标记成功
     */
    @Override
    public boolean markAsUnread(String emailId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int result = mapper.markAsUnread(emailId);
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新邮件状态
     * @param emailId 邮件ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    public boolean updateEmailStatus(String emailId, EmailStatus status) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int result = mapper.updateEmailStatus(emailId, status);
            sqlSession.commit();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 邮件管理员功能 ====================

    /**
     * 获取所有邮件（管理员权限）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 所有邮件列表
     */
    @Override
    public List<Email> getAllEmails(int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.getAllEmails(offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据用户ID获取该用户的所有邮件（管理员权限）
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 该用户的邮件列表
     */
    @Override
    public List<Email> getUserAllEmails(String userId, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.getUserAllEmails(userId, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== 扩展功能 ====================

    /**
     * 搜索所有邮件（管理员权限）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    public List<Email> searchAllEmails(String keyword, int page, int pageSize) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            int offset = (page - 1) * pageSize;
            return mapper.searchAllEmails(keyword, offset, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取所有邮件总数（管理员权限）
     * @return 邮件总数
     */
    public int getAllEmailsCount() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.getAllEmailsCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取用户邮件总数（管理员权限）
     * @param userId 用户ID
     * @return 该用户的邮件总数
     */
    public int getUserAllEmailsCount(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.getUserAllEmailsCount(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 搜索所有邮件总数（管理员权限）
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    public int searchAllEmailsCount(String keyword) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            return mapper.searchAllEmailsCount(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取用户邮件总数（用于分页）
     * @param userId 用户ID
     * @param status 邮件状态（可选）
     * @return 邮件总数
     */
    public int getEmailCount(String userId, EmailStatus status) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            if (status == null) {
                // 获取用户所有邮件数量
                List<Email> allEmails = mapper.getUserAllEmails(userId, 0, Integer.MAX_VALUE);
                return allEmails != null ? allEmails.size() : 0;
            } else {
                // 获取特定状态的邮件数量
                List<Email> statusEmails = mapper.getEmailsByStatus(userId, status, 0, Integer.MAX_VALUE);
                return statusEmails != null ? statusEmails.size() : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取未读邮件数量
     * @param userId 用户ID
     * @return 未读邮件数量
     */
    public int getUnreadEmailCount(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
            List<Email> unreadEmails = mapper.getEmailsByStatus(userId, EmailStatus.SENT, 0, Integer.MAX_VALUE);
            return unreadEmails != null ? unreadEmails.size() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 批量标记邮件为已读
     * @param emailIds 邮件ID列表
     * @return 成功标记的数量
     */
    public int batchMarkAsRead(List<String> emailIds) {
        int successCount = 0;
        for (String emailId : emailIds) {
            if (markAsRead(emailId)) {
                successCount++;
            }
        }
        return successCount;
    }

    /**
     * 批量删除邮件
     * @param emailIds 邮件ID列表
     * @return 成功删除的数量
     */
    public int batchDeleteEmails(List<String> emailIds) {
        int successCount = 0;
        for (String emailId : emailIds) {
            if (deleteEmail(emailId)) {
                successCount++;
            }
        }
        return successCount;
    }
}
