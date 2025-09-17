package com.vcampus.client.controller.courseAdmin;

import com.vcampus.client.service.courseAdmin.CourseAdminService;
import com.vcampus.common.dto.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class CourseDialogController {

    @FXML private Label titleLabel;
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField courseTypeField;
    @FXML private TextField departmentField;
    @FXML private TextField creditsField;
    @FXML private TextField categoryField;
    @FXML private TextField campusField;

    private Stage dialogStage;
    private Course courseToEdit;
    private boolean isEditMode = false;
    private final CourseAdminService courseAdminService = new CourseAdminService();

    /**
     * 由父控制器调用，用于初始化弹窗的数据和模式
     */
    public void initData(Course courseToEdit) {
        this.courseToEdit = courseToEdit;

        if (courseToEdit != null) {
            // 这是【修改模式】
            this.isEditMode = true;
            titleLabel.setText("修改课程信息");

            // 填充现有数据
            courseIdField.setText(courseToEdit.getCourseId());
            courseIdField.setDisable(true); // ID通常不允许修改
            courseNameField.setText(courseToEdit.getCourseName());
            courseTypeField.setText(courseToEdit.getCourseType());
            departmentField.setText(courseToEdit.getDepartment());
            creditsField.setText(String.valueOf(courseToEdit.getCredits()));
            categoryField.setText(courseToEdit.getCategory());
            campusField.setText(courseToEdit.getCampus());
        } else {
            // 这是【新增模式】
            this.isEditMode = false;
            titleLabel.setText("增加新课程");
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleSave() {
        if (!isInputValid()) return;

        if (isEditMode) {
            // --- 修改逻辑 ---
            // --- 这是【修改】模式的逻辑 ---
            // 1. 用输入框中的新数据，更新我们持有的 courseToEdit DTO 对象
            courseToEdit.setCourseName(courseNameField.getText());
            courseToEdit.setCourseType(courseTypeField.getText());
            courseToEdit.setDepartment(departmentField.getText());
            courseToEdit.setCredits(Double.parseDouble(creditsField.getText()));
            courseToEdit.setCategory(categoryField.getText());
            courseToEdit.setCampus(campusField.getText());

            // 2. 调用 Service，将这个【更新后】的对象发送到服务器
            System.out.println("Dialog: 正在发送修改课程请求 -> " + courseToEdit.getCourseId());
            courseAdminService.modifyCourse(courseToEdit);
            // (我们将在实现“修改课程”功能时再来完成这部分)
        } else {
            // --- 新增逻辑 ---
            Course newCourse = new Course();
            newCourse.setCourseId(courseIdField.getText());
            newCourse.setCourseName(courseNameField.getText());
            newCourse.setCourseType(courseTypeField.getText());
            newCourse.setDepartment(departmentField.getText());
            newCourse.setCredits(Double.parseDouble(creditsField.getText()));
            newCourse.setCategory(categoryField.getText());
            newCourse.setCampus(campusField.getText());
            // ⭐ 新增的课程，其教学班列表是一个空的 ArrayList
            newCourse.setSessions(new ArrayList<>());

            // 调用 Service 发送新增请求
            courseAdminService.addCourse(newCourse);
        }

        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        // TODO: 添加更完善的输入验证逻辑
        return true;
    }
}