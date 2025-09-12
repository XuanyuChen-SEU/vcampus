package com.vcampus.common.dao;

import com.vcampus.common.dto.CourseSelection;
import java.util.List;

/**
 * 选课记录数据访问对象 (DAO) 接口.
 * 定义了所有与选课记录相关的数据库操作。
 */
public interface ICourseSelectionDao {
    /**
     * 初始化数据库连接
     */
    void IUDInit();

    /**
     * 添加一条选课记录
     * @param selection 选课记录对象
     * @return 是否添加成功
     */
    boolean addSelection(CourseSelection selection);

    /**
     * 更新选课记录状态
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateSelectionStatus(String studentId, String courseId, String status);

    /**
     * 删除一条选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否删除成功
     */
    boolean deleteSelection(String studentId, String courseId);

    /**
     * 根据学生ID获取其所有选课记录
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    List<CourseSelection> getSelectionsByStudentId(String studentId);

    /**
     * 根据课程ID获取选修该课程的所有学生ID
     * @param courseId 课程ID
     * @return 学生ID列表
     */
    List<String> getStudentsByCourseId(String courseId);

    /**
     * 获取特定学生的特定课程的选课记录
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 选课记录对象，如果不存在则返回null
     */
    CourseSelection getSelectionByStudentAndCourse(String studentId, String courseId);

    /**
     * 检查课程时间是否冲突
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否冲突
     */
    boolean checkTimeConflict(String studentId, String courseId);
}
