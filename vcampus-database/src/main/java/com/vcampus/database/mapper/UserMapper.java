package com.vcampus.database.mapper;


import com.vcampus.common.dto.User;

import java.util.List;
import java.util.Map;

public interface   UserMapper {
    // 创建数据库
    void createDatabase(String dbName);
    // 删除数据库
    void dropDatabase(String dbName);
    // 创建用户表（示例表）
    void createUserTable();
    // 删除用户表
    void dropUserTable(String dbName);
    // 插入用户数据
    void InsertTempData();
    List<User> selectAll();
    User selectById(String userId);
    List<User> selectByCondition(Map map);
    List<User> selectBySingleCondition(User user);
    void add(User user);
    int update(User user);
    void deleteById(String userId);
    void deleteByIds(String[] userIds);
}
