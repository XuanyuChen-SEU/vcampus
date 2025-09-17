package com.vcampus.client.service.courseAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端【教务管理员】服务
 * 负责发送所有与教务管理相关的请求 (增删改查)。
 */
public class CourseAdminService {

    private final SocketClient socketClient;

    public CourseAdminService() {
        this.socketClient = MainApp.getGlobalSocketClient();
    }

    private void sendRequest(ActionType action, Object data) {
        if (socketClient == null || !socketClient.isConnected() || !UserSession.getInstance().isLoggedIn()) {
            System.err.println("网络或登录状态异常，无法发送请求。");
            return;
        }
        Message request = new Message(action, data);
        socketClient.sendMessage(request);
    }

    // --- 查询 ---
    public void getAllCoursesForAdmin() {
        sendRequest(ActionType.ADMIN_GET_ALL_COURSES, UserSession.getInstance().getCurrentUserId());
    }

    /**
     * ⭐ 新增：向服务器发送【新增课程】的请求
     * @param course 包含了新课程信息的对象
     */
    public void addCourse(Course course) {
        System.out.println("Client Service: 正在发送“新增课程”请求...");
        sendRequest(ActionType.ADMIN_ADD_COURSE, course);
    }

    /**
     * ⭐ 新增：向服务器发送【修改课程】的请求
     * @param course 包含了修改后课程信息的对象
     */
    public void modifyCourse(Course course) {
        System.out.println("Client Service: 正在发送“修改课程”请求...");
        sendRequest(ActionType.ADMIN_MODIFY_COURSE, course);
    }

    /**
     * (管理员) 向服务器发送【删除课程】的请求
     * @param courseId 要删除的课程 ID
     */
    public void deleteCourse(String courseId) {
        System.out.println("Client Admin Service: 正在发送“删除课程”请求...");
        sendRequest(ActionType.ADMIN_DELETE_COURSE, courseId);
    }

    /**
     * ⭐ 新增：向服务器发送【搜索课程】的请求
     * @param keyword 搜索关键词
     */
    public void searchCourses(String keyword) {

        System.out.println("Client Admin Service: 正在发送“搜索课程”请求，关键词: '" + keyword + "'");

        // 我们将关键词和管理员ID一起发送，以便未来进行更复杂的权限校验
        Map<String, Object> payload = new HashMap<>();
        payload.put("adminId", UserSession.getInstance().getCurrentUserId());
        payload.put("keyword", keyword);

        // 使用 ADMIN_SEARCH_COURSES 动作类型
        sendRequest(ActionType.ADMIN_SEARCH_COURSES, payload);
    }


    // --- 教学班操作 ---
    public void addSession(ClassSession session) {
        // ClassSession DTO 内部应包含它所属的 courseId
        System.out.println("Client Service: 正在发送“新增教学班”请求...");
        sendRequest(ActionType.ADMIN_ADD_SESSION, session);
    }


    public void modifySession(ClassSession session) {
        // 输出ClassSession对象的各种属性
        System.out.println("ClassSession属性详情：");
        System.out.println("courseId: " + session.getCourseId());
        System.out.println("sessionId: " + session.getSessionId());
        System.out.println("teacherName: " + session.getTeacherName());
        System.out.println("scheduleInfo: " + session.getScheduleInfo());
        System.out.println("capacity: " + session.getCapacity());
        System.out.println("enrolledCount: " + session.getEnrolledCount());
        System.out.println("isSelectedByStudent: " + session.isSelectedByStudent());
        
        sendRequest(ActionType.ADMIN_MODIFY_SESSION, session);
    }

    public void deleteSession(String sessionId) {
        System.out.println("Client Service: 正在发送“删除教学班”请求... sessionId: " + sessionId);
        sendRequest(ActionType.ADMIN_DELETE_SESSION, sessionId);
        // 调试日志：跟踪请求发送后的刷新触发
        System.out.println("Client Service: 删除请求已发送，等待服务器响应...");
    }

    /**
     * 获取全局Socket客户端
     * @return SocketClient实例
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }
}