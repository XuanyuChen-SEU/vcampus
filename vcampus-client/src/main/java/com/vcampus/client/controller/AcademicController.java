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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AcademicController implements IClientController {

    // ⭐ 修正1：注入的不再是 Pane，而是 FXML 中 ScrollPane 内部的 VBox 容器
    @FXML
    private VBox courseListContainer;

    @FXML private Button timetableButton;
    @FXML private Button selectCoursesButton;

    // ⭐ 修正2：遵循 Java 命名规范，变量名以小写字母开头
    private final CourseService courseService = new CourseService();
    private Button currentActiveButton;

    @FXML
    public void initialize() {
        System.out.println("教务模块已加载。");
        registerToMessageController();
        handleShowSelectCourses(null);
    }

    @Override
    public void registerToMessageController() {
        // ⭐ 修正3：从应用主类(MainApp)获取全局 MessageController
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
        updateButtonStyles(timetableButton);
        // ⭐ 现在的操作目标是 courseListContainer
        courseListContainer.getChildren().clear();
        showPlaceholder("“我的课表”功能正在开发中...");
    }

    @FXML
    private void handleShowSelectCourses(ActionEvent event) {
        if (selectCoursesButton != null) {
            updateButtonStyles(selectCoursesButton);
        }
        requestCourseDataFromServer();
    }

    public void handleGetAllCoursesResponse(Message message) {
        Platform.runLater(() -> {
            hideLoadingIndicator();
            if (message.isSuccess() && message.getData() instanceof List) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Course> courses = (List<Course>) message.getData();
                    populateCourseList(courses);
                } catch (Exception e) {
                    showError("客户端错误：渲染课程UI失败。");
                    e.printStackTrace();
                }
            } else {
                showError("获取课程列表失败: " + message.getMessage());
            }
        });
    }

    public void handleSelectOrDropCourseResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "操作成功", message.getMessage());
            } else {
                showAlert(Alert.AlertType.ERROR, "操作失败", message.getMessage());
            }
            // 无论成功失败，都刷新列表以同步最新状态
            requestCourseDataFromServer();
        });
    }

    private void requestCourseDataFromServer() {
        showLoadingIndicator();
        courseService.getAllSelectableCourses();
    }

    /**
     * ⭐ 修正4：方法已大大简化，不再需要创建 VBox 和 ScrollPane
     */
    private void populateCourseList(List<Course> courses) {
        courseListContainer.getChildren().clear();
        if (courses == null || courses.isEmpty()) {
            showPlaceholder("当前没有可选课程。");
            return;
        }
        try {
            for (Course course : courses) {
                if(course == null) continue;
                URL resourceUrl = getClass().getResource("/fxml/academic/CourseCard.fxml");
                if (resourceUrl == null) {
                    showError("客户端致命错误：找不到 CourseCard.fxml 文件！");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Node courseCardNode = loader.load();
                CourseCardController controller = loader.getController();
                controller.setData(course);
                // ⭐ 直接将课程卡片添加到 FXML 中定义的 VBox 里
                courseListContainer.getChildren().add(courseCardNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("加载课程UI失败：" + e.getMessage());
        }
    }

    private void updateButtonStyles(Button activeButton) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("active-tab-button");
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-tab-button");
            currentActiveButton = activeButton;
        }
    }

    // ⭐ 修正5：让加载动画和提示信息也显示在 courseListContainer 中
    private void showLoadingIndicator() {
        courseListContainer.getChildren().clear();
        ProgressIndicator pi = new ProgressIndicator();
        courseListContainer.setAlignment(javafx.geometry.Pos.CENTER);
        courseListContainer.getChildren().add(pi);
    }

    private void hideLoadingIndicator() {
        // 当 populateCourseList 运行时，它会先 clear()，所以这个方法可以为空
        // 或者保留以防万一
        courseListContainer.getChildren().removeIf(node -> node instanceof ProgressIndicator);
        courseListContainer.setAlignment(javafx.geometry.Pos.TOP_LEFT); // 恢复默认对齐
    }

    private void showPlaceholder(String text) {
        courseListContainer.getChildren().clear();
        Label placeholder = new Label(text);
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
        courseListContainer.setAlignment(javafx.geometry.Pos.CENTER);
        courseListContainer.getChildren().add(placeholder);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String errorMessage) {
        showAlert(Alert.AlertType.ERROR, "错误", errorMessage);
    }
}