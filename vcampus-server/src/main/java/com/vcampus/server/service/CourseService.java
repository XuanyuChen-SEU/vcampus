package com.vcampus.server.service;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.CourseStatus;
import com.vcampus.server.dao.impl.FakeCourseDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务端课程服务 (最终测试版)
 * 职责：
 * 1. 在构造时创建【您指定的】模拟数据。
 * 2. 将模拟数据注入到一个 FakeCourseDao 实例中。
 * 3. 包含所有业务逻辑，并通过 DAO 接口进行数据操作。
 */
public class CourseService {

    private final ICourseDao courseDAO;

    public CourseService() {
        FakeCourseDao fakeDao = new FakeCourseDao();
        this.courseDAO = fakeDao;

        List<Course> courseTable = new ArrayList<>();
        List<CourseSelection> selectionTable = new ArrayList<>();

        System.out.println("SERVICE: 正在创建【最终版】可交互的测试数据...");

        // ⭐ 1. 用于测试【退课】功能
        List<ClassSession> englishSessions = List.of(
                new ClassSession("ENG_S01", "[01] 张老师", "1-16周 周二 3-4节", 60, 16, false)
        );
        courseTable.add(new Course("B17M0010", "大学英语II", "必修", "外国语学院", null, englishSessions));

        // ⭐ 2. 用于测试【选课】功能
        List<ClassSession> seSessions = List.of(
                new ClassSession("SE_S01", "[01] 刘老师", "1-16周 周一 5-6节", 50, 20, false)
        );
        courseTable.add(new Course("B08M4000", "软件工程", "限选", "计算机学院", null, seSessions));

        // ⭐ 3. 用于测试【选课失败 - 已满】
        List<ClassSession> networkSessions = List.of(
                new ClassSession("CS_S01", "[01] 王教授", "1-8周 周一 1-4节", 50, 50, false)
        );
        courseTable.add(new Course("B08M3000", "计算机网络", "必修", "计算机学院", null, networkSessions));

        // ⭐ 4. 为学生 '1234567' 创建初始选课记录
        // 注意：这里的 sessionId 'ENG_S01' 必须与上面课程中定义的 sessionId 一致
        selectionTable.add(new CourseSelection("1234567", "ENG_S01", "已选"));

        // 注入数据
        fakeDao.setCourseTable(courseTable);
        fakeDao.setSelectionTable(selectionTable);
    }

    // --- 业务逻辑方法 (使用 Message.success/failure 静态方法) ---

    public Message getAllCourses(String userId) {
        try {
            List<Course> allCourses = courseDAO.getAllCourses();
            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
            Set<String> selectedSessionIds = userSelections.stream()
                    .map(CourseSelection::getCourseId)
                    .collect(Collectors.toSet());

            //----------------------------------------------------------------------------------
            // 打印所有课程数据（在处理前）
            System.out.println("获取了所有的课程如下：");
            for (Course course : allCourses) {
                System.out.println("课程ID: " + course.getCourseId());
                System.out.println("课程名称: " + course.getCourseName());
                //System.out.println("课程类型: " + course.getType());
                System.out.println("开课学院: " + course.getDepartment());
                System.out.println("课程状态: " + course.getStatus());
                System.out.println("教学班数量: " + course.getSessions().size());
                System.out.println("教学班信息:");
                for (ClassSession session : course.getSessions()) {
                    System.out.println("  - 教学班ID: " + session.getSessionId());
                    //System.out.println("    教师: " + session.getTeacher());
                    //System.out.println("    时间地点: " + session.getSchedule());
                    System.out.println("    容量: " + session.getCapacity());
                    //System.out.println("    当前人数: " + session.getCurrentEnrollment());
                    System.out.println("    是否已选: " + session.isSelectedByStudent());
                }
                System.out.println("------------------------");
            }

            //----------------------------------------------------------------------------------

            for (Course course : allCourses) {
                boolean isCourseSelected = course.getSessions().stream()
                        .anyMatch(session -> selectedSessionIds.contains(session.getSessionId()));
                if (isCourseSelected) {
                    course.setStatus(CourseStatus.SELECTED);
                    course.getSessions().forEach(session -> {
                        if (selectedSessionIds.contains(session.getSessionId())) {
                            session.setSelectedByStudent(true);
                        }
                    });
                } else {
                    course.setStatus(CourseStatus.NOT_SELECTED);
                }
                course.setSessionnum(course.getSessions().size());
            }
            // 返回修正后的成功消息
            return Message.success(ActionType.GET_ALL_COURSES_RESPONSE, allCourses, "成功获取课程列表");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.GET_ALL_COURSES_RESPONSE, "获取课程列表时服务器出错");
        }
    }

    public Message selectCourse(String studentId, String sessionId) {
        try {
            if (courseDAO.isSessionFull(sessionId)) {
                // ⭐ 修正：使用 Message.failure
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：课程人数已满");
            }
            if (courseDAO.hasScheduleConflict(studentId, sessionId)) {
                // ⭐ 修正：使用 Message.failure
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：与已选课程时间冲突");
            }

            CourseSelection newSelection = new CourseSelection(studentId, sessionId, "已选");
            boolean success = courseDAO.addCourseSelection(newSelection);

            if(success) {
                // ⭐ 修正：使用 Message.success(ActionType, String)，因为这里不需要返回额外数据
                return Message.success(ActionType.SELECT_COURSE_RESPONSE, "选课成功！");
            } else {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败，数据库操作异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "处理选课时服务器出错");
        }
    }

    public Message dropCourse(String studentId, String sessionId) {
        try {
            boolean success = courseDAO.removeCourseSelection(studentId, sessionId);
            if(success) {
                // ⭐ 修正：使用 Message.success
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