package com.vcampus.client.controller.courseAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.controller.MessageController;
import com.vcampus.client.service.courseAdmin.CourseAdminService;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class CourseAdminController implements IClientController {

    @FXML private VBox courseListContainer;
    @FXML private Button modifyButton;
    @FXML private Button deleteButton;
    @FXML private Button addCourseButton;
    // 1. 确保有这些 @FXML 注入
    @FXML private HBox searchBarContainer;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;

    private final CourseAdminService courseAdminService = new CourseAdminService();

    // --- 状态管理 ---
    private Object selectedItem = null; // 当前选中的对象（可能是 Course 或 ClassSession）
    private Node selectedNode = null;   // 当前选中的UI节点
    private final String selectedStyle = "-fx-background-color: #cce5ff;"; // 选中时的高亮样式

    @FXML
    public void initialize() {
        registerToMessageController();
        // 初始时禁用修改和删除按钮
        modifyButton.setDisable(true);
        deleteButton.setDisable(true);
        // 首次加载数据
        requestDataRefresh();
    }


    // 2. 确保有处理搜索按钮点击的事件方法
    @FXML
    private void handleSearchCourse() {
        String keyword = searchTextField.getText();
        System.out.println("UI: 正在发起课程搜索，关键词: '" + keyword + "'");

        // showLoadingIndicator(); // 可以在这里显示加载动画

        // 调用 Service 的搜索方法
        courseAdminService.searchCourses(keyword);
    }

    @Override
    public void registerToMessageController() {
        // 获取全局SocketClient中的MessageController
        com.vcampus.client.controller.MessageController messageController =
                courseAdminService.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setCourseAdminController(this);
        }
    }

    /**
     * 由子组件调用的公共方法，用于更新当前选中的项目
     * @param item 选中的数据对象 (Course 或 ClassSession)
     * @param node 选中的UI节点 (HBox)
     */
    public void setSelectedItem(Object item, Node node) {
        // 清除上一个选中项的样式
        if (selectedNode != null) {
            selectedNode.setStyle("");
        }
        // 设置新的选中项
        this.selectedItem = item;
        this.selectedNode = node;
        // 为新的选中项添加高亮
        if (selectedNode != null) {
            selectedNode.setStyle(selectedStyle);
        }
        // 更新顶部按钮的状态
        updateTopButtons();
    }

    /**
     * 处理“增加课程”按钮点击事件
     */
    @FXML
    private void handleAddCourse() {
        System.out.println("UI: “增加课程”按钮被点击，准备弹出新增窗口...");
        showCourseDialog(null); // 传入 null 代表是“新增”模式
    }

    /**
     * 处理顶部“修改”按钮点击事件
     */
    @FXML
    public void handleModify() {
        if (selectedItem instanceof Course) {
            showCourseDialog((Course) selectedItem);
        } else if (selectedItem instanceof com.vcampus.common.dto.ClassSession) {
         //这里暂时不做处理
        }
    }

    /**
     * ⭐ 核心实现：处理顶部“删除选中项”按钮的点击事件
     */
    @FXML
    public void handleDelete() {
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "操作无效", "请先点击选择一个课程或教学班。");
            return;
        }

        // 1. 根据选中项的类型，生成不同的确认信息
        String confirmationText;
        if (selectedItem instanceof Course) {
            confirmationText = "您确定要删除课程 " + ((Course) selectedItem).getCourseName() + " 吗？\n这将同时删除其下所有的教学班和选课记录！";
        } else if (selectedItem instanceof ClassSession) {
            confirmationText = "您确定要删除教学班 " + ((ClassSession) selectedItem).getSessionId() + " 吗？\n这将同时删除其所有选课记录！";
        } else {
            return;
        }

        // 2. 创建并显示一个确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmationText, ButtonType.YES, ButtonType.NO);
        alert.setTitle("确认删除操作");
        alert.setHeaderText("此操作不可恢复");
        Optional<ButtonType> result = alert.showAndWait();

        // 3. 检查用户的选择
        if (result.isPresent() && result.get() == ButtonType.YES) {
            // 4. 如果用户确认，则根据选中项类型调用对应的 Service 方法
            System.out.println("UI: 用户已确认删除，正在向服务器发送请求...");
            if (selectedItem instanceof Course) {
                courseAdminService.deleteCourse(((Course) selectedItem).getCourseId());
            } else if (selectedItem instanceof ClassSession) {
                courseAdminService.deleteSession(((ClassSession) selectedItem).getSessionId());
            }
        } else {
            System.out.println("UI: 用户取消了删除操作。");
        }
    }

    /**
     * 公共方法，用于刷新整个列表
     */
    public void requestDataRefresh() {
        System.out.println("[DEBUG] requestDataRefresh - 触发数据刷新");
        // TODO: 显示加载动画
        courseAdminService.getAllCoursesForAdmin();
    }

    /**
     * 响应从服务器返回的课程列表
     */
    public void handleGetAllCoursesResponse(Message message) {
        System.out.println("[DEBUG] handleGetAllCoursesResponse - 收到服务器响应: " + message.isSuccess());
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                List<Course> courses = (List<Course>) message.getData();
                populateUI(courses);
                
                // 如果课程列表为空，延迟重试刷新
                if (courses == null || courses.isEmpty()) {
                    System.out.println("[DEBUG] 课程数据为空，1秒后重试刷新");
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(this::requestDataRefresh);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        });
    }

    private void populateUI(List<Course> courses) {
        // 调试日志：跟踪课程数据和数量
        System.out.println("[DEBUG] populateUI - 课程数据: " + (courses != null ? courses.size() + "门课程" : "null"));
        courseListContainer.getChildren().clear();
        setSelectedItem(null, null); // 清除选中状态
        if (courses != null) {
            for (Course course : courses) {
                try {
                    URL resourceUrl = getClass().getResource("/fxml/admin/CourseAdminCard.fxml");
                    FXMLLoader loader = new FXMLLoader(resourceUrl);
                    Node courseCardNode = loader.load();
                    com.vcampus.client.controller.courseAdmin.CourseAdminCardController controller = loader.getController();
                    controller.setData(course, this); // 将总指挥的引用传递下去
                    courseListContainer.getChildren().add(courseCardNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateTopButtons() {
        boolean itemSelected = selectedItem != null;
        modifyButton.setDisable(!itemSelected);
        deleteButton.setDisable(!itemSelected);
    }

    // --- 弹出窗口管理 ---

    /**
     * 辅助方法：显示警告对话框
     * @param alertType 警告类型
     * @param title 对话框标题
     * @param content 对话框内容
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType, content);
        alert.setTitle(title);
        alert.setHeaderText(null); // 不显示头部文本
        alert.showAndWait();
    }

    /**
     * 弹出课程编辑/新增窗口
     * @param courseToEdit 要编辑的课程，如果为 null 则代表新增
     */
    public void showCourseDialog(Course courseToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/course/CourseDialog.fxml"));
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(courseToEdit == null ? "增加新课程" : "修改课程信息");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(courseListContainer.getScene().getWindow()); // 设置父窗口
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 将 Stage 和数据传递给弹窗的 Controller
            CourseDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.initData(courseToEdit);

            dialogStage.showAndWait();

            // 弹窗关闭后，主动请求刷新主列表
            requestDataRefresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSessionDialog(Course parentCourse, com.vcampus.common.dto.ClassSession sessionToEdit) {
        // TODO: 实现加载 SessionDialog.fxml, 创建新 Stage, 并显示
    }

}