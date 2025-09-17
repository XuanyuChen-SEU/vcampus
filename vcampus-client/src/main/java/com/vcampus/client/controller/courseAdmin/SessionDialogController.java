package com.vcampus.client.controller.courseAdmin;

import com.vcampus.client.service.courseAdmin.CourseAdminService;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SessionDialogController {

    @FXML private TextField courseIdField;
    @FXML private TextField sessionIdField;
    @FXML private TextField teacherNameField;
    @FXML private TextField scheduleField;
    @FXML private TextField capacityField;

    private Stage dialogStage;
    private Course parentCourse;
    private ClassSession sessionToEdit;
    private boolean isEditMode = false;
    private final CourseAdminService courseAdminService = new CourseAdminService();

    /**
     * 由父控制器调用，用于初始化弹窗的数据和模式（新增/修改）
     * @param parentCourse 新教学班所属的课程
     * @param sessionToEdit 如果是修改模式，则传入要编辑的教学班；如果是新增模式，则传入 null
     */
    public void initData(Course parentCourse, ClassSession sessionToEdit) {
        this.parentCourse = parentCourse;
        this.sessionToEdit = sessionToEdit;

        // 首先设置不可变的课程ID
        if (parentCourse != null) {
            courseIdField.setText(parentCourse.getCourseId());

        }

        if (sessionToEdit != null) {
            // 这是【修改模式】
            this.isEditMode = true;
            dialogStage.setTitle("修改教学班信息");

            // 填充现有数据
            sessionIdField.setText(sessionToEdit.getSessionId());
            sessionIdField.setDisable(true); // ID通常不允许修改
            courseIdField.setText(sessionToEdit.getCourseId());
            courseIdField.setDisable(true); // 课程ID通常不允许修改
            teacherNameField.setText(sessionToEdit.getTeacherName());
            scheduleField.setText(sessionToEdit.getScheduleInfo());
            capacityField.setText(String.valueOf(sessionToEdit.getCapacity()));
        } else {
            // 这是【新增模式】
            this.isEditMode = false;
            dialogStage.setTitle("添加新教学班");
            // 输入框保持空白，且 sessionId 可以编辑
            sessionIdField.setDisable(false);
            courseIdField.setText(parentCourse.getCourseId());
            courseIdField.setDisable(true);
        }
    }

    /**
     * 由父控制器调用，用于传入当前窗口的 Stage 对象
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * 处理“保存”按钮的点击事件
     */
    @FXML
    private void handleSave() {
        if (!isInputValid()) {
            return; // 如果输入无效，则不执行任何操作
        }

        if (isEditMode) {
            // --- 修改逻辑 ---
            // 1. 更新现有 DTO 对象
            sessionToEdit.setTeacherName(teacherNameField.getText());
            sessionToEdit.setScheduleInfo(scheduleField.getText());
            sessionToEdit.setCapacity(Integer.parseInt(capacityField.getText()));
            // 2. 调用 Service 发送修改请求
            courseAdminService.modifySession(sessionToEdit);
        } else {
            // --- 新增逻辑 ---
            // 1. 创建一个新的 ClassSession DTO
            ClassSession newSession = new ClassSession();
            newSession.setCourseId(parentCourse.getCourseId()); // 关键：设置父课程ID
            newSession.setSessionId(sessionIdField.getText());
            newSession.setTeacherName(teacherNameField.getText());
            newSession.setScheduleInfo(scheduleField.getText());
            newSession.setCapacity(Integer.parseInt(capacityField.getText()));
            newSession.setEnrolledCount(0); // 新增的班级已选人数为0
            newSession.setSelectedByStudent(false);
            // 2. 调用 Service 发送新增请求
            courseAdminService.addSession(newSession);
        }

        // 3. 关闭弹窗
        dialogStage.close();
    }

    /**
     * 处理“取消”按钮的点击事件
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * 简单的输入验证
     * @return 如果输入有效，返回 true
     */
    private boolean isInputValid() {
        String errorMessage = "";
        if (sessionIdField.getText() == null || sessionIdField.getText().isEmpty()) {
            errorMessage += "教学班号不能为空！\n";
        }
        if (teacherNameField.getText() == null || teacherNameField.getText().isEmpty()) {
            errorMessage += "教师姓名不能为空！\n";
        }
        try {
            Integer.parseInt(capacityField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "课容量必须是一个有效的整数！\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // 显示错误弹窗
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("输入无效");
            alert.setHeaderText("请修正以下错误：");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}