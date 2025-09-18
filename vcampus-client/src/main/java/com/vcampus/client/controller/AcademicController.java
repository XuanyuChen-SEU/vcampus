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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.scene.control.TextField; // ⭐ 1. 确保导入 TextField

public class AcademicController implements IClientController {

    // ⭐ 修正1：注入的不再是 Pane，而是 FXML 中 ScrollPane 内部的 VBox 容器
    @FXML
    private VBox courseListContainer;

    @FXML private Button timetableButton;
    @FXML private Button selectCoursesButton;
    // ⭐ 2. 新增 FXML 注入，用于引用我们创建的页眉
    @FXML
    private HBox courseListHeader;

    // ⭐ 修正2：遵循 Java 命名规范，变量名以小写字母开头
    private final CourseService courseService = new CourseService();
    private Button currentActiveButton;

    // --- ⭐ 2. 新增 FXML 注入 ---
    // 注入 FXML 中定义的搜索栏相关控件
    @FXML private HBox searchBarContainer;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;

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

    // 在 AcademicController.java 的 handleShowTimetable 方法中
    @FXML
    private void handleShowMyTimetable(ActionEvent event) {
        updateButtonStyles(timetableButton);
        courseListHeader.setVisible(false);
        courseListHeader.setManaged(false); // 让页眉不占据布局空间
        searchBarContainer.setVisible(false);
        searchBarContainer.setManaged(false);
        try {
            courseListContainer.getChildren().clear();

            // ⭐ 加载我们新的时间表视图
            URL resourceUrl = getClass().getResource("/fxml/academic/TimetableView.fxml");
            Node timetableView = FXMLLoader.load(resourceUrl);

            courseListContainer.getChildren().add(timetableView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowTimetable(ActionEvent event) {
        // ⭐ 3. 隐藏页眉
        courseListHeader.setVisible(false);
        courseListHeader.setManaged(false); // 让页眉不占据布局空间
        searchBarContainer.setVisible(false);
        searchBarContainer.setManaged(false);
        updateButtonStyles(timetableButton);
        try {
            courseListContainer.getChildren().clear();

            String fxmlPath = "/fxml/academic/MyTimetableView.fxml";
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) { showError("..."); return; }

            // ⭐ 关键：加载 FXML，但暂时不获取 Controller
            Node myTimetableView = FXMLLoader.load(resourceUrl);

            // ⭐ 我们不需要手动获取 MyTimetableController 并注册，
            //    因为它在自己的 initialize() 方法里已经完成了自我注册！

            courseListContainer.getChildren().add(myTimetableView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("加载课表界面失败。");
        }
    }

    @FXML
    private void handleShowSelectCourses(ActionEvent event) {
        if (selectCoursesButton != null) {
            updateButtonStyles(selectCoursesButton);
        }

        // ⭐ 4. 显示页眉
        courseListHeader.setVisible(true);
        courseListHeader.setManaged(true);
        searchBarContainer.setVisible(true);
        searchBarContainer.setManaged(true);
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

    /**
     * ⭐ 5. 新增：处理搜索按钮的点击事件
     */
    @FXML
    private void handleSearchCourse(ActionEvent event) {
        String keyword = searchTextField.getText();
        System.out.println("UI: 正在发起课程搜索，关键词: '" + keyword + "'");

        showLoadingIndicator();

        // 如果搜索框为空，则请求所有课程（相当于重置）
        if (keyword == null || keyword.trim().isEmpty()) {
            courseService.getAllSelectableCourses();
        } else {
            // 否则，调用 Service 的新搜索方法
            courseService.searchCourses(keyword);
        }
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

    // ⭐ 专门修复课表视图样式的方法
    private void fixTimetableViewStyles(Node rootNode) {
        // 1. 修复根节点
        rootNode.setStyle("-fx-background-color: white;");

        // 2. 查找并修复 TabPane
        rootNode.lookupAll(".tab-pane").forEach(node -> {
            node.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5;"
            );
        });

        // 3. ⭐ Tab 头部区域 - 设置为灰色背景
        rootNode.lookupAll(".tab-header-area").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #f0f0f0; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 0 1 0;"
            );
        });

        // 4. ⭐ 每个 Tab - 设置为灰色背景
        rootNode.lookupAll(".tab").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #f0f0f0; " +
                "-fx-text-fill: black; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1 1 0 1; " +
                "-fx-border-radius: 5 5 0 0; " +
                "-fx-background-radius: 5 5 0 0;"
            );
        });

        // 5. ⭐ 选中的 Tab
        rootNode.lookupAll(".tab:selected").forEach(node -> {
            node.setStyle(
                "-fx-background-color: white; " +
                "-fx-text-fill: black; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 1 1 0 1; " +
                "-fx-background-radius: 5 5 0 0; " +
                "-fx-border-radius: 5 5 0 0;"
            );
        });

        // 6. Tab 内容区域
        rootNode.lookupAll(".tab-content-area").forEach(node -> {
            node.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-width: 0 1 1 1; " +
                "-fx-border-radius: 0 0 5 5; " +
                "-fx-background-radius: 0 0 5 5;"
            );
        });

        // 7. ⭐ TableView 基本样式
        rootNode.lookupAll(".table-view").forEach(node -> {
            if (node instanceof TableView) {
                TableView<?> tableView = (TableView<?>) node;

                tableView.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 3; " +
                    "-fx-table-cell-border-color: #e0e0e0;"
                );

                tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
                tableView.setMinWidth(1200);

                tableView.getColumns().forEach(column -> {
                    if (column.getPrefWidth() < 80) {
                        column.setPrefWidth(100);
                    }
                    column.setMinWidth(80);
                });
            }
        });

        // 8. ⭐ 关键：修复表格列头样式
        rootNode.lookupAll(".column-header").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #f8f9fa; " +  // 浅灰色背景
                "-fx-text-fill: #333333; " +         // 深色文字，确保可见
                "-fx-border-color: #dee2e6; " +
                "-fx-border-width: 0 1 1 0; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13px; " +
                "-fx-alignment: center;"
            );
        });

        // 9. ⭐ 修复表格列头背景
        rootNode.lookupAll(".column-header-background").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #dee2e6;"
            );
        });

        // 10. ⭐ 修复表格列头区域
        rootNode.lookupAll(".table-header-row").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #dee2e6; " +
                "-fx-border-width: 0 0 1 0;"
            );
        });

        // 11. ⭐ 修复表格行
        rootNode.lookupAll(".table-row-cell").forEach(node -> {
            node.setStyle(
                "-fx-background-color: white; " +
                "-fx-text-fill: #333333; " +         // 确保行文字也是深色
                "-fx-border-color: #f0f0f0; " +
                "-fx-border-width: 0 0 1 0;"
            );
        });

        // 12. ⭐ 修复表格单元格
        rootNode.lookupAll(".table-cell").forEach(node -> {
            node.setStyle(
                "-fx-text-fill: #333333; " +         // 确保单元格文字是深色
                "-fx-border-color: #f0f0f0; " +
                "-fx-border-width: 0 1 0 0; " +
                "-fx-padding: 8px;"
            );
        });

        // 13. ⭐ 修复选中行
        rootNode.lookupAll(".table-row-cell:selected").forEach(node -> {
            node.setStyle(
                "-fx-background-color: #e3f2fd; " +  // 浅蓝色选中背景
                "-fx-text-fill: #333333; " +
                "-fx-border-color: #2196f3;"
            );
        });

        // 14. 修复所有 Label
        rootNode.lookupAll(".label").forEach(node -> {
            if (node.getStyle() == null || node.getStyle().isEmpty()) {
                node.setStyle("-fx-text-fill: #333333;");
            }
        });

        // 15. ⭐ 修复 ScrollPane
        rootNode.lookupAll(".scroll-pane").forEach(node -> {
            node.setStyle(
                "-fx-background-color: white; " +
                "-fx-border-color: transparent;"
            );
        });

        // 16. ⭐ 额外处理：强制刷新表格样式
        Platform.runLater(() -> {
            rootNode.lookupAll(".table-view").forEach(node -> {
                if (node instanceof TableView) {
                    TableView<?> table = (TableView<?>) node;
                    table.refresh(); // 强制刷新表格

                    // 再次确保列头样式
                    table.lookupAll(".column-header").forEach(header -> {
                        header.setStyle(
                            "-fx-background-color: #f8f9fa; " +
                            "-fx-text-fill: #333333; " +
                            "-fx-border-color: #dee2e6; " +
                            "-fx-border-width: 0 1 1 0; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 13px; " +
                            "-fx-alignment: center;"
                        );
                    });
                }
            });
        });
    }
}