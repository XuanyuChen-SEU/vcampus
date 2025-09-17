package com.vcampus.common.dao;

import com.vcampus.common.dto.StudentLeaveApplication;

import java.util.List;

public interface IStudentLeaveApplicationDao {

    /**
     * 从 CSV 文件批量加载学籍异动申请表
     */
    void loadFromCsv(String filePath);

    /**
     * 插入一条申请记录
     */
    boolean insert(StudentLeaveApplication application);

    /**
     * 查询某个学生的最新申请
     */
    StudentLeaveApplication selectLatestByStudentId(String studentId);

    /**
     * 更新申请状态
     */
    boolean updateStatus(int applicationId, String status);

    public List<StudentLeaveApplication> selectAllApplications();
}
