package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.dao.impl.StudentLeaveApplicationDao;
import com.vcampus.server.service.StudentAdminService;
import com.vcampus.server.service.StudentService;

/**
 * 服务端 学生控制器
 * 负责处理来自客户端的学生相关请求
 * 编写人：周蔚钺
 */
public class StudentController {

    private final StudentService studentService;
    private final StudentAdminService studentAdminService;

    public StudentController() {
        this.studentService = new StudentService();
        this.studentAdminService=new StudentAdminService();
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

    /**
     * 处理学籍异动申请（休学/复学）
     */
    public Message handleStudentStatusApplication(Message message) {
        Message response = new Message();
        response.setAction(ActionType.STUDENT_STATUS_APPLICATION);

        try {
            Object data = message.getData();
            if (data instanceof StudentLeaveApplication application) {
                // 保存到数据库
                boolean success = studentService.saveApplication(application);
                response.setStatus(success);
                response.setMessage(success ? "申请提交成功" : "申请提交失败");
                response.setData(application);
            } else {
                response.setStatus(false);
                response.setMessage("无效数据类型");
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("处理申请时发生异常：" + e.getMessage());
        }

        return response;
    }

    public Message handleRevokeApplication(Message request) {
        try {
            if (request.getData() instanceof StudentLeaveApplication application) {
                // 更新申请状态为 "已撤回"
                application.setStatus("已撤回");

                boolean success = studentAdminService.updateApplicationStatus(
                        application.getApplicationId(), "已撤回");

                if (success) {
                    return new Message(
                            ActionType.REVOKE_APPLICATION,
                            application,
                            true,
                            "申请已成功撤回");
                } else {
                    return new Message(
                            ActionType.REVOKE_APPLICATION,
                            null,
                            false,
                            "撤回失败，申请不存在或已处理");
                }
            } else {
                return new Message(
                        ActionType.REVOKE_APPLICATION,
                        null,
                        false,
                        "请求数据格式错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(
                    ActionType.REVOKE_APPLICATION,
                    null,
                    false,
                    "服务器处理异常: " + e.getMessage());
        }
    }

}

