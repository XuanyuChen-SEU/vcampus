package com.vcampus.client.controller.courseAdmin;

import com.vcampus.client.controller.courseAdmin.CourseAdminController;
import com.vcampus.client.service.courseAdmin.CourseAdminService;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ClassSessionAdminCardController {

    @FXML private HBox sessionInfoRow;
    @FXML private Label teacherLabel;
    @FXML private Label scheduleLabel;
    @FXML private Label capacityLabel;
    @FXML private Label enrolledLabel;

    private ClassSession session;
    private Course parentCourse;
    private CourseAdminController parentController; // 持有对总指挥的引用
    // ⭐ 1. 确保持有 CourseAdminService 的实例
    private final CourseAdminService courseAdminService = new CourseAdminService();
    /**
     * 注入数据和对父级 Controller 的引用
     */
    public void setData(ClassSession session, Course parentCourse, CourseAdminController parentController) {
        this.session = session;
        this.parentCourse = parentCourse;
        this.parentController = parentController;

        teacherLabel.setText(session.getTeacherName());
        scheduleLabel.setText(session.getScheduleInfo());
        capacityLabel.setText("容量: " + session.getCapacity());
        enrolledLabel.setText("已选: " + session.getEnrolledCount());
    }

    /**
     * 当这个教学班卡片被点击时，通知总指挥“我被选中了”
     */
    @FXML
    private void handleSelectSession() {
        parentController.setSelectedItem(session, sessionInfoRow);
    }

    /**
     * 处理针对本教学班的“修改”按钮点击事件
     */
//    @FXML
//    private void handleModifySession() {
//        System.out.println("请求修改教学班: " + session.getSessionId());
//        // 调用总指挥的弹出窗口方法
//        parentController.showSessionDialog(parentCourse, session);
//    }

    /**
     * 处理针对本教学班的“删除”按钮点击事件
     */
    @FXML
    private void handleDeleteSession() {
        if (session == null) return;

        System.out.println("UI: 请求删除教学班: " + session.getSessionId());

        // 2a. 创建并显示一个确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除操作");
        alert.setHeaderText("您确定要删除教学班 " + session.getTeacherName() + " (" + session.getSessionId() + ") 吗？");
        alert.setContentText("此操作将永久删除该教学班及其所有选课记录，且不可恢复。");

        Optional<ButtonType> result = alert.showAndWait();

        // 2b. 检查用户的选择
        if (result.isPresent() && result.get() == ButtonType.OK){
            // 2c. 如果用户点击了“确定”，则调用 Service 发送删除请求
            System.out.println("UI: 用户已确认删除，正在向服务器发送请求...");
            courseAdminService.deleteSession(session.getSessionId());
            
            // 删除成功后请求刷新界面
            System.out.println("UI: 删除请求已发送，请求刷新主列表...");
            this.parentController.requestDataRefresh();
        } else {
            System.out.println("UI: 用户取消了删除操作。");
        }
    }

    @FXML
    private void handleModifySession() {
        System.out.println("UI: “修改教学班”按钮被点击。正在准备弹出窗口...");

        // --- 这一整块都是纯粹的前端操作，没有网络通信 ---
        try {
            // 1. 创建 FXMLLoader 来加载弹窗的 FXML 文件
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/course/SessionDialog.fxml"));
            GridPane page = loader.load(); // VBox 或 GridPane 取决于您的 FXML 根元素

            // 2. 创建一个新的 Stage (窗口)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("修改教学班信息");
            dialogStage.initModality(Modality.WINDOW_MODAL); // 设置为模态窗口，会阻塞父窗口
            dialogStage.initOwner(this.teacherLabel.getScene().getWindow()); // 设置父窗口

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. 获取弹窗的 Controller
            SessionDialogController controller = loader.getController();

            // 4. 将这个窗口(Stage)和要编辑的数据(session)传递给弹窗的 Controller
            controller.setDialogStage(dialogStage);
            controller.initData(this.parentCourse, this.session); // 传入要编辑的对象

            // 5. 显示弹窗，并“等待”用户在弹窗中完成操作
            dialogStage.showAndWait();

            // --- 只有在弹窗关闭后，才需要通知后端刷新数据 ---
            System.out.println("UI: 弹窗已关闭，请求刷新主列表...");
            this.parentController.requestDataRefresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}