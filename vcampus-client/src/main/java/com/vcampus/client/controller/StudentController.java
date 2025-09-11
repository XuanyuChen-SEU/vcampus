package com.vcampus.client.controller;

import com.vcampus.client.service.StudentService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.vcampus.common.enums.ActionType;

import java.time.LocalDate;

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

    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private GridPane studentGridPane; // 必须绑定 FXML 中的 GridPane

    private final StudentService studentService = new StudentService();

    // 编辑控件
    private DatePicker birthDatePicker;
    private TextField nativePlaceField;
    private ComboBox<String> politicsComboBox;

    private String originalBirthDate;
    private String originalNativePlace;
    private String originalPoliticsStatus;

    @FXML
    private void initialize() {
        registerToMessageController();
        loadCurrentStudentInfo();

        editButton.setOnAction(event -> enterEditMode());
        saveButton.setOnAction(event -> saveChanges());
        cancelButton.setOnAction(event -> cancelEdit());
    }

    public void handleStudentInfoResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() != null) {
                Student student = (Student) message.getData();
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

    private void enterEditMode() {
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);

        // 保存原值，取消时恢复
        originalBirthDate = birthDateLabel.getText();
        originalNativePlace = nativePlaceLabel.getText();
        originalPoliticsStatus = politicsStatusLabel.getText();

        // 创建可编辑控件
        birthDatePicker = new DatePicker();
        if (!originalBirthDate.isEmpty()) {
            birthDatePicker.setValue(LocalDate.parse(originalBirthDate));
        }

        nativePlaceField = new TextField(originalNativePlace);

        politicsComboBox = new ComboBox<>();
        politicsComboBox.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        politicsComboBox.setValue(originalPoliticsStatus);

        // 替换 GridPane 上的 Label
        studentGridPane.getChildren().removeAll(birthDateLabel, nativePlaceLabel, politicsStatusLabel);
        studentGridPane.add(birthDatePicker, 8, 1); // column 8, row 1
        studentGridPane.add(nativePlaceField, 9, 1); // column 9, row 1
        studentGridPane.add(politicsComboBox, 10, 1); // column 10, row 1
    }

    private void saveChanges() {
        // 获取新值
        String newBirthDate = birthDatePicker.getValue() != null ? birthDatePicker.getValue().toString() : "";
        String newNativePlace = nativePlaceField.getText();
        String newPoliticsStatus = politicsComboBox.getValue();

        // 更新 Label 显示
        birthDateLabel.setText(newBirthDate);
        nativePlaceLabel.setText(newNativePlace);
        politicsStatusLabel.setText(newPoliticsStatus);

        // 构造完整 Student 对象
        Student updatedStudent = new Student();
        updatedStudent.setUserId(userIdLabel.getText());
        updatedStudent.setStudentId(studentIdLabel.getText());
        updatedStudent.setCardId(cardIdLabel.getText());
        updatedStudent.setName(nameLabel.getText());
        updatedStudent.setGender(genderLabel.getText());
        updatedStudent.setCollege(collegeLabel.getText());
        updatedStudent.setMajor(majorLabel.getText());
        updatedStudent.setGrade(Integer.parseInt(gradeLabel.getText()));
        updatedStudent.setBirth_date(newBirthDate);
        updatedStudent.setNative_place(newNativePlace);
        updatedStudent.setPolitics_status(newPoliticsStatus);
        updatedStudent.setStudent_status(studentStatusLabel.getText());

        // 调用 StudentService 更新数据库（服务端）
        studentService.updateStudentInfo(updatedStudent);

        // 退出编辑模式
        exitEditMode();
    }


    private void cancelEdit() {
        // 恢复原值
        birthDateLabel.setText(originalBirthDate);
        nativePlaceLabel.setText(originalNativePlace);
        politicsStatusLabel.setText(originalPoliticsStatus);

        exitEditMode();
    }

    private void exitEditMode() {
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        // 移除编辑控件，恢复 Label
        studentGridPane.getChildren().removeAll(birthDatePicker, nativePlaceField, politicsComboBox);
        studentGridPane.add(birthDateLabel, 8, 1);
        studentGridPane.add(nativePlaceLabel, 9, 1);
        studentGridPane.add(politicsStatusLabel, 10, 1);
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
        String currentUserId = UserSession.getInstance().getCurrentUserId();
        if (currentUserId != null && !currentUserId.isEmpty()) {
            studentService.getStudentById(currentUserId);
        } else {
            showError("当前没有登录用户，请先登录！");
        }
    }

    public void handleUpdateStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message == null) {
                showError("收到空消息");
                return;
            }

            if (message.getAction() != ActionType.UPDATE_STUDENT) {
                System.out.println("非学生更新消息，忽略: " + message.getAction());
                return;
            }

            if (message.isSuccess() && message.getData() instanceof Student) {
                Student updatedStudent = (Student) message.getData();

                // 更新界面 Label 显示
                birthDateLabel.setText(updatedStudent.getBirth_date());
                nativePlaceLabel.setText(updatedStudent.getNative_place());
                politicsStatusLabel.setText(updatedStudent.getPolitics_status());

                // 退出编辑模式
                exitEditMode();

                showInfo("学生信息更新成功");
            } else {
                showError("学生信息更新失败：" + message.getMessage());
            }
        });
    }

    private void showInfo(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }
}





