package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.StudentAdminService;

import java.util.List;

/**
 * 服务端管理员学生信息控制器
 * 每种操作对应一个独立公开方法
 */
public class StudentAdminController {

    private final StudentAdminService studentAdminService;

    public StudentAdminController() {
        this.studentAdminService = new StudentAdminService();
    }

    /**
     * 获取所有学生信息
     */
    public Message getAllStudents(Message request) {
        try {
            List<Student> allStudents = studentAdminService.findAll();
            return Message.success(ActionType.ALL_STUDENT, allStudents, "所有学生信息获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.ALL_STUDENT, "服务端异常: " + e.getMessage());
        }
    }

    /**
     * 根据姓名关键字模糊搜索学生
     */
    public Message searchStudents(Message request) {
        try {
            Object data = request.getData();
            if (!(data instanceof String)) {
                return Message.failure(ActionType.SEARCH_STUDENT, "参数错误，必须是 String 对象");
            }
            String nameKeyword = (String) data;
            List<Student> filteredStudents = studentAdminService.findByNameLike(nameKeyword);
            return Message.success(ActionType.SEARCH_STUDENT, filteredStudents, "学生信息模糊搜索成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.SEARCH_STUDENT, "服务端异常: " + e.getMessage());
        }
    }

    /**
     * 根据学号获取单个学生详细信息
     */
    public Message getStudentById(Message request) {
        try {
            Object data = request.getData();
            if (!(data instanceof String)) {
                return Message.failure(ActionType.INFO_STUDENT, "参数错误，必须是 String 对象");
            }
            String userid = (String) data;
            Student student = studentAdminService.getStudentById(userid);
            if (student != null) {
                return Message.success(ActionType.INFO_STUDENT, student, "学生详细信息查找成功");
            } else {
                return Message.failure(ActionType.INFO_STUDENT, "未找到该学生信息");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.INFO_STUDENT, "服务端异常: " + e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    public Message updateStudent(Message request) {
        try {
            Student updatedStudent=(Student)request.getData();
            boolean ok = studentAdminService.updateStudentInfo(updatedStudent);
            if (ok) {
                return Message.success(ActionType.UPDATE_STUDENT_ADMIN, "更新成功");
            } else {
                return Message.failure(ActionType.UPDATE_STUDENT_ADMIN, "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.UPDATE_STUDENT_ADMIN, "服务端异常: " + e.getMessage());
        }
    }

}

