package com.vcampus.database.mapper;

import com.vcampus.common.dto.Course;
import java.util.List;

/**
 *  接口，用于操作 courses 和 class_sessions 表
 */
public interface CourseMapper {
    void loadCoursesFromCsv(String filePath);
    /**
     * 查询所有课程，并使用左连接一次性加载其下的所有教学班
     * @return 包含教学班列表的课程列表
     */
    List<Course> selectAllCoursesWithSessions();
}