package com.vcampus.database.mapper;

import com.vcampus.common.dto.ClassSession;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * ClassSessionMapper 接口，用于操作 class_sessions 表
 */
public interface ClassSessionMapper {

    void loadClassSessionsFromCsv(String filePath);


    /**
     * 根据教学班ID查询信息
     * @param sessionId 教学班ID
     * @return ClassSession 对象
     */
    ClassSession selectById(String sessionId);

    /**
     * 根据学生ID查询该生所有已选教学班的完整信息
     * @param studentId 学生ID
     * @return ClassSession 列表
     */
    List<ClassSession> selectSessionsByStudentId(String studentId);

    /**
     * 将指定教学班的已选人数+1
     * @param sessionId 教学班ID
     * @return 影响的行数
     */
    int incrementEnrolledCount(@Param("sessionId") String sessionId);

    /**
     * 将指定教学班的已选人数-1
     * @param sessionId 教学班ID
     * @return 影响的行数
     */
    int decrementEnrolledCount(@Param("sessionId") String sessionId);
}