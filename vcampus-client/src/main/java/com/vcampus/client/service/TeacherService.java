package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Teacher;
import com.vcampus.client.session.UserSession;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.enums.ActionType;

public class TeacherService {
    private final SocketClient socketClient;

    public TeacherService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }

    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    /**
     * 获取当前登录教师的信息（发送消息给服务器获取）
     * @return Teacher对象，如果获取失败返回null
     */
    public Teacher getCurrentTeacher() {
        try {
            // 获取当前登录教师的userId
            String currentUserId = UserSession.getInstance().getCurrentUserId();
            if (currentUserId == null || currentUserId.isEmpty()) {
                return null;
            }

            // 创建消息请求
            Message request = new Message(ActionType.INFO_TEACHER,currentUserId);

            // 发送请求（异步或同步）
            socketClient.sendMessage(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 返回null，由回调处理实际数据
    }

    public void UpdateTeacher(Teacher teacher) {
        Message request = new Message(ActionType.UPDATE_TEACHER_INFO, teacher);
        socketClient.sendMessage(request);
    }
}
