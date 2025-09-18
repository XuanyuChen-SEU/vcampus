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

/**
 * TimetableController 是课表界面的控制器。
 * <p>
 * 主要功能：
 * <ul>
 *     <li>初始化课表网格，包括表头（周一到周五，节次时间）</li>
 *     <li>请求用户的课程数据并接收服务器返回结果</li>
 *     <li>将课程数据动态渲染为网格中的课程色块</li>
 *     <li>解析课程时间、节次和地点信息</li>
 * </ul>
 */
public class TimetableController implements IClientController {

    /** 用于显示课表的网格 */
    @FXML
    private GridPane timetableGrid;

    /** 课程服务，用于请求课表数据 */
    private final CourseService courseService = new CourseService();

    /** 星期数组，用于生成表头 */
    private final String[] weekDays = {"周一", "周二", "周三", "周四", "周五"};

    /** 节次时间数组，用于生成表头 */
    private final String[] timeSlots = {
            "1\n08:00\n08:45", "2\n08:50\n09:35", "3\n09:50\n10:35", "4\n10:40\n11:25",
            "5\n11:30\n12:15", "6\n14:00\n14:45", "7\n14:50\n15:35", "8\n15:50\n16:35",
            "9\n16:40\n17:25", "10\n18:30\n19:15", "11\n19:20\n20:05", "12\n20:10\n20:55",
            "13\n21:00\n21:45"
    };

    /**
     * 初始化方法，在界面加载时自动调用。
     * <p>
     * 功能：
     * <ul>
     *     <li>注册到 MessageController，接收服务器消息</li>
     *     <li>设置课表网格表头</li>
     *     <li>发送请求获取用户课表数据</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        System.out.println("[DEBUG] TimetableController initialize 方法被调用");
        registerToMessageController();
        setupGridHeaders();
        System.out.println("[DEBUG] 即将调用 courseService.getMyTimetable() 请求数据");
        courseService.getMyTimetable();
        System.out.println("[DEBUG] courseService.getMyTimetable() 请求已发送");
    }

    /**
     * 注册当前控制器到全局 MessageController，以便接收服务器消息。
     */
    @Override
    public void registerToMessageController() {
        try {
            com.vcampus.client.net.SocketClient socketClient = MainApp.getGlobalSocketClient();
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
     * 处理服务器返回的“我的课表”数据。
     *
     * @param message 服务器返回的消息对象，包含课表数据
     */
    public void handleMyTimetableResponse(Message message) {
        System.out.println("[DEBUG] handleMyTimetableResponse 被调用！");
        System.out.println("[DEBUG] 消息状态: " + message.isSuccess());
        System.out.println("[DEBUG] 消息内容: " + message.getData());

        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List) {
                System.out.println("[DEBUG] 成功解析到课程列表，共" + ((List) message.getData()).size() + "门课程");
                clearTimetableContent();
                List<Course> myCourses = (List<Course>) message.getData();
                populateGrid(myCourses);
            } else {
                System.out.println("[DEBUG] 数据不是List类型或请求失败: " + message.getData());
            }
        });
    }

    /**
     * 设置课表网格的表头，包括星期和节次时间。
     */
    private void setupGridHeaders() {
        for (int i = 0; i < weekDays.length; i++) {
            Label dayLabel = new Label(weekDays[i]);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            timetableGrid.add(dayLabel, i + 1, 0);
        }
        for (int i = 0; i < timeSlots.length; i++) {
            Label timeLabel = new Label(timeSlots[i]);
            timeLabel.setTextAlignment(TextAlignment.CENTER);
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setMaxWidth(Double.MAX_VALUE);
            timetableGrid.add(timeLabel, 0, i + 1);
        }
    }

    /**
     * 将课程数据填充到课表网格。
     *
     * @param courses 用户课程列表
     */
    private void populateGrid(List<Course> courses) {
        for (Course course : courses) {
            for (ClassSession session : course.getSessions()) {
                ScheduleDetail detail = parseScheduleInfo(session.getScheduleInfo());
                if (detail != null) {
                    createAndPlaceCourseBlock(course, session, detail);
                }
            }
        }
    }

    /**
     * 创建课程色块并放置到网格中。
     *
     * @param course 课程对象
     * @param session 课程节次对象
     * @param detail 解析后的时间和地点信息
     */
    private void createAndPlaceCourseBlock(Course course, ClassSession session, ScheduleDetail detail) {
        VBox courseBlock = new VBox();
        courseBlock.setAlignment(Pos.TOP_CENTER);
        courseBlock.getStyleClass().add("course-block");

        Label nameLabel = new Label(course.getCourseName());
        Label detailLabel = new Label(String.format("%d-%d节\n@%s", detail.startPeriod, detail.endPeriod, detail.location));
        nameLabel.setWrapText(true);
        detailLabel.setWrapText(true);

        courseBlock.getChildren().addAll(nameLabel, detailLabel);

        int rowSpan = detail.endPeriod - detail.startPeriod + 1;

        timetableGrid.add(courseBlock, detail.dayOfWeek, detail.startPeriod, 1, rowSpan);
    }

    /**
     * 从课程时间字符串解析出具体信息。
     * <p>
     * 示例输入："周二 3-4节 教四-102"
     *
     * @param scheduleInfo 原始课程时间地点字符串
     * @return 解析后的 ScheduleDetail 对象，如果解析失败返回 null
     */
    private ScheduleDetail parseScheduleInfo(String scheduleInfo) {
        Pattern pattern = Pattern.compile("周(.)\\s*(\\d+)-(\\d+)节\\s*(.+)");
        Matcher matcher = pattern.matcher(scheduleInfo);

        if (matcher.find()) {
            String dayStr = matcher.group(1);
            int start = Integer.parseInt(matcher.group(2));
            int end = Integer.parseInt(matcher.group(3));
            String loc = matcher.group(4);

            System.out.println("[DEBUG] 解析到课程时间地点：");
            System.out.println("  原始字符串：" + scheduleInfo);
            System.out.println("  星期：周" + dayStr);
            System.out.println("  节次：" + start + "-" + end + "节");
            System.out.println("  地点：" + loc);

            int dayIndex = "一二三四五".indexOf(dayStr) + 1;
            if (dayIndex > 0) {
                return new ScheduleDetail(dayIndex, start, end, loc);
            }
        } else {
            System.out.println("[DEBUG] 无法解析的时间格式：" + scheduleInfo);
        }
        return null;
    }

    /**
     * 清空课表中的课程色块，但保留表头。
     */
    private void clearTimetableContent() {
        timetableGrid.getChildren().removeIf(node -> node instanceof VBox);
    }

    /**
     * 内部类，用于存储解析后的课程时间和地点信息。
     */
    private static class ScheduleDetail {
        int dayOfWeek;   // 1=周一, 2=周二, ...
        int startPeriod; // 节次开始
        int endPeriod;   // 节次结束
        String location; // 上课地点

        public ScheduleDetail(int day, int start, int end, String loc) {
            this.dayOfWeek = day;
            this.startPeriod = start;
            this.endPeriod = end;
            this.location = loc;
        }
    }
}
