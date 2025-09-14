package com.vcampus.client.controller;

//import com.vcampus.client.model.Course;（这两个实际上并没有）
//import com.vcampus.client.model.ClassSession;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.scene.paint.Color;
/**
 * 中间层“三明治”结构的控制器：课程卡片（或称课程行）。
 *
 * 职责:
 * 1. 作为一个可展开/折叠的组件，显示一门课程的概要信息。
 * 2. 接收来自 AcademicController 的 Course 数据对象。
 * 3. 动态加载该课程下所有的 ClassSessionCard 子组件。
 * 4. 响应自身的点击事件（展开/折叠）和“课程详情”链接的点击事件。
 */
//中间层从上层接受数据和命令，再把数据和命令传递给下层
public class CourseCardController {

    //所有已知组件的联系
    @FXML private HBox headerRow;
    @FXML private Label courseIdLabel;
    @FXML private Hyperlink detailsLink;
    @FXML private Label courseNameLabel;
    @FXML private Label courseTypeLabel;
    @FXML private Label departmentLabel;
    @FXML private Label statusLabel;
    @FXML private Label sessionCountLabel;
    // 用于容纳并显示所有教学班卡片(ClassSessionCard)的容器
    @FXML private FlowPane sessionsContainer;

    // 持有该卡片所代表的课程数据
    private Course course;

    @FXML
    public void initialize() {
        // ⭐ 核心修正1：在组件初始化时，立即应用一次默认的字体颜色
        applyDefaultTextColors();
    }

    private void applyDefaultTextColors() {
        Color darkTextColor = Color.web("#333333"); // 定义一个标准的深灰色
        courseIdLabel.setTextFill(darkTextColor);
        courseNameLabel.setTextFill(darkTextColor);
        courseTypeLabel.setTextFill(darkTextColor);
        departmentLabel.setTextFill(darkTextColor);
        sessionCountLabel.setTextFill(darkTextColor);
        // 注意：statusLabel 的颜色由 updateStatusLabel 动态控制，所以这里不设置
    }


    /**
     * 公共入口方法，由上层控制器(AcademicController)调用。
     * 负责接收数据、填充UI，并串联加载最内层的教学班卡片。
     * @param course 该卡片需要显示的课程数据
     */
    public void setData(Course course) {
//        this.course = course;
//
//        // 1. 填充标题行（自身）的UI信息
//        courseIdLabel.setText(course.getCourseId());
//        courseNameLabel.setText(course.getCourseName());
//        courseTypeLabel.setText(course.getCourseType());
//        departmentLabel.setText(course.getDepartment());
//
//        int sessionCount = (course.getSessions() != null) ? course.getSessions().size() : 0;
//        sessionCountLabel.setText(String.valueOf(sessionCount));
//
//        // 根据课程状态更新状态标签的显示
//        updateStatusLabel(course);
//
//        // 2. 动态加载并填充下属的教学班卡片
//        loadSessionCards();
        // ⭐ 核心修正：添加“卫兵语句”(Guard Clause)来防止 null course
        if (course == null) {
            System.err.println("严重错误：试图用一个空的(null) Course 对象来设置课程卡片。该卡片将不被显示。");
            // 我们可以直接隐藏这张有问题的卡片，防止界面错乱
            if(headerRow != null) {
                headerRow.setVisible(false);
                headerRow.setManaged(false);
            }
            return; // 提前退出，不执行任何后续操作
        }

        // 增加调试信息：显示接收到的课程数据
        System.out.println("\n----------- 课程卡片收到数据 -----------");
        System.out.println("课程名称: " + course.getCourseName());
        System.out.println("课程ID: " + course.getCourseId());
        System.out.println("课程状态字符串: " + course.getStatus());
        System.out.println("-------------------------------------------\n");

        this.course = course;
        // ⭐ 每次都用最新的 course 对象数据来完全重置UI
        courseIdLabel.setText(course.getCourseId());
        courseNameLabel.setText(course.getCourseName());
        courseTypeLabel.setText(course.getCourseType());
        departmentLabel.setText(course.getDepartment());
        sessionCountLabel.setText(String.valueOf(course.getSessionnum()));
        updateStatusLabel(course);
        loadSessionCards();
    }

