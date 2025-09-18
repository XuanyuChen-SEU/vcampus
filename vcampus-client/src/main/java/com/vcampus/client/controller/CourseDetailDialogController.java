package com.vcampus.client.controller;

import com.vcampus.common.dto.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * 课程详情弹窗的控制器
 */
public class CourseDetailDialogController {

    // 注入所有用来显示“值”的 Label
    @FXML private Label lblCourseName;
    @FXML private Label lblDepartment;
    @FXML private Label lblCourseType;
    @FXML private Label lblCredits;
    @FXML private Label lblCategory;
    @FXML private Label lblLanguage;
    @FXML private Label lblCampus;

    /**
     * 公共方法，由父控制器调用，用于传入 Course 对象并填充界面
     * @param course 要显示的课程数据
     */
    public void initData(Course course) {
        if (course == null) return;

        // 从 Course 对象中获取数据，并设置到对应的 Label 上
        lblCourseName.setText(course.getCourseName() + " (" + course.getCourseId() + ")");
        lblDepartment.setText(course.getDepartment());
        lblCourseType.setText(course.getCourseType());
        lblCredits.setText(String.valueOf(course.getCredits()));
        lblCategory.setText(course.getCategory());
        lblCampus.setText(course.getCampus());

        // 假设 Course DTO 中有这些字段，如果没有，则显示默认值
        // 您可以根据需要，在 Course DTO 中添加这些字段
        lblLanguage.setText("中文"); // 示例
    }
}