package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.IUserDao;
import com.vcampus.common.dto.User;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.utils.MyBatisUtil; // 引入MyBatisUtil
import org.apache.ibatis.session.SqlSession;

public class UserDao implements IUserDao {

    // 不再需要任何 static 成员变量和初始化方法

    @Override
    public User getUserById(String id) {
        // 每次操作都获取一个新的 SqlSession
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.selectById(id);
        }
        // try-with-resources 会自动关闭 sqlSession
    }

    @Override
    public boolean updateUser(User user) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int affectedRows = userMapper.update(user);
            // 对于写操作（insert, update, delete），需要手动提交事务
            sqlSession.commit();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteUser(User user) { // 接口的参数最好直接是ID，而不是整个User对象
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.deleteById(user.getUserId());
            sqlSession.commit();
            return true;
        }
    }

}