    /**
     * FXML中定义的标题行点击事件处理方法，用于展开/折叠教学班列表。
     */
    @FXML
    private void toggleSessions() {
        if (sessionsContainer.getChildren().isEmpty()) return;

        boolean isVisible = !sessionsContainer.isVisible();
        sessionsContainer.setVisible(isVisible);
        sessionsContainer.setManaged(isVisible);

        // ⭐ 2. 核心修正：不再使用 setStyle，而是分别控制背景和字体颜色
        if (isVisible) {
            // 设置展开时的淡蓝色背景
            headerRow.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 15 20;");
        } else {
            // 设置折叠时的透明背景
            headerRow.setStyle("-fx-background-color: transparent; -fx-padding: 15 20;");
        }

        // ⭐ 3. 强制设定所有相关 Label 的字体颜色为深色，覆盖任何默认主题样式
        // 无论展开还是折叠，都确保字体是深色的
        Color darkTextColor = Color.web("#333333");
        courseIdLabel.setTextFill(darkTextColor);
        courseNameLabel.setTextFill(darkTextColor);
        courseTypeLabel.setTextFill(darkTextColor);
        departmentLabel.setTextFill(darkTextColor);
        // statusLabel 由 updateStatusLabel 方法控制颜色，所以这里不设置
        sessionCountLabel.setTextFill(darkTextColor);
    }


    @FXML
    private void handleDetailsLinkClick(MouseEvent event) {
        System.out.println("点击了课程详情: " + course.getCourseId());
        event.consume();
    }

    /**
     * 私有辅助方法：加载并初始化所有的教学班卡片。
     */
    private void loadSessionCards() {
        // 先清空，防止重复加载
        sessionsContainer.getChildren().clear();

        if (course.getSessions() != null && !course.getSessions().isEmpty()) {
            for (ClassSession session : course.getSessions()) {
                try {
                    // 动态加载最内层的 FXML 文件
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/academic/ClassSessionCard.fxml"));
                    Node sessionCardNode = loader.load();

                    // 获取最内层 FXML 对应的控制器实例
                    ClassSessionCardController sessionController = loader.getController();

                    // 将教学班数据传递给最内层的控制器
                    sessionController.setData(session);

                    // 将加载好的教学班卡片UI添加到本层的 FlowPane 容器中
                    sessionsContainer.getChildren().add(sessionCardNode);
                } catch (IOException e) {
                    System.err.println("加载教学班卡片失败 for session: " + session.getSessionId());
                    e.printStackTrace();
                }
            }
        }
    }



    //处理前端逻辑
    //根据课程状态更新标签
    // 处理前端逻辑：根据课程状态更新标签
    private void updateStatusLabel(Course course) {
        // 安全检查
        if (course == null || course.getStatus() == null || course.getStatus().trim().isEmpty()) {
            statusLabel.setText("未知状态");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888;");
            return;
        }
        
        // 由于Course中的status已经是String类型，直接使用它进行比较
        String statusStr = course.getStatus().trim().toUpperCase(); // 标准化字符串以提高兼容性
        if ("SELECTED".equals(statusStr)) {
            statusLabel.setText("✔ 已选");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        } else if ("CONFLICT".equals(statusStr)) {
            statusLabel.setText("时间冲突");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #c62828;");
        } else if ("FULL".equals(statusStr)) {
            statusLabel.setText("选课已满");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ef6c00;");
        } else if ("NOT_SELECTED".equals(statusStr)) {
            statusLabel.setText("未选");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888;");
        } else {
            // 增加调试信息
            System.out.println("发现未知的课程状态字符串: " + statusStr);
            statusLabel.setText("未知状态");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888;");
        }
    }





}