package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.Teacher;
import com.vcampus.common.enums.ActionType;

import java.util.List;

/**
 * 管理员端学生信息服务类
 * 负责向服务端请求学生数据，并向 Controller 提供接口
 */
public class StudentAdminService {

    private final SocketClient socketClient;

    public StudentAdminService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }

    /**
     * 获取全局的 Socket 客户端，用于注册 Controller
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    /**
     * 查询所有学生信息
     */
    public void getAllStudents() {
        Message request = new Message(ActionType.ALL_STUDENT, null);
        socketClient.sendMessage(request);
    }

    /**
     * 根据姓名关键字模糊搜索学生
     * @param nameKeyword 姓名关键字
     */
    public void searchStudentsByName(String nameKeyword) {
        Message request = new Message(ActionType.SEARCH_STUDENT, nameKeyword);
        socketClient.sendMessage(request);
    }

    /**
     * 根据学号获取单个学生详细信息
     * @param studentId 学号
     */
    public void getStudentById(String studentId) {
        Message request = new Message(ActionType.INFO_STUDENT, studentId);
        socketClient.sendMessage(request);
    }

    /**
     * 处理服务端返回的学生列表
     * @param message 服务端返回消息
     * @return List<Student>，失败返回 null
     */
    @SuppressWarnings("unchecked")
    public List<Student> handleStudentListResponse(Message message) {
        if (message.isSuccess() && message.getData() != null) {
            return (List<Student>) message.getData();
        }
        return null;
    }

    /**
     * 处理服务端返回的单个学生信息
     * @param message 服务端返回消息
     * @return Student 对象，失败返回 null
     */
    public Student handleStudentInfoResponse(Message message) {
        if (message.isSuccess() && message.getData() != null) {
            return (Student) message.getData();
        }
        return null;
    }

    /**
     * 更新学生信息（管理员可用）
     * @param student 修改后的学生对象
     */
    public void updateStudent(Student student) {
        Message request = new Message(ActionType.UPDATE_STUDENT_ADMIN, student);
        socketClient.sendMessage(request);
    }

    /** 获取所有请假申请 */
    public void getAllApplications() {
        Message request = new Message(ActionType.GET_ALL_APPLICATIONS, null);
        socketClient.sendMessage(request);
    }

    /**
     * 更新请假申请的审核状态
     *
     * @param applicationId 申请的唯一ID
     * @param status        审核结果（已通过/未通过）
     */
    public void updateApplicationStatus(String applicationId, String status) {
        try {
            // 封装 data，这里简单传一个 Map，或者你可以定义 DTO
            var data = new java.util.HashMap<String, Object>();
            data.put("applicationId", applicationId);
            data.put("status", status);

            Message request = new Message(ActionType.UPDATE_APPLICATION_STATUS, data);

            // 发送到服务器
            Message response = socketClient.sendMessage(request);

            if (response == null || !response.isStatus()) {
                System.err.println("更新申请状态失败：" + (response != null ? response.getMessage() : "无响应"));
            } else {
                System.out.println("申请状态更新成功 -> ID=" + applicationId + " 状态=" + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStudents(List<Student> students) {
        Message message = new Message(ActionType.UPDATE_STUDENTS, students);
        getGlobalSocketClient().sendMessage(message);
    }

    public void getAllTeachers() {
        Message request = new Message(ActionType.ALL_TEACHER, null);
        socketClient.sendMessage(request);
    }

    /**
     * 发送更新教师请求
     */
    public void sendRequest(Teacher teacher) {
        Message request = new Message(ActionType.UPDATE_TEACHER, teacher);
        socketClient.sendMessage(request);
    }
}
