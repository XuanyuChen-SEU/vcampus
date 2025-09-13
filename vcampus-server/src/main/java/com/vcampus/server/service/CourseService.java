//package com.vcampus.server.service;
//
//import com.vcampus.common.dao.ICourseDao;
//import com.vcampus.common.dto.ClassSession;
//import com.vcampus.common.dto.Course;
//import com.vcampus.common.dto.CourseSelection;
//import com.vcampus.common.dto.Message;
//import com.vcampus.common.enums.ActionType;
//import com.vcampus.server.dao.impl.FakeCourseDao;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * 服务端课程服务 (最终测试版)
// * 职责：
// * 1. 在构造时创建【您指定的】模拟数据。
// * 2. 将模拟数据注入到一个 FakeCourseDao 实例中。
// * 3. 包含所有业务逻辑，并通过 DAO 接口进行数据操作。
// */
//public class CourseService {
//
//    private final ICourseDao courseDAO;
//
//    public CourseService() {
//        FakeCourseDao fakeDao = new FakeCourseDao();
//        this.courseDAO = fakeDao;
//
//        List<Course> courseTable = new ArrayList<>();
//        List<CourseSelection> selectionTable = new ArrayList<>();
//
//        System.out.println("SERVICE: 正在创建【最终版】可交互的测试数据...");
//
//        // ⭐ 1. 用于测试【退课】功能
//        List<ClassSession> englishSessions = List.of(
//                new ClassSession("ENG_S01", "[01] 张老师", "1-16周 周二 3-4节", 60, 16, false)
//        );
//        courseTable.add(new Course("B17M0010", "大学英语II", "必修", "外国语学院", "NOT_SELECTED", englishSessions));
//
//        // ⭐ 2. 用于测试【选课】功能
//        List<ClassSession> seSessions = List.of(
//                new ClassSession("SE_S01", "[01] 刘老师", "1-16周 周一 5-6节", 50, 20, false)
//        );
//        courseTable.add(new Course("B08M4000", "软件工程", "限选", "计算机学院", "NOT_SELECTED", seSessions));
//
//        // ⭐ 3. 用于测试【选课失败 - 已满】
//        List<ClassSession> networkSessions = List.of(
//                new ClassSession("CS_S01", "[01] 王教授", "1-8周 周一 1-4节", 50, 50, false)
//        );
//        courseTable.add(new Course("B08M3000", "计算机网络", "必修", "计算机学院", (Object)"NOT_SELECTED", networkSessions));
//
//        // ⭐ 修正1：恢复学生的初始选课记录，用于测试“退课”功能
//       // selectionTable.add(new CourseSelection("1234567",  "ENG_S01", "已选"));
//
//        fakeDao.setCourseTable(courseTable);
//        fakeDao.setSelectionTable(selectionTable);
//        System.out.println("SERVICE: 模拟数据初始化完成。");
//
//        // 注入数据
//        fakeDao.setCourseTable(courseTable);
//        fakeDao.setSelectionTable(selectionTable);
//    }
//
//    // --- 业务逻辑方法 (使用 Message.success/failure 静态方法) ---
//
////    public Message getAllCourses(String userId) {
////        try {
////            List<Course> allCourses = courseDAO.getAllCourses();
////            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
////            Set<String> selectedSessionIds = userSelections.stream()
////                    .map(CourseSelection::getSessionId)
////                    .collect(Collectors.toSet());
////
////
////
////            // =========================================================
////            // ⭐ 新增：打印当前用户的选课记录 (您要求的功能)
////            // =========================================================
////            System.out.println("\n--- [服务端状态] 正在为用户 " + userId + " 计算课程状态 ---");
////            System.out.println("  该生当前的选课记录 (" + userSelections.size() + "条):");
////            if(userSelections.isEmpty()) {
////                System.out.println("    (无)");
////            } else {
////                for(CourseSelection selection : userSelections) {
////                    System.out.println("    - " + selection);
////                    System.out.println("选课的id " + selection.getSessionId());
////                    System.out.println("选课状态"+selection.getStatus());
////                }
////            }
////            System.out.println("-----------------------------------------------------");
////            //----------------------------------------------------------------------------------
////            // 打印所有课程数据（在处理前）
////            System.out.println("获取了所有的课程如下：");
////            for (Course course : allCourses) {
////                System.out.println("课程ID: " + course.getCourseId());
////                System.out.println("课程名称: " + course.getCourseName());
////                //System.out.println("课程类型: " + course.getType());
////                System.out.println("开课学院: " + course.getDepartment());
////                System.out.println("课程状态: " + course.getStatus());
////                System.out.println("教学班数量: " + course.getSessions().size());
////                System.out.println("教学班信息:");
////                for (ClassSession session : course.getSessions()) {
////                    System.out.println("  - 教学班ID: " + session.getSessionId());
////                    //System.out.println("    教师: " + session.getTeacher());
////                    //System.out.println("    时间地点: " + session.getSchedule());
////                    System.out.println("    容量: " + session.getCapacity());
////                    //System.out.println("    当前人数: " + session.getCurrentEnrollment());
////                    System.out.println("    是否已选: " + session.isSelectedByStudent());
////                }
////                System.out.println("------------------------");
////            }
////
////            //----------------------------------------------------------------------------------
////
////            for (Course course : allCourses) {
////                boolean isCourseSelected = course.getSessions().stream()
////                        .anyMatch(session -> selectedSessionIds.contains(session.getSessionId()));
////                System.out.println("课程ID: " + course.getCourseId());
////                System.out.println("课程名称: " + course.getCourseName());
////                System.out.println("课程状态: " + course.getStatus());
////                System.out.println("已选状态: " + isCourseSelected);
////                if (isCourseSelected) {
////                    course.setStatus(SELECTED);
////                    course.getSessions().forEach(session -> {
////                        if (selectedSessionIds.contains(session.getSessionId())) {
////                            session.setSelectedByStudent(true);
////                            System.out.println("班级名字"+session.getSessionId());
////                            System.out.println("班级状态"+session.isSelectedByStudent());
////                        }
////                    });
////                } else {
////                    course.setStatus(NOT_SELECTED);
////                }
////                course.setSessionnum(course.getSessions().size());
////            }
////            // 返回修正后的成功消息
////            return Message.success(ActionType.GET_ALL_COURSES_RESPONSE, allCourses, "成功获取课程列表");
////        } catch (Exception e) {
////            e.printStackTrace();
////            return Message.failure(ActionType.GET_ALL_COURSES_RESPONSE, "获取课程列表时服务器出错");
////        }
////    }
//public Message getAllCourses(String userId) {
//    try {
//        // 1. 从 DAO 获取“活”的课程数据对象。
//        // 因为我们现在操作的是 FakeDAO，所以这里得到的是指向内存中可变对象的引用列表。
//        List<Course> allCourses = courseDAO.getAllCourses();
//
//        // 2. 获取该学生的选课记录
//        List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
//        Set<String> selectedSessionIds = userSelections.stream()
//                .map(CourseSelection::getSessionId)
//                .collect(Collectors.toSet());
//
//        // ==================================================================
//        // ⭐ 新增：在这里打印出所有原始数据，进行追根溯源
//        // ==================================================================
//        System.out.println("\n----------- CourseService.getAllCourses 内部状态 -----------");
//        System.out.println(">>> 正在为用户 " + userId + " 处理数据...");
//
//        System.out.println("\n--- 1. 从DAO获取的该生选课记录 (Selection Table) ---");
//        if (userSelections.isEmpty()) {
//            System.out.println("    (该生当前无任何选课记录)");
//        } else {
//            userSelections.forEach(selection -> System.out.println("    - " + selection));
//        }
//
//        System.out.println("\n--- 2. 从DAO获取的所有课程 (Course Table) 原始状态 ---");
//        if (allCourses.isEmpty()) {
//            System.out.println("    (当前无任何课程信息)");
//        } else {
//            for (Course course : allCourses) {
//                System.out.println("  课程: " + course.getCourseName() + " (" + course.getCourseId() + ")"+"选课状态"+course.getStatus());
////                for(ClassSession session : course.getSessions()) {
////                    System.out.println(String.format(
////                            "    -> 教学班: %s (%s) | 容量: %d | 已选人数: %d",
////                            session.getTeacherName(),
////                            session.getSessionId(),
////                            session.getCapacity()
////                    ));
////                }
//            }
//        }
//        System.out.println("----------------------------------------------------------\n");
//        // ==================================================================
//
//
//        // 3. ⭐ 核心修正：直接在从 DAO 获取的这些“活”对象上计算并设置状态
//        //    我们不再创建任何副本，直接修改我们从“数据库”（FakeDAO）中取出的对象。
//        for (Course course : allCourses) {
//            boolean isCourseSelected = course.getSessions().stream()
//                    .anyMatch(session -> selectedSessionIds.contains(session.getSessionId()));
//
//            // 设置课程级别的状态 - 使用字符串代替枚举以避免序列化问题
//            course.setStatus(isCourseSelected ? "SELECTED" : "NOT_SELECTED"); // 注意：Course类的setStatus方法接受String类型
//
//            // 设置每个教学班级别的“学生是否已选”状态
//            course.getSessions().forEach(session ->
//                    session.setSelectedByStudent(selectedSessionIds.contains(session.getSessionId()))
//            );
//
//            // =========================================================
//            // ⭐ 新增：打印出本课程的最终计算结果 (您要求的功能)
//            // =========================================================
//            System.out.println("  [处理中] 课程: " + course.getCourseName() + " (" + course.getCourseId() + ")");
//            System.out.println("    -> 学生已选课程集合中是否包含本课的教学班? " + isCourseSelected);
//            System.out.println("    -> 因此，本课程的最终状态被设定为: " + course.getStatus());
//            //System.out.println("    -> 其下各教学班的“是否已选”状态被设定为:");
////            for(ClassSession session : course.getSessions()) {
////                System.out.println(String.format(
////                        "    -> 教学班: %s (%s) | 容量: %d | 已选人数: %d | 学生是否已选: %b",
////                        session.getTeacherName(),
////                        session.getSessionId(),
////                        session.getCapacity(),
////                        session.getEnrolledCount(), // 补上了这个缺失的参数
////                        session.isSelectedByStudent()
////                ));
////            }
//            System.out.println("  --------------------------------------------------");
//            // =========================================================
//        }
//
//        System.out.println("SERVICE: [修正后] 为用户 " + userId + " 计算了最新的课程状态，准备发送...");
//        return Message.success(ActionType.GET_ALL_COURSES_RESPONSE, allCourses, "成功获取课程列表");
//
//    } catch (Exception e) {
//        e.printStackTrace();
//        return Message.failure(ActionType.GET_ALL_COURSES_RESPONSE, "获取课程列表时服务器出错");
//    }
//}
//
//    public Message selectCourse(String studentId, String sessionId) {
////        try {
////            if (courseDAO.isSessionFull(sessionId)) {
////                // ⭐ 修正：使用 Message.failure
////                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：课程人数已满");
////            }
////            if (courseDAO.hasScheduleConflict(studentId, sessionId)) {
////                // ⭐ 修正：使用 Message.failure
////                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：与已选课程时间冲突");
////            }
////
////            CourseSelection newSelection = new CourseSelection(studentId, courseId,sessionId, "已选");
////            boolean success = courseDAO.addCourseSelection(newSelection);
////
////            if(success) {
////                // ⭐ 修正：使用 Message.success(ActionType, String)，因为这里不需要返回额外数据
////                return Message.success(ActionType.SELECT_COURSE_RESPONSE, "选课成功！");
////            } else {
////                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败，数据库操作异常");
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "处理选课时服务器出错");
////        }
//        try {
//            // 业务逻辑判断
//            if (courseDAO.isAlreadyEnrolled(studentId, sessionId)) {
//                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：您已选过此课程");
//            }
//            if (courseDAO.isSessionFull(sessionId)) {
//                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：课程人数已满");
//            }
//            if (courseDAO.hasScheduleConflict(studentId, sessionId)) {
//                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：与已选课程时间冲突");
//            }
//
//            // 调用 DAO 执行数据库操作
//            CourseSelection newSelection = new CourseSelection(studentId, sessionId, "已选");
//            boolean success = courseDAO.addCourseSelection(newSelection);
//
//            if(success) {
//                return Message.success(ActionType.SELECT_COURSE_RESPONSE, "选课成功！");
//            } else {
//                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败，数据库操作异常");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "处理选课时服务器出错");
//        }
//    }
//
//    public Message dropCourse(String studentId,String sessionId) {
////        try {
////            boolean success = courseDAO.removeCourseSelection(studentId, sessionId);
////            if(success) {
////                // ⭐ 修正：使用 Message.success
////                return Message.success(ActionType.DROP_COURSE_RESPONSE, "退课成功！");
////            } else {
////                return Message.failure(ActionType.DROP_COURSE_RESPONSE, "退课失败，您可能未选此课");
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            return Message.failure(ActionType.DROP_COURSE_RESPONSE, "处理退课时服务器出错");
////        }
//        try {
//            // ⭐ 只需调用 DAO 即可，DAO 内部会处理人数变更
//            boolean success = courseDAO.removeCourseSelection(studentId, sessionId);
//            if(success) {
//                return Message.success(ActionType.DROP_COURSE_RESPONSE, "退课成功！");
//            } else {
//                return Message.failure(ActionType.DROP_COURSE_RESPONSE, "退课失败，您可能未选此课");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Message.failure(ActionType.DROP_COURSE_RESPONSE, "处理退课时服务器出错");
//        }
//    }
//}
package com.vcampus.server.service;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.CourseStatus;
import com.vcampus.server.dao.impl.FakeCourseDao;

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

    /**
     * 构造函数。
     * 在当前的测试阶段，我们实例化一个 FakeCourseDao。
     * 当连接真实数据库时，这里可以替换为真实的 CourseDaoImpl。
     */
    public CourseService() {
        // 对于模拟测试，我们使用 FakeCourseDao
        this.courseDAO = new FakeCourseDao();
        System.out.println("SERVICE: CourseService 实例已创建，将使用 FakeCourseDao。");
    }


    /**
     * 获取指定学生的所有可选课程，并为该生动态计算课程状态。
     * @param userId 请求课程列表的学生ID
     * @return 包含处理后课程列表的 Message 对象
     */
    public Message getAllCourses(String userId) {
        try {
            // 1. 从 DAO 获取所有课程的“模板”数据
            List<Course> allCourses = courseDAO.getAllCourses();
            // 2. 从 DAO 获取该学生的所有选课记录
            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
            Set<String> selectedSessionIds = userSelections.stream()
                    .map(CourseSelection::getSessionId)
                    .collect(Collectors.toSet());

            // 3. 核心业务逻辑：为该学生动态计算每门课的状态
            for (Course course : allCourses) {
                boolean isCourseSelected = course.getSessions().stream()
                        .anyMatch(session -> selectedSessionIds.contains(session.getSessionId()));

                course.setStatus(isCourseSelected ? CourseStatus.SELECTED.toString() : CourseStatus.NOT_SELECTED.toString());

                // 同时设置每个教学班对该学生而言，是否被选中
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
            // --- 进行一系列业务规则检查 ---
            if (courseDAO.isAlreadyEnrolled(studentId, sessionId)) {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：您已选过此课程");
            }
            if (courseDAO.isSessionFull(sessionId)) {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：课程人数已满");
            }
            if (courseDAO.hasScheduleConflict(studentId, sessionId)) {
                return Message.failure(ActionType.SELECT_COURSE_RESPONSE, "选课失败：与已选课程时间冲突");
            }

            // 所有检查通过，调用DAO执行数据库操作
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
            // 直接调用 DAO 进行删除操作
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