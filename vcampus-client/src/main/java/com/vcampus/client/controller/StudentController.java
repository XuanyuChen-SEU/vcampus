package com.vcampus.client.controller;

import com.vcampus.client.service.StudentService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.common.enums.ActionType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class StudentController implements IClientController {

    // === 基本信息 ===
    @FXML private Label userIdLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private Label collegeLabel;
    @FXML private Label majorLabel;
    @FXML private Label gradeLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label nativePlaceLabel;
    @FXML private Label politicsStatusLabel;
    @FXML private Label studentStatusLabel;

    // === 联系方式 ===
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dormAddressLabel;

    // === 父亲信息 ===
    @FXML private Label fatherNameLabel;
    @FXML private Label fatherPhoneLabel;
    @FXML private Label fatherPoliticsLabel;
    @FXML private Label fatherWorkLabel;

    // === 母亲信息 ===
    @FXML private Label motherNameLabel;
    @FXML private Label motherPhoneLabel;
    @FXML private Label motherPoliticsLabel;
    @FXML private Label motherWorkLabel;

    // === 按钮 ===
    @FXML private Button editOrSaveButton;       // 修改/保存
    @FXML private Button pdfOrCancelButton;      // 休学/复学申请按钮

    // === 各区块 GridPane ===
    @FXML private GridPane studentGridPane;
    @FXML private GridPane contactGridPane;
    @FXML private GridPane fatherGridPane;
    @FXML private GridPane motherGridPane;

    private final StudentService studentService = new StudentService();

    // === 编辑模式下的输入控件 ===
    private DatePicker birthDatePicker;
    private TextField nativePlaceField;
    private ComboBox<String> politicsComboBox;

    private TextField phoneField;
    private TextField emailField;
    private TextField dormAddressField;

    private TextField fatherNameField;
    private TextField fatherPhoneField;
    private ComboBox<String> fatherPoliticsCombo;
    private TextField fatherWorkField;

    private TextField motherNameField;
    private TextField motherPhoneField;
    private ComboBox<String> motherPoliticsCombo;
    private TextField motherWorkField;

    // === 保存原始值 ===
    private String originalBirthDate;
    private String originalNativePlace;
    private String originalPoliticsStatus;

    private String originalPhone;
    private String originalEmail;
    private String originalDormAddress;

    private String originalFatherName;
    private String originalFatherPhone;
    private String originalFatherPolitics;
    private String originalFatherWork;

    private String originalMotherName;
    private String originalMotherPhone;
    private String originalMotherPolitics;
    private String originalMotherWork;

    private boolean editing = false;

    @FXML
    private void initialize() {
        registerToMessageController();
        loadCurrentStudentInfo();

        editOrSaveButton.setOnAction(event -> {
            if (!editing) enterEditMode();
            else saveChanges();
        });

        pdfOrCancelButton.setOnAction(event -> {
            if (!editing) { // 学籍异动申请
                showStudentStatusApplicationDialog();
            } else { // 编辑模式下取消
                cancelEdit();
            }
        });
    }

    // =================== 进入编辑模式 ===================
    private void enterEditMode() {
        editing = true;

        // 按钮切换
        editOrSaveButton.setText("保存");
        editOrSaveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 6;");
        pdfOrCancelButton.setText("取消");
        pdfOrCancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");

        // 保存原始值
        originalBirthDate = safeText(birthDateLabel);
        originalNativePlace = safeText(nativePlaceLabel);
        originalPoliticsStatus = safeText(politicsStatusLabel);

        originalPhone = safeText(phoneLabel);
        originalEmail = safeText(emailLabel);
        originalDormAddress = safeText(dormAddressLabel);

        originalFatherName = safeText(fatherNameLabel);
        originalFatherPhone = safeText(fatherPhoneLabel);
        originalFatherPolitics = safeText(fatherPoliticsLabel);
        originalFatherWork = safeText(fatherWorkLabel);

        originalMotherName = safeText(motherNameLabel);
        originalMotherPhone = safeText(motherPhoneLabel);
        originalMotherPolitics = safeText(motherPoliticsLabel);
        originalMotherWork = safeText(motherWorkLabel);

        // === 基本信息 ===
        birthDatePicker = new DatePicker();
        birthDatePicker.setStyle("-fx-pref-height: 26px;");
        if (!originalBirthDate.isEmpty()) {
            birthDatePicker.setValue(LocalDate.parse(originalBirthDate));
        }
        nativePlaceField = new TextField(originalNativePlace);
        nativePlaceField.setStyle("-fx-pref-height: 26px;");
        politicsComboBox = new ComboBox<>();
        politicsComboBox.setStyle("-fx-pref-height: 26px;");
        politicsComboBox.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        politicsComboBox.setValue(originalPoliticsStatus.isEmpty() ? "群众" : originalPoliticsStatus);

        studentGridPane.getChildren().removeAll(birthDateLabel, nativePlaceLabel, politicsStatusLabel);
        studentGridPane.add(birthDatePicker, 3, 3);
        studentGridPane.add(nativePlaceField, 1, 4);
        studentGridPane.add(politicsComboBox, 3, 4);

        // === 联系方式 ===
        phoneField = new TextField(originalPhone);
        phoneField.setStyle("-fx-pref-height: 26px;");
        emailField = new TextField(originalEmail);
        emailField.setStyle("-fx-pref-height: 26px;");
        dormAddressField = new TextField(originalDormAddress);
        dormAddressField.setStyle("-fx-pref-height: 26px;");
        contactGridPane.getChildren().removeAll(phoneLabel, emailLabel, dormAddressLabel);
        contactGridPane.add(phoneField, 1, 0);
        contactGridPane.add(emailField, 3, 0);
        contactGridPane.add(dormAddressField, 1, 1);

        // === 父亲信息 ===
        fatherNameField = new TextField(originalFatherName);
        fatherNameField.setStyle("-fx-pref-height: 26px;");
        fatherPhoneField = new TextField(originalFatherPhone);
        fatherPhoneField.setStyle("-fx-pref-height: 26px;");
        fatherPoliticsCombo = new ComboBox<>();
        fatherPoliticsCombo.setStyle("-fx-pref-height: 26px;");
        fatherPoliticsCombo.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        fatherPoliticsCombo.setValue(originalFatherPolitics.isEmpty() ? "群众" : originalFatherPolitics);
        fatherWorkField = new TextField(originalFatherWork);
        fatherWorkField.setStyle("-fx-pref-height: 26px;");
        fatherGridPane.getChildren().removeAll(fatherNameLabel, fatherPhoneLabel, fatherPoliticsLabel, fatherWorkLabel);
        fatherGridPane.add(fatherNameField, 1, 0);
        fatherGridPane.add(fatherPhoneField, 3, 0);
        fatherGridPane.add(fatherPoliticsCombo, 1, 1);
        fatherGridPane.add(fatherWorkField, 3, 1);

        // === 母亲信息 ===
        motherNameField = new TextField(originalMotherName);
        motherNameField.setStyle("-fx-pref-height: 26px;");
        motherPhoneField = new TextField(originalMotherPhone);
        motherPhoneField.setStyle("-fx-pref-height: 26px;");
        motherPoliticsCombo = new ComboBox<>();
        motherPoliticsCombo.setStyle("-fx-pref-height: 26px;");
        motherPoliticsCombo.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        motherPoliticsCombo.setValue(originalMotherPolitics.isEmpty() ? "群众" : originalMotherPolitics);
        motherWorkField = new TextField(originalMotherWork);
        motherWorkField.setStyle("-fx-pref-height: 26px;");
        motherGridPane.getChildren().removeAll(motherNameLabel, motherPhoneLabel, motherPoliticsLabel, motherWorkLabel);
        motherGridPane.add(motherNameField, 1, 0);
        motherGridPane.add(motherPhoneField, 3, 0);
        motherGridPane.add(motherPoliticsCombo, 1, 1);
        motherGridPane.add(motherWorkField, 3, 1);
    }

    // =================== 保存修改 ===================
    private void saveChanges() {
        String newBirthDate = birthDatePicker.getValue() != null ? birthDatePicker.getValue().toString() : "";
        String newNativePlace = nativePlaceField.getText();
        String newPoliticsStatus = politicsComboBox.getValue();

        String newPhone = phoneField.getText();
        String newEmail = emailField.getText();
        String newDormAddress = dormAddressField.getText();

        String newFatherName = fatherNameField.getText();
        String newFatherPhone = fatherPhoneField.getText();
        String newFatherPolitics = fatherPoliticsCombo.getValue();
        String newFatherWork = fatherWorkField.getText();

        String newMotherName = motherNameField.getText();
        String newMotherPhone = motherPhoneField.getText();
        String newMotherPolitics = motherPoliticsCombo.getValue();
        String newMotherWork = motherWorkField.getText();

        // 更新标签文本
        birthDateLabel.setText(newBirthDate);
        nativePlaceLabel.setText(newNativePlace);
        politicsStatusLabel.setText(newPoliticsStatus);

        phoneLabel.setText(newPhone);
        emailLabel.setText(newEmail);
        dormAddressLabel.setText(newDormAddress);

        fatherNameLabel.setText(newFatherName);
        fatherPhoneLabel.setText(newFatherPhone);
        fatherPoliticsLabel.setText(newFatherPolitics);
        fatherWorkLabel.setText(newFatherWork);

        motherNameLabel.setText(newMotherName);
        motherPhoneLabel.setText(newMotherPhone);
        motherPoliticsLabel.setText(newMotherPolitics);
        motherWorkLabel.setText(newMotherWork);

        Student updatedStudent = new Student();
        updatedStudent.setUserId(userIdLabel.getText());
        updatedStudent.setStudentId(studentIdLabel.getText());
        updatedStudent.setName(nameLabel.getText());
        updatedStudent.setGender(genderLabel.getText());
        updatedStudent.setCollege(collegeLabel.getText());
        updatedStudent.setMajor(majorLabel.getText());
        updatedStudent.setGrade(Integer.parseInt(gradeLabel.getText()));
        updatedStudent.setBirth_date(newBirthDate);
        updatedStudent.setNative_place(newNativePlace);
        updatedStudent.setPolitics_status(newPoliticsStatus);
        updatedStudent.setStudent_status(studentStatusLabel.getText());
        updatedStudent.setPhone(newPhone);
        updatedStudent.setEmail(newEmail);
        updatedStudent.setDormAddress(newDormAddress);
        updatedStudent.setFatherName(newFatherName);
        updatedStudent.setFatherPhone(newFatherPhone);
        updatedStudent.setFatherPoliticsStatus(newFatherPolitics);
        updatedStudent.setFatherWorkUnit(newFatherWork);
        updatedStudent.setMotherName(newMotherName);
        updatedStudent.setMotherPhone(newMotherPhone);
        updatedStudent.setMotherPoliticsStatus(newMotherPolitics);
        updatedStudent.setMotherWorkUnit(newMotherWork);

        studentService.updateStudentInfo(updatedStudent);
    }

    // =================== 取消编辑 ===================
    private void cancelEdit() {
        birthDateLabel.setText(originalBirthDate);
        nativePlaceLabel.setText(originalNativePlace);
        politicsStatusLabel.setText(originalPoliticsStatus);

        phoneLabel.setText(originalPhone);
        emailLabel.setText(originalEmail);
        dormAddressLabel.setText(originalDormAddress);

        fatherNameLabel.setText(originalFatherName);
        fatherPhoneLabel.setText(originalFatherPhone);
        fatherPoliticsLabel.setText(originalFatherPolitics);
        fatherWorkLabel.setText(originalFatherWork);

        motherNameLabel.setText(originalMotherName);
        motherPhoneLabel.setText(originalMotherPhone);
        motherPoliticsLabel.setText(originalMotherPolitics);
        motherWorkLabel.setText(originalMotherWork);

        exitEditMode();
    }

    // =================== 退出编辑模式 ===================
    private void exitEditMode() {
        editing = false;

        editOrSaveButton.setText("修改");
        editOrSaveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");

        // 根据学籍状态更新按钮
        updateStatusButton(studentStatusLabel.getText());

        // === 基本信息 ===
        studentGridPane.getChildren().removeAll(birthDatePicker, nativePlaceField, politicsComboBox);
        studentGridPane.add(birthDateLabel, 3, 3);
        studentGridPane.add(nativePlaceLabel, 1, 4);
        studentGridPane.add(politicsStatusLabel, 3, 4);

        // === 联系方式 ===
        contactGridPane.getChildren().removeAll(phoneField, emailField, dormAddressField);
        contactGridPane.add(phoneLabel, 1, 0);
        contactGridPane.add(emailLabel, 3, 0);
        contactGridPane.add(dormAddressLabel, 1, 1);

        // === 父亲信息 ===
        fatherGridPane.getChildren().removeAll(fatherNameField, fatherPhoneField, fatherPoliticsCombo, fatherWorkField);
        fatherGridPane.add(fatherNameLabel, 1, 0);
        fatherGridPane.add(fatherPhoneLabel, 3, 0);
        fatherGridPane.add(fatherPoliticsLabel, 1, 1);
        fatherGridPane.add(fatherWorkLabel, 3, 1);

        // === 母亲信息 ===
        motherGridPane.getChildren().removeAll(motherNameField, motherPhoneField, motherPoliticsCombo, motherWorkField);
        motherGridPane.add(motherNameLabel, 1, 0);
        motherGridPane.add(motherPhoneLabel, 3, 0);
        motherGridPane.add(motherPoliticsLabel, 1, 1);
        motherGridPane.add(motherWorkLabel, 3, 1);
    }

    // =================== 根据学籍状态设置按钮 ===================
    private void updateStatusButton(String studentStatus) {
        Platform.runLater(() -> {
            pdfOrCancelButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 6;");
            if ("在读".equals(studentStatus)) {
                pdfOrCancelButton.setText("休学申请");
                pdfOrCancelButton.setVisible(true);
            } else if ("休学".equals(studentStatus)) {
                pdfOrCancelButton.setText("复学申请");
                pdfOrCancelButton.setVisible(true);
            } else { // 毕业或其他状态
                pdfOrCancelButton.setVisible(false);
            }
        });
    }

    // =================== 弹出学籍异动申请对话框 ===================
    private void showStudentStatusApplicationDialog() {
        String currentStatus = studentStatusLabel.getText();
        String applicationType;

        // 根据当前学籍状态确定申请类型
        if ("在读".equals(currentStatus)) {
            applicationType = "休学申请";
        } else if ("休学".equals(currentStatus)) {
            applicationType = "复学申请";
        } else {
            showInfo("当前学生状态无法进行学籍申请");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(applicationType);
        dialog.setHeaderText("请填写学籍异动申请内容");

        ButtonType submitButtonType = new ButtonType("提交", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("请输入申请理由...");
        reasonArea.setPrefRowCount(5);
        dialog.getDialogPane().setContent(reasonArea);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return reasonArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(reason -> {
            if (reason != null && !reason.trim().isEmpty()) {
                submitStatusApplication(applicationType, reason);
            } else {
                showInfo("申请内容不能为空");
            }
        });
    }

    private void submitStatusApplication(String applicationType, String reason) {
        StudentLeaveApplication application = new StudentLeaveApplication();
        application.setStudentId(userIdLabel.getText());
        application.setStudentName(nameLabel.getText());
        application.setCurrentStatus(studentStatusLabel.getText());
        application.setType(applicationType);
        application.setReason(reason);
        application.setCreateTime(java.time.LocalDate.now());
        application.setStatus("待审批");

        // 调用 service 提交
        studentService.submitStatusApplication(application);
    }


    // =================== 网络消息回调 ===================
    public void handleStudentInfoResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() != null) {
                Student student = (Student) message.getData();
                userIdLabel.setText(student.getUserId());
                studentIdLabel.setText(student.getStudentId());
                nameLabel.setText(student.getName());
                genderLabel.setText(student.getGender());
                collegeLabel.setText(student.getCollege());
                majorLabel.setText(student.getMajor());
                gradeLabel.setText(String.valueOf(student.getGrade()));
                birthDateLabel.setText(student.getBirth_date());
                nativePlaceLabel.setText(student.getNative_place());
                politicsStatusLabel.setText(student.getPolitics_status());
                studentStatusLabel.setText(student.getStudent_status());

                phoneLabel.setText(student.getPhone());
                emailLabel.setText(student.getEmail());
                dormAddressLabel.setText(student.getDormAddress());

                fatherNameLabel.setText(student.getFatherName());
                fatherPhoneLabel.setText(student.getFatherPhone());
                fatherPoliticsLabel.setText(student.getFatherPoliticsStatus());
                fatherWorkLabel.setText(student.getFatherWorkUnit());

                motherNameLabel.setText(student.getMotherName());
                motherPhoneLabel.setText(student.getMotherPhone());
                motherPoliticsLabel.setText(student.getMotherPoliticsStatus());
                motherWorkLabel.setText(student.getMotherWorkUnit());

                // 更新按钮显示
                updateStatusButton(student.getStudent_status());
            } else {
                showError("加载学生信息失败：" + message.getMessage());
            }
        });
    }

    public void handleUpdateStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message == null) {
                showError("收到空消息");
                return;
            }
            if (message.getAction() != ActionType.UPDATE_STUDENT) return;

            if (message.isSuccess() && message.getData() instanceof Student) {
                Student updatedStudent = (Student) message.getData();
                birthDateLabel.setText(updatedStudent.getBirth_date());
                nativePlaceLabel.setText(updatedStudent.getNative_place());
                politicsStatusLabel.setText(updatedStudent.getPolitics_status());

                phoneLabel.setText(updatedStudent.getPhone());
                emailLabel.setText(updatedStudent.getEmail());
                dormAddressLabel.setText(updatedStudent.getDormAddress());

                fatherNameLabel.setText(updatedStudent.getFatherName());
                fatherPhoneLabel.setText(updatedStudent.getFatherPhone());
                fatherPoliticsLabel.setText(updatedStudent.getFatherPoliticsStatus());
                fatherWorkLabel.setText(updatedStudent.getFatherWorkUnit());

                motherNameLabel.setText(updatedStudent.getMotherName());
                motherPhoneLabel.setText(updatedStudent.getMotherPhone());
                motherPoliticsLabel.setText(updatedStudent.getMotherPoliticsStatus());
                motherWorkLabel.setText(updatedStudent.getMotherWorkUnit());

                exitEditMode();
                showInfo("学生信息更新成功");
            } else {
                showError("学生信息更新失败：" + message.getMessage());
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    @Override
    public void registerToMessageController() {
        if (studentService.getGlobalSocketClient() != null &&
                studentService.getGlobalSocketClient().getMessageController() != null) {
            studentService.getGlobalSocketClient().getMessageController().setStudentController(this);
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

    // null 安全处理
    private String safeText(Label label) {
        return (label != null && label.getText() != null) ? label.getText() : "";
    }

    public void handleStudentLeaveApplicationResponse(Message response) {
        Platform.runLater(() -> {
            if (response == null) {
                showError("未收到服务器响应。");
                return;
            }

            if (response.isSuccess()) {
                Object data = response.getData();
                if (data instanceof StudentLeaveApplication) {
                    StudentLeaveApplication application = (StudentLeaveApplication) data;
                    showInfo("申请提交成功！\n申请编号: " + application.getApplicationId() +
                            "\n状态: " + application.getStatus());
                } else {
                    showInfo("申请提交成功！");
                }
            } else {
                String errorMsg = response.getMessage() != null ? response.getMessage() : "未知错误";
                showError("申请失败: " + errorMsg);
            }
        });
    }

}
