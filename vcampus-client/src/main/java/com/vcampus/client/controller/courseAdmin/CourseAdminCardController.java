package com.vcampus.client.controller.courseAdmin;

import com.vcampus.client.controller.courseAdmin.ClassSessionAdminCardController;
import com.vcampus.client.controller.courseAdmin.CourseAdminController;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class CourseAdminCardController {

    @FXML private HBox headerRow;
    @FXML private Label courseIdLabel;
    @FXML private Label courseNameLabel;
    @FXML private Label courseTypeLabel;
    @FXML private Label departmentLabel;
    @FXML private VBox sessionsContainer;

    private Course course;
    private CourseAdminController parentController; // 持有对总指挥的引用

    /**
     * 注入数据和对父级 Controller 的引用
     */
    public void setData(Course course, CourseAdminController parentController) {
        this.course = course;
        this.parentController = parentController;

        courseIdLabel.setText(course.getCourseId());
        courseNameLabel.setText(course.getCourseName());
        courseTypeLabel.setText(course.getCourseType());
        departmentLabel.setText(course.getDepartment());

        loadSessionCards();
    }

    /**
     * 当这门课程的标题行被点击时，通知总指挥“我被选中了”
     */
    @FXML
    private void handleSelectCourse() {
        parentController.setSelectedItem(course, headerRow);
    }

    /**
     * 处理“添加教学班”按钮的点击事件
     */
    @FXML
    private void handleAddSession() {
        System.out.println("UI: “添加教学班”按钮被点击，准备为课程 " + course.getCourseId() + " 弹出新增窗口...");
        // 调用下面的辅助方法，并传入 null，代表是“新增”模式
        showSessionDialog(null);
    }

    /**
     * 公共方法，用于弹出教学班编辑/新增窗口
     * @param sessionToEdit 要编辑的教学班，如果为 null 则代表新增
     */
    public void showSessionDialog(ClassSession sessionToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/course/SessionDialog.fxml"));
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(sessionToEdit == null ? "添加新教学班" : "修改教学班信息");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(headerRow.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 将 Stage 和数据传递给弹窗的 Controller
            SessionDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            // 关键：将父课程信息和要编辑的教学班信息（新增时为null）传进去
            controller.initData(this.course, sessionToEdit);

            // 显示窗口并“等待”用户在弹窗中完成操作
            dialogStage.showAndWait();

            // 弹窗关闭后，无论用户是否保存，都主动请求一次主列表刷新，以确保数据同步
            parentController.requestDataRefresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态加载并填充下属的教学班卡片
     */
    private void loadSessionCards() {
        sessionsContainer.getChildren().clear();
        if (course.getSessions() != null) {
            if (course.getSessions().isEmpty()) {
                // 如果没有教学班，添加一个提示标签
                Label emptyLabel = new Label("暂无教学班");
                emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 10;");
                sessionsContainer.getChildren().add(emptyLabel);
            } else {
                for (ClassSession session : course.getSessions()) {
                    try {
                        URL resourceUrl = getClass().getResource("/fxml/admin/ClassSessionAdminCard.fxml");
                        FXMLLoader loader = new FXMLLoader(resourceUrl);
                        Node sessionCardNode = loader.load();

                        ClassSessionAdminCardController controller = loader.getController();
                        // 将本教学班数据、所属课程数据、以及总指挥的引用，一同传递下去
                        controller.setData(session, this.course, this.parentController);

                        sessionsContainer.getChildren().add(sessionCardNode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // 如果sessions为null，也添加一个提示标签
            Label emptyLabel = new Label("暂无教学班");
            emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 10;");
            emptyLabel.setStyle("-fx-text-fill: #888; -fx-padding: 10;");
            sessionsContainer.getChildren().add(emptyLabel);
        }
    }
}