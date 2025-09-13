package com.vcampus.database.mapper;

import java.util.List;

import com.vcampus.common.dto.PasswordResetApplication;

/**
 * 密码重置申请数据库映射器接口
 * 提供密码重置申请相关的数据库操作
 * 编写人：谌宣羽
 */
public interface PasswordResetApplicationMapper {
    
    /**
     * 创建密码重置申请表
     */
    void createPasswordResetApplicationTable();
    
    /**
     * 删除密码重置申请表
     * @param dbName 数据库名称
     */
    void dropPasswordResetApplicationTable(String dbName);

    /*
     * 加载数据
     */
    void loadPasswordResetApplicationsFromCsv(String filePath);

    /**
     * 删除所有表
     */
    void dropTables(String dbName);
    
    /**
     * 查询所有密码重置申请
     * @return 密码重置申请列表
     */
    List<PasswordResetApplication> selectAll();
    
    /**
     * 根据用户ID查询密码重置申请
     * @param userId 用户ID
     * @return 密码重置申请
     */
    PasswordResetApplication selectById(String userId);
    
    /**
     * 添加密码重置申请
     * @param application 密码重置申请
     */
    void add(PasswordResetApplication application);
    
    /**
     * 更新密码重置申请
     * @param application 密码重置申请
     * @return 影响的行数
     */
    int update(PasswordResetApplication application);
    
    /**
     * 根据用户ID删除密码重置申请
     * @param userId 用户ID
     */
    void deleteById(String userId);
    
}
