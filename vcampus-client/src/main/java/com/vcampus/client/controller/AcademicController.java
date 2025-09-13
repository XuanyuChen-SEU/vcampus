package com.vcampus.client.controller;

import com.vcampus.client.MainApp;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 最外层“三明治”结构的控制器：教务管理模块主控制器。 (最终修正版)
 */
public class AcademicController implements IClientController {

    @FXML private Pane contentPane;
    @FXML private Button timetableButton;
    @FXML private Button selectCoursesButton;

    // ⭐ 修正1：遵循 Java 命名规范，变量名以小写字母开头。
    private final CourseService courseService = new CourseService();
    private Button currentActiveButton;

    @FXML
    public void initialize() {
        System.out.println("教务模块已加载。");
        // 注册自己到总消息控制器，以便接收服务器响应
        registerToMessageController();
        // 默认显示选课界面并加载数据
        handleShowSelectCourses(null);
    }

    /**
     * 实现 IClientController 接口的注册方法。
     */
    @Override
    public void registerToMessageController() {
        // ⭐ 修正2：从应用主类(MainApp)获取全局 MessageController，而不是从 Service 获取
        com.vcampus.client.controller.MessageController messageController =
                courseService.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setAcademicController(this);
        } else {
            System.err.println("严重错误：教务Controller 注册失败，无法获取 MessageController 实例！");
        }
    }

    @FXML
    private void handleShowTimetable(ActionEvent event) {
        System.out.println("切换到'我的课表'视图");
        updateButtonStyles(timetableButton);
        contentPane.getChildren().clear();
        showPlaceholder("“我的课表”功能正在开发中...");
    }

    @FXML
    private void handleShowSelectCourses(ActionEvent event) {
        System.out.println("切换到'选课'视图");
        if (selectCoursesButton != null) {
            updateButtonStyles(selectCoursesButton);
        }
        requestCourseDataFromServer();
    }

    /**
     * [响应处理器 ①]：处理“获取所有课程”的响应
     */
    public void handleGetAllCoursesResponse(Message message) {
        Platform.runLater(() -> {
            hideLoadingIndicator();
            if (message.isSuccess()) {
                try {
                    List<Course> courses = (List<Course>) message.getData();
                    populateCourseList(courses);
                } catch (ClassCastException e) {
                    showError("客户端错误：无法解析服务器返回的课程数据。");
                    e.printStackTrace();
                }
            } else {
                showError("获取课程列表失败: " + message.getMessage());
            }
        });
    }

    /**
     * [响应处理器 ②]：处理“选课/退课”操作的最终结果
     */
    public void handleSelectOrDropCourseResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "操作成功", message.getMessage());
                // 操作成功后，重新请求数据以刷新整个界面，确保状态完全同步
                requestCourseDataFromServer();
            } else {
                showAlert(Alert.AlertType.ERROR, "操作失败", message.getMessage());
                // 即使操作失败，也刷新一次，以解除按钮的“处理中”状态
                requestCourseDataFromServer();
            }
        });
    }

    /**
     * 私有辅助方法，负责发起数据请求和显示加载动画。
     */
    private void requestCourseDataFromServer() {
        showLoadingIndicator();
        // ⭐ 修正3：通过“对象实例(courseService)”来调用它的“非静态方法”
        courseService.getAllSelectableCourses();
    }

    /**
     * 私有辅助方法，负责将课程数据动态加载成UI（串联“中间层”的核心）。
     */
    private void populateCourseList(List<Course> courses) {
        try {
            contentPane.getChildren().clear();
            VBox courseListContainer = new VBox();

            if (courses == null || courses.isEmpty()) {
                showPlaceholder("当前没有可选课程。");
                return;
            }

            for (Course course : courses) {
                // ⭐ 修正4：修复 FXML 路径中的拼写错误 (fxmll -> fxml)
                String fxmlPath = "/fxml/academic/CourseCard.fxml";

                URL resourceUrl = getClass().getResource(fxmlPath);
                if (resourceUrl == null) {
                    showError("客户端严重错误：找不到 CourseCard.fxml 文件！请检查路径：" + fxmlPath);
                    return;
                }

                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Node courseCardNode = loader.load();
                CourseCardController controller = loader.getController();
                controller.setData(course);
                courseListContainer.getChildren().add(courseCardNode);
            }

            ScrollPane scrollPane = new ScrollPane(courseListContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.getStyleClass().add("edge-to-edge");

            contentPane.getChildren().add(scrollPane);
        } catch (IOException e) {
            e.printStackTrace();
            showError("加载课程卡片UI失败：" + e.getMessage());
        }
    }

    /**
     * 私有辅助方法，更新导航按钮的样式。
     */
    private void updateButtonStyles(Button activeButton) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("active-tab-button");
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-tab-button");
            currentActiveButton = activeButton;
        }
    }

    // --- UI反馈辅助方法 ---
    private void showLoadingIndicator() {
        contentPane.getChildren().clear();
        ProgressIndicator pi = new ProgressIndicator();
        StackPane.setAlignment(pi, javafx.geometry.Pos.CENTER);
        contentPane.getChildren().add(pi);
    }

    private void hideLoadingIndicator() {
        contentPane.getChildren().removeIf(node -> node instanceof ProgressIndicator);
    }

    private void showPlaceholder(String text) {
        contentPane.getChildren().clear();
        Label placeholder = new Label(text);
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
        StackPane.setAlignment(placeholder, javafx.geometry.Pos.CENTER);
        contentPane.getChildren().add(placeholder);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();


    }
    // 添加缺失的showError方法
    private void showError(String errorMessage) {
        showAlert(Alert.AlertType.ERROR, "错误", errorMessage);
    }
}