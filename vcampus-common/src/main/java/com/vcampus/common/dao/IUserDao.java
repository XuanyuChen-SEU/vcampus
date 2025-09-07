package com.vcampus.common.dao;

import com.vcampus.common.dto.User;

public interface IUserDao {//数据访问对象
    /*
        设置数据库;
     */
    void UDInit();

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
     * @param user 用户信息
     * @return 是否删除成功
     */
    boolean deleteUser(User user);

    void UDClose();
}
