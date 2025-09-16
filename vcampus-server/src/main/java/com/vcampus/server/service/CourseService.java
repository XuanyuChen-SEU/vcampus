package com.vcampus.server.service;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.CourseStatus;
// 导入新的DAO实现类
import com.vcampus.server.dao.impl.CourseDao;
import com.vcampus.server.dao.impl.FakeCourseDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vcampus.common.enums.CourseStatus.SELECTED;

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
        //this.courseDAO = new CourseDao();
        //我先看一下我的固定模拟数据库
        this.courseDAO = new FakeCourseDao();
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

                course.setStatus(isCourseSelected ? SELECTED.toString() : CourseStatus.NOT_SELECTED.toString());

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

    public Message getMyCourses(String userId) {
//        try {
//            // 1. 从 DAO 获取所有课程的“模板”数据
//            List<Course> allCourses = courseDAO.getAllCourses();
//            // 2. 从 DAO 获取该学生的所有选课记录
//            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
//            Set<String> selectedSessionIds = userSelections.stream()
//                    .map(CourseSelection::getSessionId)
//                    .collect(Collectors.toSet());
//
//            // 3. ⭐ 核心区别：只筛选出被选中的课程
//            List<Course> selectedCourses = new ArrayList<>();
//            for (Course course : allCourses) {
//                // 筛选出至少有一个教学班被该生选中的课程
//                List<ClassSession> selectedSessions = course.getSessions().stream()
//                        .filter(session -> selectedSessionIds.contains(session.getSessionId()))
//                        .collect(Collectors.toList());
//
//                if (!selectedSessions.isEmpty()) {
//                    // 创建一个新的 Course 对象，只包含被选中的教学班
//                    Course selectedCourse = new Course(course); // 使用拷贝构造
//                    selectedCourse.setSessions(selectedSessions);
//                    selectedCourse.setStatus("SELECTED");
//                    selectedCourse.getSessions().forEach(s -> s.setSelectedByStudent(true));
//                    selectedCourses.add(selectedCourse);
//                }
//            }
//            return Message.success(ActionType.GET_MY_COURSES_RESPONSE, selectedCourses, "成功获取已选课程");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Message.failure(ActionType.GET_MY_COURSES_RESPONSE, "获取已选课程时服务器出错");
//        }
        try {
            List<Course> allCourses = courseDAO.getAllCourses();
            List<CourseSelection> userSelections = courseDAO.getSelectionsByStudentId(userId);
            Set<String> selectedSessionIds = userSelections.stream()
                    .map(CourseSelection::getSessionId)
                    .collect(Collectors.toSet());

            // 核心区别：只筛选出被选中的课程
            List<Course> selectedCourses = new ArrayList<>();
            for (Course course : allCourses) {
                List<ClassSession> selectedSessionsInThisCourse = course.getSessions().stream()
                        .filter(session -> selectedSessionIds.contains(session.getSessionId()))
                        .collect(Collectors.toList());

                if (!selectedSessionsInThisCourse.isEmpty()) {
                    Course selectedCourse = new Course(course); // 拷贝一份
                    selectedCourse.setSessions(selectedSessionsInThisCourse); // 只保留选中的教学班
                    selectedCourse.setStatus("SELECTED");
                    selectedCourse.getSessions().forEach(s -> s.setSelectedByStudent(true));
                    selectedCourses.add(selectedCourse);
                }
            }
            return Message.success(ActionType.GET_MY_COURSES_RESPONSE, selectedCourses, "成功获取已选课程");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.GET_MY_COURSES_RESPONSE, "获取已选课程时服务器出错");
        }
    }

    /**
     * ⭐ 新增：根据关键词搜索课程的业务逻辑
     * @param userId 发起搜索的学生ID
     * @param keyword 搜索关键词
     * @return 包含过滤后课程列表的 Message 对象
     */
    public Message searchCourses(String userId, String keyword) {
        // 1. 先调用现有方法，获取对该用户来说状态已经计算好的【完整】课程列表
        Message allCoursesMessage = getAllCourses(userId);
        if (allCoursesMessage.isFailure()) {
            return allCoursesMessage; // 如果获取失败，直接返回失败信息
        }

        List<Course> allCourses = (List<Course>) allCoursesMessage.getData();

        // 2. 如果关键词为空，则返回全部结果
        if (keyword == null || keyword.trim().isEmpty()) {
            return allCoursesMessage;
        }

        // 3. 在内存中进行不区分大小写的模糊搜索过滤
        String finalKeyword = keyword.trim().toLowerCase();
        List<Course> filteredCourses = allCourses.stream()
                .filter(course -> course.getCourseName().toLowerCase().contains(finalKeyword))
                .collect(Collectors.toList());

        // 4. ⭐ 关键：复用 GET_ALL_COURSES_RESPONSE 这个已有的响应类型，返回过滤后的结果
        return Message.success(ActionType.GET_ALL_COURSES_RESPONSE, filteredCourses, "搜索成功");
    }

    // --- ⭐ 管理员相关业务 ---

    public Message getAllCoursesForAdmin() {
        try {
            List<Course> allCourses = courseDAO.getAllCourses();
            return Message.success(ActionType.ADMIN_GET_ALL_COURSES_RESPONSE, allCourses, "成功获取所有课程");
        } catch (Exception e) {
            return Message.failure(ActionType.ADMIN_GET_ALL_COURSES_RESPONSE, "服务器获取课程列表失败");
        }
    }

    /**
     * ⭐ 新增：处理新增课程的业务逻辑
     * @param course 包含了新课程信息的 DTO
     * @return 表示操作结果的 Message 对象
     */
    public Message addCourse(Course course) {
        try {
            // --- 在这里可以添加业务规则检查 ---
            // 例如：检查课程ID是否已存在，这在DAO中实现更佳

            // 调用 DAO 执行数据持久化操作
            boolean success = courseDAO.addCourse(course);

            if (success) {
                return Message.success(ActionType.ADMIN_ADD_COURSE_RESPONSE, "课程 " + course.getCourseName() + " 添加成功！");
            } else {
                return Message.failure(ActionType.ADMIN_ADD_COURSE_RESPONSE, "添加失败，课程ID " + course.getCourseId() + " 可能已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.ADMIN_ADD_COURSE_RESPONSE, "服务器内部错误");
        }
    }

    /**
     * ⭐ 新增：处理修改课程的业务逻辑
     * @param course 包含了修改后课程信息的 DTO
     * @return 表示操作结果的 Message 对象
     */
    public Message modifyCourse(Course course) {
        try {
            // 调用 DAO 执行数据持久化操作
            boolean success = courseDAO.updateCourse(course);

            if (success) {
                return Message.success(ActionType.ADMIN_MODIFY_COURSE_RESPONSE, "课程 " + course.getCourseName() + " 修改成功！");
            } else {
                return Message.failure(ActionType.ADMIN_MODIFY_COURSE_RESPONSE, "修改失败，课程ID " + course.getCourseId() + " 可能不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.ADMIN_MODIFY_COURSE_RESPONSE, "服务器内部错误");
        }
    }

    public Message deleteCourse(String courseId) {
        boolean success = courseDAO.deleteCourse(courseId);
        return success ? Message.success(ActionType.ADMIN_DELETE_COURSE_RESPONSE, "课程删除成功")
                : Message.failure(ActionType.ADMIN_DELETE_COURSE_RESPONSE, "删除失败，课程不存在");
    }

    /**
     * ⭐ 新增：根据关键词搜索课程 (管理员视角)
     * @param keyword 搜索关键词
     * @return 包含过滤后课程列表的 Message 对象
     */
    public Message searchCoursesForAdmin(String keyword) {
        try {
            // 1. 先调用现有方法，获取完整的课程列表
            //    (对于管理员，我们不需要计算学生个人状态)
            List<Course> allCourses = courseDAO.getAllCourses();

            // 2. 如果关键词为空或只有空格，则返回全部结果
            if (keyword == null || keyword.trim().isEmpty()) {
                return Message.success(ActionType.ADMIN_GET_ALL_COURSES_RESPONSE, allCourses, "成功获取所有课程");
            }

            // 3. 在内存中进行不区分大小写的模糊搜索过滤
            String finalKeyword = keyword.trim().toLowerCase();
            List<Course> filteredCourses = allCourses.stream()
                    .filter(course -> course.getCourseName().toLowerCase().contains(finalKeyword))
                    .collect(Collectors.toList());

            // 4. ⭐ 关键：复用 ADMIN_GET_ALL_COURSES_RESPONSE 这个已有的响应类型
            //    这样客户端的 Controller 就可以用同一个方法来处理“获取全部”和“获取搜索结果”两种情况
            return Message.success(ActionType.ADMIN_GET_ALL_COURSES_RESPONSE, filteredCourses, "搜索成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.ADMIN_GET_ALL_COURSES_RESPONSE, "搜索课程时服务器出错");
        }
    }


     /* ⭐ 核心实现：处理新增教学班的业务逻辑，并创建响应
     * @param session 包含了新教学班信息的 DTO
     * @return 表示操作结果的 Message 对象
     */
    public Message addSession(ClassSession session) {
        try {
            // 1. 调用 DAO 执行数据持久化操作
            //    DAO 内部会进行业务校验（如父课程是否存在、ID是否重复）
            boolean success = courseDAO.addSession(session);

            // 2. ⭐ 根据 DAO 的返回结果，创建并返回最终的响应 Message
            if (success) {
                // 如果成功，创建一个成功的 Message
                // 这个 Message 将被一路返回，最终发送给客户端
                return Message.success(ActionType.ADMIN_ADD_SESSION_RESPONSE, "教学班 " + session.getSessionId() + " 添加成功！");
            } else {
                // 如果失败，创建一个失败的 Message
                return Message.failure(ActionType.ADMIN_ADD_SESSION_RESPONSE, "添加失败，父课程不存在或教学班ID已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果过程中出现意外异常，返回一个服务器内部错误
            return Message.failure(ActionType.ADMIN_ADD_SESSION_RESPONSE, "服务器内部错误");
        }
    }

    public Message modifySession(ClassSession session) {
        boolean success = courseDAO.updateSession(session);
        return success ? Message.success(ActionType.ADMIN_MODIFY_SESSION_RESPONSE, "教学班修改成功")
                : Message.failure(ActionType.ADMIN_MODIFY_SESSION_RESPONSE, "修改失败，教学班不存在");
    }

    /**
     * ⭐ 新增：处理删除教学班的业务逻辑
     * @param sessionId 要删除的教学班 ID
     * @return 表示操作结果的 Message 对象
     */
    public Message deleteSession(String sessionId) {
        try {
            // 直接调用 DAO 进行删除操作
            boolean success = courseDAO.deleteSession(sessionId);

            if (success) {
                return Message.success(ActionType.ADMIN_DELETE_SESSION_RESPONSE, "教学班 " + sessionId + " 删除成功！");
            } else {
                return Message.failure(ActionType.ADMIN_DELETE_SESSION_RESPONSE, "删除失败，未找到该教学班");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.ADMIN_DELETE_SESSION_RESPONSE, "服务器处理删除教学班请求时出错");
        }
    }

//    public Message updateCapacity(String sessionId, int newCapacity) {
//        // 在这里可以添加业务逻辑，比如检查 newCapacity 是否 > 已选人数
//        boolean success = courseDAO.updateSessionCapacity(sessionId, newCapacity);
//        if(success) {
//            return Message.success(ActionType.ADMIN_UPDATE_CAPACITY_RESPONSE, "课容量更新成功！");
//        } else {
//            return Message.failure(ActionType.ADMIN_UPDATE_CAPACITY_RESPONSE, "修改失败，未找到该教学班");
//        }
//    }

}