package com.vcampus.server.controller;

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

    // 在服务端的 CourseController.java 中添加新方法:
    public Message handleSearchCourses(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String userId = (String) payload.get("userId");
        String keyword = (String) payload.get("keyword");
        return courseService.searchCourses(userId, keyword);
    }
}