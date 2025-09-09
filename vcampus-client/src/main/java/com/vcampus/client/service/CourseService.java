package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.Message;
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
    private final UserSession userSession;

    public CourseService() {
        this.socketClient = MainApp.getGlobalSocketClient();
        this.userSession = UserSession.getInstance();
    }

    //我想了一下这个是请求，也就是说客户端向服务端拉取课表（根据前端学生id)
    public Message getCourseTable() {
        // 我的想法是：从UserSession获取当前登录用户的ID，判断是不是null
        //如果用户登陆了，我还必须判断是否是学生，如果是学生才能执行此请求，否则又回传递拉取失败情况

        // 从UserSession获取当前登录用户的ID
        String userId = userSession.getCurrentUserId();
        // 检查用户是否已经登录
        if (userId == null || !userSession.isLoggedIn()) {
            return Message.failure(ActionType.GET_COURSE_TABLE, "用户未登录");
        }
        // 检查用户是否为学生角色
        Role userRole = userSession.getCurrentUserRole();
        if (userRole == null || userRole != Role.STUDENT) {
            return Message.failure(ActionType.GET_COURSE_TABLE, "只有学生才能查看课表");
        }
        // 创建消息，将用户ID作为数据传递
        Message message = new Message(ActionType.GET_COURSE_TABLE, null);
        return socketClient.sendMessage(message);
    }


    //还有一个请求，客户端向服务端请求所有课程（选课）-》我觉得可选课程不行而是应该所有课程，有些课程不能选，那也是所有课程的信息



    //还有一个功能，客户端向服务端请求退选课程
    public Message WithdrawCourse(String courseId) {
    //因为退课需要三思而后行，所以我们这里打算点两次，就是验证一次
    }



    //最后一个功能（应该是一个按钮，这个按钮有对应的课程ID），客户端向服务端请求选课程
    // 选课功能
    public Message enrollCourse(String courseId) {
        // 这里我也需要二次认证，三思而后行
        //我认为不需要经过服务器来，因为这只是一次确认罢了

        // 从UserSession获取当前登录用户的ID
        String userId = userSession.getCurrentUserId();
        //直接利用DTO类去传递消息
        CourseSelection courseSelection = new CourseSelection(userId,courseId,"选修中");

        // 创建消息
        Message message = new Message(ActionType.ENROLL_COURSE, courseSelection);
        return socketClient.sendMessage(message);
    }


    //一个功能：退课验证


    /**
     * 获取全局Socket客户端
     * @return SocketClient实例
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

     //调试用：
}
