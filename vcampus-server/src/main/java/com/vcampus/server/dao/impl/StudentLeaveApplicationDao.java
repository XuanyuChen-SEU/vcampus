package com.vcampus.server.dao.impl;

import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.database.mapper.StudentLeaveApplicationMapper;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class StudentLeaveApplicationDao {

    /**
     * 插入一条申请记录
     */
    public boolean insert(StudentLeaveApplication application) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentLeaveApplicationMapper mapper = sqlSession.getMapper(StudentLeaveApplicationMapper.class);
            int rows = mapper.insertApplication(application);
            sqlSession.commit();
            return rows > 0;
        }
    }

    /**
     * 查询某个学生的最新申请
     */
    public StudentLeaveApplication selectLatestByStudentId(String studentId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentLeaveApplicationMapper mapper = sqlSession.getMapper(StudentLeaveApplicationMapper.class);
            return mapper.selectLatestByStudentId(studentId);
        }
    }

    /**
     * 更新申请状态
     */
    public boolean updateStatus(String applicationId, String status) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentLeaveApplicationMapper mapper = sqlSession.getMapper(StudentLeaveApplicationMapper.class);
            int rows = mapper.updateStatus(applicationId, status);
            sqlSession.commit();
            return rows > 0;
        }
    }

    /**
     * 查询所有学生请假申请
     */
    public List<StudentLeaveApplication> selectAllApplications() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentLeaveApplicationMapper mapper = sqlSession.getMapper(StudentLeaveApplicationMapper.class);
            return mapper.selectAllApplications();
        }
    }

    public StudentLeaveApplication findById(String id) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentLeaveApplicationMapper mapper = sqlSession.getMapper(StudentLeaveApplicationMapper.class);
            return mapper.findById(id);
        }
    }
}

