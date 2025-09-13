package com.vcampus.client.controller;

//import com.vcampus.client.model.ClassSession;(目前暂时不用）
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.ClassSession;//（这个我一定要自己实现）
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.function.Consumer;

public class ClassSessionCardController {

    @FXML private Label teacherNameLabel;
    @FXML private Label conflictLabel; // 未使用，但保留
    @FXML private Label selectedTagLabel;
    @FXML private Label scheduleLabel;
    @FXML private Label capacityLabel;
    @FXML private Label enrolledLabel;
    @FXML private Button actionButton;

    // 每个卡片控制器都持有 Service 的实例
    private final CourseService courseService = new CourseService();
    private ClassSession session;

    /**
     * 由上层(CourseCardController)调用，用于填充数据
     * @param session 该卡片代表的教学班数据
     */
    //这个回调我觉得后期可以换成message,就是怎么通过message来实现回调
    public void setData(ClassSession session) {
//        this.session = session;
//
//        teacherNameLabel.setText(session.getTeacherName());
//        //scheduleLabel.setText(session.getScheduleInfo());
//        capacityLabel.setText("课容量: " + session.getCapacity() + "人");
//        enrolledLabel.setText("已选人数: " + session.getEnrolledCount() + "人");
//
//        selectedTagLabel.setVisible(session.isSelectedByStudent());
//        selectedTagLabel.setManaged(session.isSelectedByStudent());
//        updateButtonState();
        this.session = session;
        // ⭐ 每次都用最新的 session 对象数据来完全重置UI
        teacherNameLabel.setText(session.getTeacherName());
        scheduleLabel.setText(session.getScheduleInfo());
        capacityLabel.setText("课容量: " + session.getCapacity() + "人");
        enrolledLabel.setText("已选人数: " + session.getEnrolledCount() + "人");
        updateButtonState();
    }


    /**
     * 处理“选择”或“退选”按钮的点击事件
     */
    @FXML
    private void handleSelectAction() {
        // ⭐ 串联的触发点：执行回调，将自己的 session 数据传递出去
        //将session传给service，调用serbice层的东西传输给服务层
        // 安全检查，防止在数据未设置时被调用

        // 1. 提供即时UI反馈：禁用按钮并显示“处理中”，防止用户重复点击
        actionButton.setDisable(true);
        actionButton.setText("处理中...");

        // 2. 直接调用 CourseService 发送网络请求
        // 这是职责分离的核心：本控制器只负责“发起”，不关心“结果”
        if (session.isSelectedByStudent()) {
            // 如果当前是“已选”状态，则执行退课操作
            courseService.dropCourse(session.getSessionId());
        } else {
            // 否则，执行选课操作
            courseService.selectCourse(session.getSessionId());
        }
        // 请求发送后，本控制器的任务就完成了。
        // 它会静静等待顶层 AcademicController 刷新整个列表，从而获得新的状态。
    }

    //这里我要设置session为一个类
    private void updateButtonState() {
        if (session.isSelectedByStudent()) {
            actionButton.setText("退选");
            actionButton.setDisable(false);
            actionButton.setStyle("-fx-background-color: #FF7043; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px;");
        } else if (session.getEnrolledCount() >= session.getCapacity()) {
            actionButton.setText("已满");
            actionButton.setDisable(true);
            actionButton.setStyle("");
        } else {
            actionButton.setText("选择");
            actionButton.setDisable(false);
            actionButton.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 12px;");
        }
    }

}