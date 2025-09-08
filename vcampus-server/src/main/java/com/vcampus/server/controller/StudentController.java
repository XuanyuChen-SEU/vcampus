package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.StudentService;

/**
 * 服务端 学生控制器
 * 负责处理来自客户端的学生相关请求
 * 编写人：周蔚钺
 */
public class StudentController {

    private final StudentService studentService;

    public StudentController() {
        this.studentService = new StudentService();
    }

    /**
     * 处理客户端发来的消息
     */
    public Message handle(Message request) {
        ActionType action = request.getAction();
        Object data = request.getData();

        switch (action) {
            case INFO_STUDENT: {
                if (!(data instanceof String)) {
                    return Message.failure(ActionType.INFO_STUDENT, "参数错误，应为 userId(String)");
                }
                String userId = (String) data;
                Student student = studentService.getStudentById(userId);
                if (student != null) {
                    return Message.success(ActionType.INFO_STUDENT, student, "查询成功");
                } else {
                    return Message.failure(ActionType.INFO_STUDENT, "未找到该学生信息");
                }
            }

            default:
                return Message.failure(action, "不支持的学生操作: " + action);
        }
    }
}
