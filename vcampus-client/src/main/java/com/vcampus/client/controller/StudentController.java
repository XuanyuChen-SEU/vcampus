package com.vcampus.client.controller;

import com.vcampus.client.service.StudentService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.enums.ActionType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label dormAddressLabel;

    @FXML private Label fatherNameLabel;
    @FXML private Label fatherPhoneLabel;
    @FXML private Label fatherPoliticsLabel;
    @FXML private Label fatherWorkLabel;

    @FXML private Label motherNameLabel;
    @FXML private Label motherPhoneLabel;
    @FXML private Label motherPoliticsLabel;
    @FXML private Label motherWorkLabel;


    @FXML private Button editOrSaveButton;       // 修改/保存
    @FXML private Button pdfOrCancelButton;      // 导出PDF/取消

    @FXML private GridPane studentGridPane;

    private final StudentService studentService = new StudentService();

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
            if (!editing) exportPdf();
            else cancelEdit();
        });
    }

    private void enterEditMode() {
        editing = true;

        // 按钮样式修改
        editOrSaveButton.setText("保存");
        editOrSaveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 6;");
        pdfOrCancelButton.setText("取消");
        pdfOrCancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");

        // 保存原值
        originalBirthDate = birthDateLabel.getText();
        originalNativePlace = nativePlaceLabel.getText();
        originalPoliticsStatus = politicsStatusLabel.getText();

        originalPhone = phoneLabel.getText();
        originalEmail = emailLabel.getText();
        originalDormAddress = dormAddressLabel.getText();

        originalFatherName = fatherNameLabel.getText();
        originalFatherPhone = fatherPhoneLabel.getText();
        originalFatherPolitics = fatherPoliticsLabel.getText();
        originalFatherWork = fatherWorkLabel.getText();

        originalMotherName = motherNameLabel.getText();
        originalMotherPhone = motherPhoneLabel.getText();
        originalMotherPolitics = motherPoliticsLabel.getText();
        originalMotherWork = motherWorkLabel.getText();

        // 创建编辑控件
        birthDatePicker = new DatePicker();
        if (originalBirthDate != null && !originalBirthDate.isEmpty()) {
            birthDatePicker.setValue(LocalDate.parse(originalBirthDate));
        }
        nativePlaceField = new TextField(originalNativePlace);

        politicsComboBox = new ComboBox<>();
        politicsComboBox.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        politicsComboBox.setValue(originalPoliticsStatus);

        phoneField = new TextField(originalPhone);
        emailField = new TextField(originalEmail);
        dormAddressField = new TextField(originalDormAddress);

        fatherNameField = new TextField(originalFatherName);
        fatherPhoneField = new TextField(originalFatherPhone);
        fatherPoliticsCombo = new ComboBox<>();
        fatherPoliticsCombo.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        fatherPoliticsCombo.setValue(originalFatherPolitics);
        fatherWorkField = new TextField(originalFatherWork);

        motherNameField = new TextField(originalMotherName);
        motherPhoneField = new TextField(originalMotherPhone);
        motherPoliticsCombo = new ComboBox<>();
        motherPoliticsCombo.getItems().addAll("中共党员", "预备党员", "共青团员", "群众");
        motherPoliticsCombo.setValue(originalMotherPolitics);
        motherWorkField = new TextField(originalMotherWork);

        // 替换 Label 为编辑控件
        studentGridPane.getChildren().removeAll(
                birthDateLabel, nativePlaceLabel, politicsStatusLabel,
                phoneLabel, emailLabel, dormAddressLabel,
                fatherNameLabel, fatherPhoneLabel, fatherPoliticsLabel, fatherWorkLabel,
                motherNameLabel, motherPhoneLabel, motherPoliticsLabel, motherWorkLabel
        );

        studentGridPane.add(birthDatePicker, 8, 1);
        studentGridPane.add(nativePlaceField, 9, 1);
        studentGridPane.add(politicsComboBox, 10, 1);

        studentGridPane.add(phoneField, 12, 1);
        studentGridPane.add(emailField, 13, 1);
        studentGridPane.add(dormAddressField, 14, 1);

        studentGridPane.add(fatherNameField, 0, 2);
        studentGridPane.add(fatherPhoneField, 1, 2);
        studentGridPane.add(fatherPoliticsCombo, 2, 2);
        studentGridPane.add(fatherWorkField, 3, 2);

        studentGridPane.add(motherNameField, 4, 2);
        studentGridPane.add(motherPhoneField, 5, 2);
        studentGridPane.add(motherPoliticsCombo, 6, 2);
        studentGridPane.add(motherWorkField, 7, 2);
    }

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

    private void cancelEdit() {
        // 恢复原值
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

    private void exitEditMode() {
        editing = false;

        editOrSaveButton.setText("修改");
        editOrSaveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");

        pdfOrCancelButton.setText("导出PDF");
        pdfOrCancelButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 6;");

        studentGridPane.getChildren().removeAll(
                birthDatePicker, nativePlaceField, politicsComboBox,
                phoneField, emailField, dormAddressField,
                fatherNameField, fatherPhoneField, fatherPoliticsCombo, fatherWorkField,
                motherNameField, motherPhoneField, motherPoliticsCombo, motherWorkField
        );

        studentGridPane.add(birthDateLabel, 8, 1);
        studentGridPane.add(nativePlaceLabel, 9, 1);
        studentGridPane.add(politicsStatusLabel, 10, 1);

        studentGridPane.add(phoneLabel, 12, 1);
        studentGridPane.add(emailLabel, 13, 1);
        studentGridPane.add(dormAddressLabel, 14, 1);

        studentGridPane.add(fatherNameLabel, 0, 2);
        studentGridPane.add(fatherPhoneLabel, 1, 2);
        studentGridPane.add(fatherPoliticsLabel, 2, 2);
        studentGridPane.add(fatherWorkLabel, 3, 2);

        studentGridPane.add(motherNameLabel, 4, 2);
        studentGridPane.add(motherPhoneLabel, 5, 2);
        studentGridPane.add(motherPoliticsLabel, 6, 2);
        studentGridPane.add(motherWorkLabel, 7, 2);
    }

    private void exportPdf() {
        // TODO: 实现 PDF 导出逻辑
        showInfo("导出 PDF 功能待实现");
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
}






