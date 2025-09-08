package com.vcampus.client.controller;

import com.vcampus.client.service.StudentService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * 学生信息控制器（适配 GridPane + Labels 布局）
 */
public class StudentController implements IClientController {

    @FXML private Label userIdLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label cardIdLabel;
    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private Label collegeLabel;
    @FXML private Label majorLabel;
    @FXML private Label gradeLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label nativePlaceLabel;
    @FXML private Label politicsStatusLabel;
    @FXML private Label studentStatusLabel;


    private final StudentService studentService = new StudentService();

    @FXML
    private void initialize() {
        // 注册到 MessageController
        registerToMessageController();

        // 加载当前登录用户信息
        loadCurrentStudentInfo();
    }

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController =
                studentService.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setStudentController(this);
        }
    }

    private void loadCurrentStudentInfo() {
        // 从全局用户会话获取当前用户ID
        String currentUserId = UserSession.getInstance().getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            showError("当前没有登录用户，请先登录！");
            return;
        }

        // 使用当前用户ID向服务端请求学生信息
        studentService.getStudentById(currentUserId);
    }

    /**
     * 处理服务端返回的学生信息响应
     */
    public void handleStudentInfoResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() != null) {
                Student student = (Student) message.getData();
                // 将学生信息填充到 GridPane 的 Labels 上
                userIdLabel.setText(student.getUserId());
                studentIdLabel.setText(student.getStudentId());
                cardIdLabel.setText(student.getCardId());
                nameLabel.setText(student.getName());
                genderLabel.setText(student.getGender());
                collegeLabel.setText(student.getCollege());
                majorLabel.setText(student.getMajor());
                gradeLabel.setText(String.valueOf(student.getGrade()));
                birthDateLabel.setText(student.getBirth_date());
                nativePlaceLabel.setText(student.getNative_place());
                politicsStatusLabel.setText(student.getPolitics_status());
                studentStatusLabel.setText(student.getStudent_status());

            } else {
                showError("加载学生信息失败：" + message.getMessage());
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("学生信息加载失败");
        alert.setContentText(message);
        alert.showAndWait();
    }
}


