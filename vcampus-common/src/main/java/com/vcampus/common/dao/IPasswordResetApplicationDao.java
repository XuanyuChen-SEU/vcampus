package com.vcampus.common.dao;

import java.util.List;

import com.vcampus.common.dto.PasswordResetApplication;

/**
 * 密码重置申请数据访问对象接口
 * 定义密码重置申请相关的数据库操作方法
 * 编写人：谌宣羽
 */
public interface IPasswordResetApplicationDao {

    /**
     * 根据用户ID查询密码重置申请
     * @param userId 用户ID
     * @return 密码重置申请
     */
    PasswordResetApplication selectById(String userId);
    
    /**
     * 查询所有密码重置申请
     * @return 密码重置申请列表
     */
    List<PasswordResetApplication> selectAll();
    
    /**
     * 添加密码重置申请
     * @param application 密码重置申请
     * @return 是否添加成功
     */
    boolean add(PasswordResetApplication application);
    
    /**
     * 更新密码重置申请
     * @param application 密码重置申请
     * @return 是否更新成功
     */
    boolean update(PasswordResetApplication application);
    
    /**
     * 根据用户ID删除密码重置申请
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteById(String userId);
}
