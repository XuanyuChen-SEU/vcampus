package com.vcampus.server.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.vcampus.common.dao.IUserDao;
import com.vcampus.common.dto.Student; // 引入MyBatisUtil
import com.vcampus.common.dto.User;
import com.vcampus.common.entity.Balance;
import com.vcampus.database.mapper.ShopMapper;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.utils.MyBatisUtil;

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
    public boolean deleteUser(String userId) { 
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            
            // 先删除相关记录（学籍和余额）
            // 删除学籍记录（仅学生用户）
            if (userId != null && userId.startsWith("1")) {
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                studentMapper.deleteById(userId);
            }
            
            // 删除余额记录（仅学生和教师用户）
            if (userId != null && (userId.startsWith("1") || userId.startsWith("2"))) {
                ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
                shopMapper.deleteBalanceByUserId(userId);
            }
            
            // 最后删除用户记录
            userMapper.deleteById(userId);
            sqlSession.commit();
            return true;
        }
    }
    
    // === 用户管理相关方法实现 ===
    
    @Override
    public boolean createUser(User user) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.add(user);
            
            // 只为学生和教师创建余额记录，管理员不需要余额
            String userId = user.getUserId();
            if (userId != null && (userId.startsWith("1") || userId.startsWith("2"))) {
                ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
                Balance balance = new Balance(userId, new BigDecimal("100.00"));
                shopMapper.createBalance(balance);
            }
            
            // 只为学生用户创建学籍记录
            if (userId != null && userId.startsWith("1")) {
                StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                Student student = new Student();
                student.setUserId(userId);
                student.setName("待填写"); // 默认姓名，后续可由学籍管理员完善
                student.setStudent_status("在籍"); // 默认学籍状态
                studentMapper.add(student);
            }
            
            sqlSession.commit();
            return true;
        }
    }
    
    @Override
    public List<User> searchUsers(String searchText, String selectedRole) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 使用selectAll然后过滤，因为UserMapper没有searchByKeyword方法
            List<User> allUsers = userMapper.selectAll();
            return allUsers.stream()
                    .filter(user -> {
                        // 根据用户ID前缀判断角色
                        boolean roleMatch = true;
                        if (selectedRole != null && !selectedRole.isEmpty()) {
                            char firstChar = user.getUserId().charAt(0);
                            switch (selectedRole) {
                                case "学生":
                                    roleMatch = firstChar == '1';
                                    break;
                                case "教师":
                                    roleMatch = firstChar == '2';
                                    break;
                                case "管理员":
                                    roleMatch = firstChar >= '3';
                                    break;
                                default:
                                    roleMatch = true; // 如果角色不明确，显示所有
                            }
                        }
                        // 检查用户ID是否包含搜索文本
                        boolean textMatch = user.getUserId().contains(searchText);
                        
                        return roleMatch && textMatch;
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
    }
    
    @Override
    public boolean resetUserPassword(User user) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int affectedRows = userMapper.update(user);
            sqlSession.commit();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean isUserIdExists(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectById(userId);
            return user != null;
        }
    }
}