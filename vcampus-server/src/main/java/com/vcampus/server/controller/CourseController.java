package com.vcampus.server.controller;

import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import com.vcampus.server.service.CourseService;

import java.util.Map;

public class CourseController {
    private final CourseService courseService;

    public CourseController() {
        this.courseService = new CourseService();
    }

    public Message handleGetAllCourses(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String userId = (String) payload.get("userId");
//        Object courseParam = payload.get("course");

        // 打印当前用户的选课记录
//        System.out.println("\n--- [客户端请求] 获取所有课程 ---");
//        System.out.println("  请求用户ID: " + userId);
//        System.out.println("  请求参数: " + payload);
//        System.out.println("  course参数值: " + courseParam);
        return courseService.getAllCourses(userId);
    }

    public Message handleSelectCourse(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String studentId = (String) payload.get("userId");
        String sessionId = (String) payload.get("sessionId");
        //String courseId = (String) payload.get("courseId");
        return courseService.selectCourse(studentId,sessionId);
    }

    public Message handleDropCourse(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String studentId = (String) payload.get("userId");
        String sessionId = (String) payload.get("sessionId");
        //String courseId = (String) payload.get("courseId");
        return courseService.dropCourse(studentId,sessionId);
    }

    public Message handleGetMyCourses(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String studentId = (String) payload.get("userId");
        return courseService.getMyCourses(studentId);
    }

 //    在服务端的 CourseController.java 中添加新方法:
    public Message handleSearchCourses(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String userId = (String) payload.get("userId");
        String keyword = (String) payload.get("keyword");
        return courseService.searchCourses(userId, keyword);
    }

    // --- ⭐ 新增：管理员请求处理 ---

    public Message handleGetAllCoursesAdmin(Message request) {
        // 管理员获取所有课程，通常不需要额外参数
        return courseService.getAllCoursesForAdmin();
    }

    /**
     * ⭐ 新增：处理“新增课程”的请求
     * @param request 包含新课程(Course) DTO 的消息
     * @return 包含操作结果的响应消息
     */
    public Message handleAddCourse(Message request) {
        // 1. 从消息中解析出 Course 对象
        Course courseToAdd = (Course) request.getData();

        // 2. 将任务直接传递给 Service 层
        return courseService.addCourse(courseToAdd);
    }

    /**
     * ⭐ 新增：处理“修改课程”的请求
     * @param request 包含修改后课程(Course) DTO 的消息
     * @return 包含操作结果的响应消息
     */
    public Message handleModifyCourse(Message request) {
        // 1. 从消息中解析出 Course 对象
        Course courseToModify = (Course) request.getData();

        // 2. 将任务直接传递给 Service 层
        return courseService.modifyCourse(courseToModify);
    }

    public Message handleDeleteCourse(Message request) {
        // 删除课程时，data部分是 courseId 字符串
        String courseId = (String) request.getData();
        return courseService.deleteCourse(courseId);
    }

    public Message handleAddSession(Message request) {
        // 增加教学班时，data部分是 ClassSession 对象
        ClassSession sessionToAdd = (ClassSession) request.getData();
        return courseService.addSession(sessionToAdd);
    }

    public Message handleModifySession(Message request) {
        ClassSession sessionToModify = (ClassSession) request.getData();
        return courseService.modifySession(sessionToModify);
    }

    public Message handleDeleteSession(Message request) {
        // 1. 从消息中解析出 sessionId (我们约定它直接作为 data 发送)
        String sessionId = (String) request.getData();

        // 2. 将任务直接传递给 Service 层
        return courseService.deleteSession(sessionId);
    }

    /**
     * ⭐ 新增：处理“管理员搜索课程”的请求
     * @param request 包含搜索关键词(keyword)的消息
     * @return 包含筛选后课程列表的响应消息
     */
    public Message handleAdminSearchCourses(Message request) {
        // 1. 从消息中解析出载荷 (Payload)
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String keyword = (String) payload.get("keyword");

        // 2. 将任务直接传递给 Service 层
        return courseService.searchCoursesForAdmin(keyword);
    }
}