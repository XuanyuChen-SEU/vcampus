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

        // 只处理 INFO_STUDENT 操作
        if (action == ActionType.INFO_STUDENT) {
            if (!(data instanceof String)) {
                return Message.failure(ActionType.INFO_STUDENT, "参数错误，应为 userId(String)");
            }
            String userId = (String) data;

            // 调用 StudentService 获取学生信息
            return studentService.getStudentById(userId);
        }

        // 其他操作一律返回不支持
        return Message.failure(action, "不支持的学生操作: " + action);
    }

    public Message updateStudent(Message request) {
        try {
            // 从 Message 中获取 Student 对象
            Object data = request.getData();
            if (!(data instanceof Student)) {
                return Message.failure(ActionType.UPDATE_STUDENT, "参数错误，必须是 Student 对象");
            }

            Student student = (Student) data;

            if (student.getUserId() == null || student.getUserId().isEmpty()) {
                return Message.failure(ActionType.UPDATE_STUDENT, "用户ID不能为空");
            }

            // 调用 StudentService 更新数据库
            boolean result = studentService.updateStudent(student);

            if (result) {
                return Message.success(ActionType.UPDATE_STUDENT, student, "学生信息更新成功");
            } else {
                return Message.failure(ActionType.UPDATE_STUDENT, "学生信息更新失败");
            }

        } catch (Exception e) {
            System.err.println("更新学生信息过程中发生异常: " + e.getMessage());
            return Message.failure(ActionType.UPDATE_STUDENT, "服务器内部错误");
        }
    }

}

