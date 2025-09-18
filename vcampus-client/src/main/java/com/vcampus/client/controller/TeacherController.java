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

/**
 * TeacherController 是教师信息界面的控制器。
 * <p>
 * 主要功能：
 * <ul>
 *     <li>显示教师的基本信息（姓名、性别、学院、部门、职称、联系方式、办公室）</li>
 *     <li>支持教师信息的编辑与保存（电话、邮箱、办公室可修改）</li>
 *     <li>更新教师头像，根据性别显示不同图片</li>
 *     <li>接收并处理服务器返回的教师信息和更新结果</li>
 * </ul>
 */
public class TeacherController implements IClientController {

    // === 基本信息 Label ===
    @FXML private Label userIdLabel;       // 教师工号
    @FXML private Label nameLabel;         // 教师姓名
    @FXML private Label genderLabel;       // 性别
    @FXML private Label collegeLabel;      // 学院
    @FXML private Label departmentLabel;   // 系/部门
    @FXML private Label titleLabel;        // 职称
    @FXML private Label phoneLabel;        // 电话
    @FXML private Label emailLabel;        // 邮箱
    @FXML private Label officeLabel;       // 办公室
    @FXML private ImageView teacherImageView; // 教师头像

    // === 按钮 ===
    @FXML private Button editOrSaveButton; // 编辑/保存按钮

    @FXML private GridPane teacherGridPane; // 用于布局 Label/TextField

    // 服务类
    private final TeacherService teacherService = new TeacherService();
    private final StudentAdminService studentAdminService = new StudentAdminService();

    // 编辑状态标记
    private boolean editing = false;

    // === 可编辑 TextField ===
    private TextField phoneField;
    private TextField emailField;
    private TextField officeField;

    // 教师头像资源路径
    private final String TEACHER_MAN_IMAGE = "/images/StudentIcons/man.png";
    private final String TEACHER_WOMAN_IMAGE = "/images/StudentIcons/woman.png";

    /**
     * 初始化方法，在界面加载时自动调用。
     * <p>
     * 功能：
     * <ul>
     *     <li>注册到消息控制器</li>
     *     <li>加载当前教师信息</li>
     *     <li>设置编辑/保存按钮的事件处理逻辑</li>
     * </ul>
     */
    @FXML
    private void initialize() {
        registerToMessageController();
        loadCurrentTeacherInfo();

        editOrSaveButton.setOnAction(event -> {
            if (!editing) enterEditMode();
            else saveChanges();
        });
    }

    /** 加载当前登录教师的信息 */
    private void loadCurrentTeacherInfo() {
        String currentUserId = UserSession.getInstance().getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            showError("当前没有登录用户，请先登录！");
            return;
        }
        teacherService.getCurrentTeacher();
    }

    /** 进入编辑模式，仅允许修改电话、邮箱和办公室 */
    private void enterEditMode() {
        editing = true;
        editOrSaveButton.setText("保存");

        phoneField = new TextField(phoneLabel.getText());
        emailField = new TextField(emailLabel.getText());
        officeField = new TextField(officeLabel.getText());

        // 获取 Label 在 GridPane 的位置
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

    /** 保存修改后的信息并恢复 Label 显示 */
    private void saveChanges() {
        editing = false;
        editOrSaveButton.setText("修改");

        phoneLabel.setText(phoneField.getText());
        emailLabel.setText(emailField.getText());
        officeLabel.setText(officeField.getText());

        // 获取 TextField 在 GridPane 的位置
        int phoneRow = GridPane.getRowIndex(phoneField);
        int phoneCol = GridPane.getColumnIndex(phoneField);
        int emailRow = GridPane.getRowIndex(emailField);
        int emailCol = GridPane.getColumnIndex(emailField);
        int officeRow = GridPane.getRowIndex(officeField);
        int officeCol = GridPane.getColumnIndex(officeField);

        // 移除 TextField 恢复 Label
        teacherGridPane.getChildren().removeAll(phoneField, emailField, officeField);
        teacherGridPane.add(phoneLabel, phoneCol, phoneRow);
        teacherGridPane.add(emailLabel, emailCol, emailRow);
        teacherGridPane.add(officeLabel, officeCol, officeRow);

        // 构建更新请求
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

    /**
     * 处理服务器返回的教师信息
     *
     * @param message 服务器返回消息对象
     */
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

    /** 显示信息提示弹窗 */
    private void showInfo(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    /**
     * 根据教师性别更新头像
     *
     * @param gender 教师性别
     */
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

    /** 注册当前控制器到全局消息控制器 */
    @Override
    public void registerToMessageController() {
        if (teacherService.getGlobalSocketClient() != null &&
                teacherService.getGlobalSocketClient().getMessageController() != null) {
            teacherService.getGlobalSocketClient().getMessageController().setTeacherController(this);
        }
    }

    /**
     * 处理服务器返回的教师信息更新结果
     *
     * @param message 服务器返回消息
     */
    public void handleTeacherUpdateResponse(Message message) {
        Platform.runLater(() -> {
            if (message == null) return;

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

                    updateTeacherImage(teacher.getGender());
                }
                showInfo("教师信息更新成功！");
            } else {
                if (message.getData() instanceof Teacher teacher) {
                    // 更新界面以修正客户端状态
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
                showError("教师信息更新失败，请稍后重试！");
            }
        });
    }

}
