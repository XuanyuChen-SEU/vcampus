package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程相关服务类
 * 处理客户端的课程业务逻辑
 */
public class CourseService {
    private final SocketClient socketClient;
    //这是为了从前端的登陆系统获取信息
    //private final UserSession userSession;

    public CourseService() {
        this.socketClient = MainApp.getGlobalSocketClient();
        //this.userSession = UserSession.getInstance();
    }

    /**
     * 向服务器请求当前登录用户的可选课程列表。
     */
    public void getAllSelectableCourses() {
        if (!checkConnectionAndLogin()) return;

        // 创建一个 Map 作为数据载荷
        Map<String, Object> payload = new HashMap<>();
        // ⭐ 从 UserSession 获取当前用户ID，并放入 Map
        payload.put("userId", UserSession.getInstance().getCurrentUserId());

        // 使用这个 Map 作为 Message 的 data
        Message request = new Message(ActionType.GET_ALL_COURSES, payload);

        System.out.println("Service: 发送获取课程请求 -> " + request);
        socketClient.sendMessage(request);
    }

    /**
     * 请求选择一个教学班。
     * @param sessionId 要选择的教学班 ID
     */
    public void selectCourse(String sessionId) {
        if (!checkConnectionAndLogin()) return;

        // 创建一个 Map 作为数据载荷
        Map<String, Object> payload = new HashMap<>();
        // ⭐ 从 UserSession 获取当前用户ID，并放入 Map
        payload.put("userId", UserSession.getInstance().getCurrentUserId());
        // 放入本次操作的核心数据
        payload.put("sessionId", sessionId);

        // 使用这个 Map 作为 Message 的 data
        Message request = new Message(ActionType.SELECT_COURSE, payload);

        System.out.println("Service: 发送选课请求 -> " + request);
        socketClient.sendMessage(request);
    }

    /**
     * 请求退选一个教学班。
     * @param sessionId 要退选的教学班 ID
     */
    public void dropCourse(String sessionId) {
        if (!checkConnectionAndLogin()) return;

        // 创建一个 Map 作为数据载荷
        Map<String, Object> payload = new HashMap<>();
        // ⭐ 从 UserSession 获取当前用户ID，并放入 Map
        payload.put("userId", UserSession.getInstance().getCurrentUserId());
        // 放入本次操作的核心数据
        payload.put("sessionId", sessionId);

        // 使用这个 Map 作为 Message 的 data
        Message request = new Message(ActionType.DROP_COURSE, payload);

        System.out.println("Service: 发送退课请求 -> " + request);
        socketClient.sendMessage(request);
    }








    //我想了一下这个是请求，也就是说客户端向服务端拉取课表（根据前端学生id)
//    public Message getCourseTable() {
//        // 我的想法是：从UserSession获取当前登录用户的ID，判断是不是null
//        //如果用户登陆了，我还必须判断是否是学生，如果是学生才能执行此请求，否则又回传递拉取失败情况
//
//        // 从UserSession获取当前登录用户的ID
//        String userId = userSession.getCurrentUserId();
//        // 检查用户是否已经登录
//        if (userId == null || !userSession.isLoggedIn()) {
//            return Message.failure(ActionType.GET_COURSE_TABLE, "用户未登录");
//        }
//        // 检查用户是否为学生角色
//        Role userRole = userSession.getCurrentUserRole();
//        if (userRole == null || userRole != Role.STUDENT) {
//            return Message.failure(ActionType.GET_COURSE_TABLE, "只有学生才能查看课表");
//        }
//        // 创建消息，将用户ID作为数据传递
//        Message message = new Message(ActionType.GET_COURSE_TABLE, null);
//        return socketClient.sendMessage(message);
//    }


    //还有一个请求，客户端向服务端请求所有课程（选课）-》我觉得可选课程不行而是应该所有课程，有些课程不能选，那也是所有课程的信息



    //还有一个功能，客户端向服务端请求退选课程
//    public Message WithdrawCourse(String courseId) {
//    //因为退课需要三思而后行，所以我们这里打算点两次，就是验证一次
//
//
//    }



    //最后一个功能（应该是一个按钮，这个按钮有对应的课程ID），客户端向服务端请求选课程
    // 选课功能
//    public Message enrollCourse(String courseId) {
//        // 这里我也需要二次认证，三思而后行
//        //我认为不需要经过服务器来，因为这只是一次确认罢了
//
//        // 从UserSession获取当前登录用户的ID
//        String userId = userSession.getCurrentUserId();
//        //直接利用DTO类去传递消息
//        CourseSelection courseSelection = new CourseSelection(userId,courseId,"选修中");
//
//        // 创建消息
//        Message message = new Message(ActionType.ENROLL_COURSE, courseSelection);
//        return socketClient.sendMessage(message);
//    }


    //一个功能：退课验证

    //===================================================================================
    //辅助功能区
    /**
     * 核心辅助方法：将当前登录的用户信息附加到请求消息中。
     * @param request 准备发送给服务器的消息
     */
//    private void attachUserContext(Message request) {
//        // ⭐ 从您提供的 UserSession 单例中获取当前用户的ID
//        String currentUserId = userSession.getCurrentUserId();
//
//        // 创建一个临时的 User DTO，仅用于在 Message 中传递身份信息
//        User userContext = new User();
//        userContext.setCardNumber(currentUserId); // 假设 User DTO 中用 cardNumber 存储 ID
//
//        // 将包含用户ID的 User 对象设置到 Message 中
//        request.setUser(userContext);
//    }

    /**
     * 检查网络和登录状态的辅助方法
     */
    private boolean checkConnectionAndLogin() {
        if (socketClient == null || !socketClient.isConnected()) {
            System.err.println("网络未连接，操作无法执行。");
            return false;
        }
        if (!UserSession.getInstance().isLoggedIn()) {
            System.err.println("用户未登录，操作无法执行。");
            return false;
        }
        return true;
    }







    /**
     * 获取全局Socket客户端
     * @return SocketClient实例
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

     //调试用：
}
