package com.vcampus.common.dao;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import java.util.List;

/**
 * 课程数据访问对象 (DAO) 接口
 * 定义了所有与课程相关的数据库操作。
 * Service 层将依赖此接口，而不是具体的实现类。
 */
public interface ICourseDao {

    // 获取所有课程的基本信息
    List<Course> getAllCourses();
    // 获取指定学生的所有选课记录
    List<CourseSelection> getSelectionsByStudentId(String studentId);
    // 检查特定教学班是否已满
    boolean isSessionFull(String sessionId);
    // 检查学生在新选课程时是否有时间冲突
    boolean hasScheduleConflict(String studentId, String newSessionId);
    // 添加一条选课记录
    boolean addCourseSelection(CourseSelection selection);
    // 删除一条选课记录
    boolean removeCourseSelection(String studentId, String sessionId);
    // 检查学生是否已选过该课程
    boolean isAlreadyEnrolled(String studentId, String sessionId);
}
