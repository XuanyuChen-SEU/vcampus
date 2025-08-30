package com.vcampus.common.dao;

import com.vcampus.common.dto.User;

/**
 * 用户数据访问接口
 * 定义用户相关的数据库操作方法
 * 编写人：谌宣羽
 */
public interface UserDAO {
    
    /**
     * 根据用户ID查找用户
     * @param userId 用户ID（7位字符串）
     * @return 用户对象，如果不存在返回null
     */
    User findById(String userId);
    
    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数，成功返回1，失败返回0
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 影响的行数，成功返回1，失败返回0
     */
    int update(User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 影响的行数，成功返回1，失败返回0
     */
    int delete(String userId);
    
    /**
     * 检查用户ID是否存在
     * @param userId 用户ID
     * @return 如果存在返回true，否则返回false
     */
    boolean exists(String userId);
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码（加密后的）
     * @return 影响的行数，成功返回1，失败返回0
     */
    int updatePassword(String userId, String newPassword);
    
    /**
     * 更新用户手机号
     * @param userId 用户ID
     * @param newPhone 新手机号
     * @return 影响的行数，成功返回1，失败返回0
     */
    int updatePhone(String userId, String newPhone);
}
