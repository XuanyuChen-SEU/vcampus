package com.vcampus.common.dao;

import java.util.List;

import com.vcampus.common.dto.User;

public interface IUserDao {//数据访问对象

    /*
     * @param id 用户ID
     * @return 用户信息
     * 如果不存在，返回空值User，都是null
     */
    User getUserById(String id);

    /*
     * @param user 用户信息
     * @return 是否更新成功
     */
    boolean updateUser(User user);

    /*
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(String userId);
    
    // === 用户管理相关方法 ===
    
    /**
     * 创建新用户
     * @param user 用户信息
     * @return 是否创建成功
     */
    boolean createUser(User user);
    
    /**
     * 搜索用户（根据用户ID或姓名）
     * @param searchText 搜索关键词
     * @param selectedRole 角色
     * @return 用户列表
     */
    List<User> searchUsers(String searchText, String selectedRole);
    
    /**
     * 重置用户密码
     * @param user 用户信息
     * @return 是否重置成功
     */
    boolean resetUserPassword(User user);
    
    /**
     * 检查用户ID是否已存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean isUserIdExists(String userId);

}
