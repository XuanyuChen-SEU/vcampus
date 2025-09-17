package com.vcampus.database.mapper;

import com.vcampus.common.dto.StudentLeaveApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentLeaveApplicationMapper {
    void loadStudentLeaveApplicationsFromCsv(String filePath);

    int insertApplication(StudentLeaveApplication application);

    StudentLeaveApplication selectLatestByStudentId(@Param("studentId") String studentId);

    int updateStatus(@Param("applicationId") int applicationId, @Param("status") String status);

    List<StudentLeaveApplication> selectAllApplications();
}

