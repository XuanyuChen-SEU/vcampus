package com.vcampus.client.controller;

import com.vcampus.client.MainApp;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimetableController implements IClientController {

    @FXML
    private GridPane timetableGrid;

    private final CourseService courseService = new CourseService();
    private final String[] weekDays = {"周一", "周二", "周三", "周四", "周五"};
    private final String[] timeSlots = {
            "1\n08:00\n08:45", "2\n08:50\n09:35", "3\n09:50\n10:35", "4\n10:40\n11:25",
            "5\n11:30\n12:15", "6\n14:00\n14:45", "7\n14:50\n15:35", "8\n15:50\n16:35",
            "9\n16:40\n17:25", "10\n18:30\n19:15", "11\n19:20\n20:05", "12\n20:10\n20:55",
            "13\n21:00\n21:45"
    };

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] TimetableController initialize 方法被调用");
        registerToMessageController();
        setupGridHeaders();
        System.out.println("[DEBUG] 即将调用 courseService.getMyTimetable() 请求数据");
        courseService.getMyTimetable(); // 请求数据
        System.out.println("[DEBUG] courseService.getMyTimetable() 请求已发送");
    }

    @Override
    public void registerToMessageController() {
        // 获取全局SocketClient中的MessageController
        try {
            com.vcampus.client.net.SocketClient socketClient = com.vcampus.client.MainApp.getGlobalSocketClient();
            if (socketClient != null) {
                com.vcampus.client.controller.MessageController messageController = socketClient.getMessageController();
                if (messageController != null) {
                    messageController.setTimetableController(this);
                    System.out.println("[DEBUG] TimetableController 已成功注册到 MessageController");
                } else {
                    System.err.println("[DEBUG] MessageController 为空，无法注册 TimetableController");
                }
            } else {
                System.err.println("[DEBUG] SocketClient 为空，无法获取 MessageController");
            }
        } catch (Exception e) {
            System.err.println("[DEBUG] 注册 TimetableController 到 MessageController 失败: " + e.getMessage());
        }
    }

    /**
     * 响应服务器返回的“我的课表”数据
     */
    public void handleMyTimetableResponse(Message message) {
        System.out.println("[DEBUG] handleMyTimetableResponse 被调用！");
        System.out.println("[DEBUG] 消息状态: " + message.isSuccess());
        System.out.println("[DEBUG] 消息内容: " + message.getData());

        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List) {
                System.out.println("[DEBUG] 成功解析到课程列表，共" + ((List)message.getData()).size() + "门课程");
                clearTimetableContent(); // 清空旧的课程色块
                List<Course> myCourses = (List<Course>) message.getData();
                populateGrid(myCourses);
            } else {
                System.out.println("[DEBUG] 数据不是List类型或请求失败: " + message.getData());
            }
        });
    }

    /**
     * 动态创建网格的表头
     */
    private void setupGridHeaders() {
        // 创建星期表头
        for (int i = 0; i < weekDays.length; i++) {
            Label dayLabel = new Label(weekDays[i]);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            timetableGrid.add(dayLabel, i + 1, 0);
        }
        // 创建节次和时间表头
        for (int i = 0; i < timeSlots.length; i++) {
            Label timeLabel = new Label(timeSlots[i]);
            timeLabel.setTextAlignment(TextAlignment.CENTER);
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setMaxWidth(Double.MAX_VALUE);
            timetableGrid.add(timeLabel, 0, i + 1);
        }
    }

    /**
     * 将课程数据填充到网格中
     */
    private void populateGrid(List<Course> courses) {
        for (Course course : courses) {
            for (ClassSession session : course.getSessions()) {
                // 解析每一条时间地点信息
                ScheduleDetail detail = parseScheduleInfo(session.getScheduleInfo());
                if (detail != null) {
                    createAndPlaceCourseBlock(course, session, detail);
                }
            }
        }
    }

    /**
     * 创建并放置一个课程色块
     */
    private void createAndPlaceCourseBlock(Course course, ClassSession session, ScheduleDetail detail) {
        VBox courseBlock = new VBox();
        courseBlock.setAlignment(Pos.TOP_CENTER);
        courseBlock.getStyleClass().add("course-block"); // 应用CSS样式

        Label nameLabel = new Label(course.getCourseName());
        Label detailLabel = new Label(String.format("%d-%d节\n@%s", detail.startPeriod, detail.endPeriod, detail.location));
        nameLabel.setWrapText(true);
        detailLabel.setWrapText(true);

        courseBlock.getChildren().addAll(nameLabel, detailLabel);

        int rowSpan = detail.endPeriod - detail.startPeriod + 1;

        // 将色块放置到网格的正确位置
        timetableGrid.add(courseBlock, detail.dayOfWeek, detail.startPeriod, 1, rowSpan);
    }

    /**
     * ⭐ 核心解析逻辑：从 "周二 3-4节 教四-102" 这样的字符串中提取信息
     */
    private ScheduleDetail parseScheduleInfo(String scheduleInfo) {
        // 使用正则表达式来匹配 "周X a-b节 地点" 格式
        Pattern pattern = Pattern.compile("周(.)\\s*(\\d+)-(\\d+)节\\s*(.+)");
        Matcher matcher = pattern.matcher(scheduleInfo);

        if (matcher.find()) {
            String dayStr = matcher.group(1);
            int start = Integer.parseInt(matcher.group(2));
            int end = Integer.parseInt(matcher.group(3));
            String loc = matcher.group(4);

            // 调试信息：打印解析出的时间和地点
            System.out.println("[DEBUG] 解析到课程时间地点：");
            System.out.println("  原始字符串：" + scheduleInfo);
            System.out.println("  星期：周" + dayStr);
            System.out.println("  节次：" + start + "-" + end + "节");
            System.out.println("  地点：" + loc);

            int dayIndex = "一二三四五".indexOf(dayStr) + 1;
            if(dayIndex > 0) {
                return new ScheduleDetail(dayIndex, start, end, loc);
            }
        } else {
            // 解析失败时也打印调试信息
            System.out.println("[DEBUG] 无法解析的时间格式：" + scheduleInfo);
        }
        return null; // 解析失败
    }

    private void clearTimetableContent() {
        // 移除所有 VBox 类型的节点（即课程色块），保留 Label（表头）
        timetableGrid.getChildren().removeIf(node -> node instanceof VBox);
    }

    // 一个私有的内部类，用于存储解析后的时间信息
    private static class ScheduleDetail {
        int dayOfWeek; // 1=周一, 2=周二, ...
        int startPeriod; // 1, 2, ...
        int endPeriod;
        String location;
        public ScheduleDetail(int day, int start, int end, String loc) {
            this.dayOfWeek = day;
            this.startPeriod = start;
            this.endPeriod = end;
            this.location = loc;
        }
    }
}