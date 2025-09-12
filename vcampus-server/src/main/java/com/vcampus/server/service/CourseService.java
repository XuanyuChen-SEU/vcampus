package com.vcampus.server.service;

import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.CourseStatus;
import com.vcampus.server.dao.impl.CourseDao;
import com.vcampus.common.dao.ICourseDao;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseService {
    //这里要紧行具体实现
    private final ICourseDao courseDAO = new CourseDao();

    public Message getAllCourses(String userId) {
        try {
            // 1. 从DAO获取所有课程的“模板”数据
            List<Course> allCourses = courseDAO.getAllCourses();
            // 2. 从DAO获取该学生的所有选课记录
            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
            Set<String> selectedSessionIds = userSelections.stream()
                    .map(CourseSelection::getCourseId)
                    .collect(Collectors.toSet());

            // 3. ⭐ 核心业务逻辑：为该学生动态计算每门课的状态
            for (Course course : allCourses) {
                // 判断学生是否已选该课程下的某个教学班
                boolean isCourseSelected = course.getSessions().stream()
                        .anyMatch(session -> selectedSessionIds.contains(session.getSessionId()));

                if (isCourseSelected) {
                    course.setStatus(CourseStatus.SELECTED);
                    // 标记具体的教学班为已选
                    course.getSessions().forEach(session -> {
                        if (selectedSessionIds.contains(session.getSessionId())) {
                            session.setSelectedByStudent(true);
                        }
                    });
                } else {
                    // TODO: 在这里可以添加更复杂的冲突、已满等状态判断
                    course.setStatus(CourseStatus.NOT_SELECTED);
                }
                course.setSessionnum(course.getSessions().size()); // 更新教学班数量
            }

            return new Message(ActionType.GET_ALL_COURSES_RESPONSE, true, allCourses.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(ActionType.GET_ALL_COURSES_RESPONSE, false, "获取课程列表时服务器出错");
        }
    }

    public Message selectCourse(String studentId, String sessionId) {
        try {
            // 在这里进行业务逻辑判断
            if (courseDAO.isSessionFull(sessionId)) {
                return new Message(ActionType.SELECT_COURSE_RESPONSE, false, "选课失败：课程人数已满");
            }
            if (courseDAO.hasScheduleConflict(studentId, sessionId)) {
                return new Message(ActionType.SELECT_COURSE_RESPONSE, false, "选课失败：与已选课程时间冲突");
            }
            // ... 其他判断

            // 调用DAO执行数据库操作
            CourseSelection newSelection = new CourseSelection(studentId, sessionId, "已选");
            boolean success = courseDAO.addCourseSelection(newSelection);

            if(success) {
                return new Message(ActionType.SELECT_COURSE_RESPONSE, true, "选课成功！");
            } else {
                return new Message(ActionType.SELECT_COURSE_RESPONSE, false, "选课失败，数据库操作异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(ActionType.SELECT_COURSE_RESPONSE, false, "处理选课时服务器出错");
        }
    }

    public Message dropCourse(String studentId, String sessionId) {
        try {
            boolean success = courseDAO.removeCourseSelection(studentId, sessionId);
            if(success) {
                return new Message(ActionType.DROP_COURSE_RESPONSE, true, "退课成功！");
            } else {
                return new Message(ActionType.DROP_COURSE_RESPONSE, false, "退课失败，您可能未选此课");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(ActionType.DROP_COURSE_RESPONSE, false, "处理退课时服务器出错");
        }
    }
}

