package com.vcampus.client.controller;

import com.vcampus.client.service.StudentAdminService;
import com.vcampus.client.service.TeacherService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Teacher;
import com.vcampus.common.enums.ActionType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class TeacherController implements IClientController {

    // === 基本信息 Label ===
    @FXML private Label userIdLabel;
    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private Label collegeLabel;
    @FXML private Label departmentLabel;
    @FXML private Label titleLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label officeLabel;
    @FXML private ImageView teacherImageView;

    // === 按钮 ===
    @FXML private Button editOrSaveButton;

    @FXML private GridPane teacherGridPane;

    private final TeacherService teacherService = new TeacherService();
    private final StudentAdminService studentAdminService = new StudentAdminService();
    private boolean editing = false;

    // === 可编辑 TextField ===
    private TextField phoneField;
    private TextField emailField;
    private TextField officeField;

    private final String TEACHER_MAN_IMAGE = "/images/StudentIcons/man.png";
    private final String TEACHER_WOMAN_IMAGE = "/images/StudentIcons/woman.png";

    @FXML
    private void initialize() {
        registerToMessageController();
        loadCurrentTeacherInfo();

        // 编辑/保存按钮
        editOrSaveButton.setOnAction(event -> {
            if (!editing) enterEditMode();
            else saveChanges();
        });
    }

    /** 加载当前教师信息 */
    private void loadCurrentTeacherInfo() {
        String currentUserId = UserSession.getInstance().getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            showError("当前没有登录用户，请先登录！");
            return;
        }

        teacherService.getCurrentTeacher();
    }

    /** 进入编辑模式，只允许修改电话、邮箱、办公室 */
    private void enterEditMode() {
        editing = true;
        editOrSaveButton.setText("保存");

        // 创建 TextField 并赋初值
        phoneField = new TextField(phoneLabel.getText());
        emailField = new TextField(emailLabel.getText());
        officeField = new TextField(officeLabel.getText());

        // 获取 GridPane 中原始 row/col
        int phoneRow = GridPane.getRowIndex(phoneLabel);
        int phoneCol = GridPane.getColumnIndex(phoneLabel);

        int emailRow = GridPane.getRowIndex(emailLabel);
        int emailCol = GridPane.getColumnIndex(emailLabel);

        int officeRow = GridPane.getRowIndex(officeLabel);
        int officeCol = GridPane.getColumnIndex(officeLabel);

        // 替换 Label 为 TextField
        teacherGridPane.getChildren().removeAll(phoneLabel, emailLabel, officeLabel);
        teacherGridPane.add(phoneField, phoneCol, phoneRow);
        teacherGridPane.add(emailField, emailCol, emailRow);
        teacherGridPane.add(officeField, officeCol, officeRow);
    }

    /** 保存修改并恢复 Label */
    private void saveChanges() {
        editing = false;
        editOrSaveButton.setText("修改");

        // 更新 Label
        phoneLabel.setText(phoneField.getText());
        emailLabel.setText(emailField.getText());
        officeLabel.setText(officeField.getText());

        // 获取 row/col
        int phoneRow = GridPane.getRowIndex(phoneField);
        int phoneCol = GridPane.getColumnIndex(phoneField);

        int emailRow = GridPane.getRowIndex(emailField);
        int emailCol = GridPane.getColumnIndex(emailField);

        int officeRow = GridPane.getRowIndex(officeField);
        int officeCol = GridPane.getColumnIndex(officeField);

        // 移除 TextField，恢复 Label
        teacherGridPane.getChildren().removeAll(phoneField, emailField, officeField);
        teacherGridPane.add(phoneLabel, phoneCol, phoneRow);
        teacherGridPane.add(emailLabel, emailCol, emailRow);
        teacherGridPane.add(officeLabel, officeCol, officeRow);

        // 发送更新请求
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setUserId(userIdLabel.getText());

        updatedTeacher.setName(nameLabel.getText());
        updatedTeacher.setGender(genderLabel.getText());
        updatedTeacher.setCollege(collegeLabel.getText());
        updatedTeacher.setDepartment(departmentLabel.getText());
        updatedTeacher.setTitle(titleLabel.getText());

        updatedTeacher.setPhone(phoneLabel.getText());
        updatedTeacher.setEmail(emailLabel.getText());
        updatedTeacher.setOffice(officeLabel.getText());

        teacherService.UpdateTeacher(updatedTeacher);

    }

    /** 处理教师信息返回消息 */
    public void handleTeacherInfoResponse(Message message) {
        Platform.runLater(() -> {
            if (message == null) return;

            if (message.isSuccess() && message.getData() instanceof Teacher teacher) {
                userIdLabel.setText(teacher.getUserId());
                nameLabel.setText(teacher.getName());
                genderLabel.setText(teacher.getGender());
                collegeLabel.setText(teacher.getCollege());
                departmentLabel.setText(teacher.getDepartment());
                titleLabel.setText(teacher.getTitle());
                phoneLabel.setText(teacher.getPhone());
                emailLabel.setText(teacher.getEmail());
                officeLabel.setText(teacher.getOffice());

                updateTeacherImage(teacher.getGender());
            } else if (!message.isSuccess() && message.getAction() == ActionType.INFO_TEACHER) {
                showError("无法获取教师信息，请稍后重试！");
            } else {
                System.out.println("收到教师信息消息，但数据格式不正确: " + message.getData());
            }
        });
    }

    /** 显示错误弹窗 */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** 更新教师头像 */
    private void updateTeacherImage(String gender) {
        if (gender == null) gender = "";
        String imagePath = gender.contains("女") ? TEACHER_WOMAN_IMAGE : TEACHER_MAN_IMAGE;

        try {
            java.net.URL url = getClass().getResource(imagePath);
            if (url != null) {
                teacherImageView.setImage(new Image(url.toExternalForm()));
            } else {
                teacherImageView.setImage(null);
                System.out.println("找不到教师头像资源: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            teacherImageView.setImage(null);
        }
    }

    /** 注册到全局消息控制器 */
    @Override
    public void registerToMessageController() {
        if (teacherService.getGlobalSocketClient() != null &&
                teacherService.getGlobalSocketClient().getMessageController() != null) {
            teacherService.getGlobalSocketClient().getMessageController().setTeacherController(this);
        }
    }

    public void handleTeacherUpdateResponse(Message message) {
        Platform.runLater(() -> {
            if (message == null) return; // 不处理空消息

            // 处理成功更新
            if (message.isSuccess()) {
                if (message.getData() instanceof Teacher teacher) {
                    // 更新界面显示最新教师信息
                    userIdLabel.setText(teacher.getUserId());
                    nameLabel.setText(teacher.getName());
                    genderLabel.setText(teacher.getGender());
                    collegeLabel.setText(teacher.getCollege());
                    departmentLabel.setText(teacher.getDepartment());
                    titleLabel.setText(teacher.getTitle());
                    phoneLabel.setText(teacher.getPhone());
                    emailLabel.setText(teacher.getEmail());
                    officeLabel.setText(teacher.getOffice());

                    // 更新头像
                    updateTeacherImage(teacher.getGender());
                }
                // 提示用户更新成功
                showInfo("教师信息更新成功！");
            }
            // 处理更新失败
            else {
                // 如果服务端返回 data，也可以用来更新界面（比如修正客户端状态）
                if (message.getData() instanceof Teacher teacher) {
                    userIdLabel.setText(teacher.getUserId());
                    nameLabel.setText(teacher.getName());
                    genderLabel.setText(teacher.getGender());
                    collegeLabel.setText(teacher.getCollege());
                    departmentLabel.setText(teacher.getDepartment());
                    titleLabel.setText(teacher.getTitle());
                    phoneLabel.setText(teacher.getPhone());
                    emailLabel.setText(teacher.getEmail());
                    officeLabel.setText(teacher.getOffice());

                    updateTeacherImage(teacher.getGender());
                }
                // 只有真正失败才弹窗
                showError("教师信息更新失败，请稍后重试！");
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
