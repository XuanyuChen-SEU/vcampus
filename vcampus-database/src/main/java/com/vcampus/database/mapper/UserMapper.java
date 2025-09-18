package com.vcampus.database.mapper;


import java.util.List;
import java.util.Map;

import com.vcampus.common.dto.User;

public interface UserMapper {

    void loadUsersFromCsv(String filePath);

    List<User> selectAll();
    User selectById(String userId);
    List<User> selectByCondition(Map map);
    List<User> selectBySingleCondition(User user);
    void add(User user);
    int update(User user);
    void deleteById(String userId);
    void deleteByIds(String[] userIds);
}
