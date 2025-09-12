package com.vcampus.server.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.vcampus.common.dao.IPasswordResetApplicationDao;
import com.vcampus.common.dto.PasswordResetApplication;
import com.vcampus.database.mapper.PasswordResetApplicationMapper;
import com.vcampus.database.utils.MyBatisUtil;

/**
 * 密码重置申请数据访问对象实现类
 * 实现密码重置申请相关的数据库操作
 * 编写人：谌宣羽
 */
public class PasswordResetApplicationDao implements IPasswordResetApplicationDao {
    
    @Override
    public PasswordResetApplication selectById(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            return passwordResetApplicationMapper.selectById(userId);
        }
    }

    @Override
    public List<PasswordResetApplication> selectAll() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            return passwordResetApplicationMapper.selectAll();
        }
    }
    
    @Override
    public boolean add(PasswordResetApplication application) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            passwordResetApplicationMapper.add(application);
            sqlSession.commit();
            return true;
        }
    }
    
    @Override
    public boolean update(PasswordResetApplication application) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            int affectedRows = passwordResetApplicationMapper.update(application);
            sqlSession.commit();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean deleteById(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            passwordResetApplicationMapper.deleteById(userId);
            sqlSession.commit();
            return true;
        }
    }
}