package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Teacher;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.TeacherService;

/**
 * 服务端 教师控制器
 * 负责处理来自客户端的教师相关请求
 */
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController() {
        this.teacherService = new TeacherService();
    }

    /**
     * 处理教师信息查询请求
     */
    public Message handleInfoTeacher(Message request) {
        Object data = request.getData();
        if (!(data instanceof String teacherId) || teacherId.isBlank()) {
            return Message.failure(ActionType.INFO_TEACHER, "参数错误，应为非空 teacherId(String)");
        }

        Teacher teacher = teacherService.getTeacherById(teacherId);
        if (teacher != null) {
            return Message.success(ActionType.INFO_TEACHER, teacher, "教师信息查询成功");
        } else {
            return Message.failure(ActionType.INFO_TEACHER, "未找到教师信息");
        }
    }
    public Message updateTeacher(Message request) {
        try {
            if (!(request.getData() instanceof Teacher updatedTeacher)) {
                return Message.failure(ActionType.UPDATE_TEACHER, "参数错误，必须是教师对象");
            }
            boolean ok = teacherService.updateTeacher(updatedTeacher);
            if (ok) {
                return Message.success(ActionType.UPDATE_TEACHER, "更新教师成功");
            } else {
                return Message.failure(ActionType.UPDATE_TEACHER, "更新教师失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.UPDATE_TEACHER, "服务端异常: " + e.getMessage());
        }
    }

}
