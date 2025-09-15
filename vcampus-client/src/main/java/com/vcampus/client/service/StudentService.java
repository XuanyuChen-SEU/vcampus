package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.enums.ActionType;

/**
 * 学生信息服务类
 * 负责向服务端请求学生数据，并向 Controller 提供接口
 */
public class StudentService {

    private final SocketClient socketClient;

    public StudentService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }

    /**
     * 获取全局的 Socket 客户端，用于注册 Controller
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    /**
     * 通过学号获取学生信息
     * @param userId 当前登录学生的学号 / 用户ID
     */
    public void getStudentById(String userId) {
        // 构造请求消息
        Message request = new Message(ActionType.INFO_STUDENT, userId);

        // 发送到服务端，服务端会返回学生信息
        socketClient.sendMessage(request);
    }

    /**
     * 更新学生信息
     * @param student 包含修改后的学生信息
     */
    public void updateStudentInfo(Student student) {
        // 构造请求消息，ActionType 可为 UPDATE_STUDENT（需在服务端定义对应处理逻辑）
        Message request = new Message(ActionType.UPDATE_STUDENT, student);

        // 发送到服务端，服务端处理更新并返回结果
        socketClient.sendMessage(request);
    }

    /**
     * 处理服务端返回的学生信息
     * @param message 服务端返回的消息
     * @return 返回 Student 对象，如果失败返回 null
     */
    public Student handleStudentInfoResponse(Message message) {
        if (message.isSuccess() && message.getData() != null) {
            return (Student) message.getData();
        }
        return null;
    }
}