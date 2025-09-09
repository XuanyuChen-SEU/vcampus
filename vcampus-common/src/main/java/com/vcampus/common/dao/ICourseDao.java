package com.vcampus.common.dao;

import com.vcampus.common.dto.Course;
import java.util.List;

/**
 * 课程数据访问对象 (DAO) 接口.
 * 定义了所有与课程相关的数据库操作。
 */
public interface ICourseDao {
    /**
     * 获取数据库中所有课程的列表。
     * @return 包含所有课程信息的列表。
     */
    List<Course> getAllCourses();

    /**
     * 根据学生ID获取该生已选的所有课程ID。
     * @param studentId 学生ID。
     * @return 一个包含课程ID的字符串列表。
     */
    List<String> getCoursesByStudentId(String studentId);

    /**
     * 尝试为学生选择一门课程（原子操作）。
     * @param studentId 学生ID。
     * @param courseId 课程ID。
     * @return 如果选课成功，返回 true；否则返回 false。
     */
    boolean selectCourse(String studentId, String courseId);

    /**
     * 尝试为学生退掉一门课程（原子操作）。
     * @param studentId 学生ID。
     * @param courseId 课程ID。
     * @return 如果退课成功，返回 true；否则返回 false。
     */
    boolean dropCourse(String studentId, String courseId);

    /**
     * 检查指定课程的已选人数是否已达到或超过其容量。
     * @param courseId 课程ID。
     * @return 如果课程已满，返回 true；否则返回 false。
     */
    boolean isCourseFull(String courseId);


    /**
     * 根据课程ID获取课程信息，可以先是名字
     * @param courseId 课程ID
     * @return 课程对象，如果不存在则返回null
     */
    Course getCourseById(String courseId);

    /**
     * 更新课程信息
     * @param course 课程对象
     * @return 是否更新成功
     */
    boolean updateCourse(Course course);

    /**
     * 根据教师姓名获取其教授的课程
     * @param teacherName 教师姓名
     * @return 该教师教授的课程列表
     * 这个不急着实现
     */
    List<Course> getCoursesByTeacherName(String teacherName);

    /**
     * 检查指定学生是否已选某门课程
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否已选
     */
    boolean isCourseSelectedByStudent(String studentId, String courseId);

    /**
     * 获取课程的已选人数
     * @param courseId 课程ID
     * @return 已选人数
     */
    int getCourseEnrolledCount(String courseId);

    /**
     * 检查课程时间是否冲突
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否冲突
     */
    boolean checkTimeConflict(String studentId, String courseId);
}
