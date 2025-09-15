package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
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

}
