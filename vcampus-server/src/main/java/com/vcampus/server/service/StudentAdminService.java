package com.vcampus.server.service;

import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.server.dao.impl.StudentDao;
import com.vcampus.server.dao.impl.StudentLeaveApplicationDao;
import com.vcampus.common.dto.Student;

import java.util.List;

/**
 * 服务端管理员端学生信息业务类
 */
public class StudentAdminService {

    private final StudentDao studentDao = new StudentDao();
    private final StudentLeaveApplicationDao studentleaveapplicationDao = new StudentLeaveApplicationDao();

    public List<Student> findAll() {
        return studentDao.findAll();
    }

    public List<Student> findByNameLike(String nameKeyword) {
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return findAll();
        }
        return studentDao.findByNameLike("%" + nameKeyword + "%");
    }

    public Student getStudentById(String userId) {
        return studentDao.findByUserId(userId);
    }

    public boolean updateStudentInfo(Student student) {
        return studentDao.update(student) ; // update 返回受影响行数
    }

    /**
     * 获取所有请假申请
     */
    public List<StudentLeaveApplication> getAllApplications() {
        return studentleaveapplicationDao.selectAllApplications(); // 从数据库中查询所有申请
    }

}

