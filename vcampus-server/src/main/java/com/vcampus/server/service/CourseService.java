package com.vcampus.server.service;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.CourseStatus;
// 导入新的DAO实现类
import com.vcampus.server.dao.impl.CourseDao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务端课程服务 (最终实现版)
 *
 * 职责：
 * 1. 作为业务逻辑中心，处理所有与课程相关的复杂操作。
 * 2. 依赖于 ICourseDao 接口进行数据持久化操作。
 * 3. 为上层 Controller 提供清晰、简单的业务方法。
 */
public class CourseService {

    // Service 层依赖于 DAO 接口
    private final ICourseDao courseDAO;

    public CourseService() {
        // !!! 核心修改在此：将 FakeCourseDao 替换为 CourseDaoImpl !!!
        this.courseDAO = new CourseDao();
        System.out.println("SERVICE: CourseService 实例已创建，将使用 CourseDaoImpl 连接真实数据库。");
    }

    // ↓↓↓↓↓↓ 以下所有业务逻辑代码均无需任何修改 ↓↓↓↓↓↓

    /**
     * 获取指定学生的所有可选课程，并为该生动态计算课程状态。
     * @param userId 请求课程列表的学生ID
     * @return 包含处理后课程列表的 Message 对象
     */
    public Message getAllCourses(String userId) {
        try {
            List<Course> allCourses = courseDAO.getAllCourses();
            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
            Set<String> selectedSessionIds = userSelections.stream()
                    .map(CourseSelection::getSessionId)
                    .collect(Collectors.toSet());

            for (Course course : allCourses) {
                boolean isCourseSelected = course.getSessions().stream()
                        .anyMatch(session -> selectedSessionIds.contains(session.getSessionId()));
                course.setStatus(isCourseSelected ? CourseStatus.SELECTED.toString() : CourseStatus.NOT_SELECTED.toString());
                course.getSessions().forEach(session ->
                        session.setSelectedByStudent(selectedSessionIds.contains(session.getSessionId()))
                );
            }
            return Message.success(ActionType.GET_ALL_COURSES_RESPONSE, allCourses, "成功获取课程列表");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.GET_ALL_COURSES_RESPONSE, "获取课程列表时服务器出错: " + e.getMessage());
        }
    }

    /**
     * 处理学生选课的业务逻辑。
     * @param studentId 发起选课请求的学生ID
     * @param sessionId 目标教学班ID
     * @return 表示操作结果的 Message 对象
     */
    public Message selectCourse(String studentId, String sessionId) {
        try {
            if (courseDAO.isAlreadyEnrolled(studentId, sessionId)) {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：您已选过此课程");
            }
            if (courseDAO.isSessionFull(sessionId)) {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：课程人数已满");
            }
            if (courseDAO.hasScheduleConflict(studentId, sessionId)) {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：与已选课程时间冲突");
            }
            CourseSelection newSelection = new CourseSelection(studentId, sessionId, "已选");
            boolean success = courseDAO.addCourseSelection(newSelection);
            if(success) {
                return Message.success(ActionType.SELECT_COURSE_RESPONSE, "选课成功！");
            } else {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败，数据库操作异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "处理选课时服务器出错");
        }
    }

    /**
     * 处理学生退课的业务逻辑。
     * @param studentId 发起退课请求的学生ID
     * @param sessionId 目标教学班ID
     * @return 表示操作结果的 Message 对象
     */
    public Message dropCourse(String studentId, String sessionId) {
        try {
            boolean success = courseDAO.removeCourseSelection(studentId, sessionId);
            if(success) {
                return Message.success(ActionType.DROP_COURSE_RESPONSE, "退课成功！");
            } else {
                return Message.failure(ActionType.DROP_COURSE_RESPONSE, "退课失败，您可能未选此课");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.DROP_COURSE_RESPONSE, "处理退课时服务器出错");
        }
    }
}