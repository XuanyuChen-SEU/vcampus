package com.vcampus.database.mapper;

import com.vcampus.common.dto.CourseSelection;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * CourseSelectionMapper 接口，用于操作 course_selections 表
 */
public interface CourseSelectionMapper {
    void loadCourseSelectionsFromCsv(String csvFilePath);
    /**
     * 根据学生ID查询所有选课记录
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    List<CourseSelection> selectByStudentId(String studentId);

    /**
     * 根据学生ID和教学班ID查询单条选课记录
     * @param studentId 学生ID
     * @param sessionId 教学班ID
     * @return 单条选课记录
     */
    CourseSelection selectByStudentAndSession(@Param("studentId") String studentId, @Param("sessionId") String sessionId);

    /**
     * 插入一条新的选课记录
     * @param selection 选课记录对象
     * @return 影响的行数
     */
    int insert(CourseSelection selection);

    /**
     * 根据学生ID和教学班ID删除一条选课记录
     * @param studentId 学生ID
     * @param sessionId 教学班ID
     * @return 影响的行数
     */
    int delete(@Param("studentId") String studentId, @Param("sessionId") String sessionId);
}