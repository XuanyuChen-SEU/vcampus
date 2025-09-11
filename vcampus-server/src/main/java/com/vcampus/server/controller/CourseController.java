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
        return courseService.getAllCourses(userId);
    }

    public Message handleSelectCourse(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String studentId = (String) payload.get("userId");
        String sessionId = (String) payload.get("sessionId");
        return courseService.selectCourse(studentId, sessionId);
    }

    public Message handleDropCourse(Message request) {
        Map<String, Object> payload = (Map<String, Object>) request.getData();
        String studentId = (String) payload.get("userId");
        String sessionId = (String) payload.get("sessionId");
        return courseService.dropCourse(studentId, sessionId);
    }
}