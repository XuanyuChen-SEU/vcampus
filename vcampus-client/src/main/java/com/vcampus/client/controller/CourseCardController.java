package com.vcampus.client.controller;

//import com.vcampus.client.model.Course;（这两个实际上并没有）
//import com.vcampus.client.model.ClassSession;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.function.Consumer;
//中间层从上层接受数据和命令，再把数据和命令传递给下层
public class CourseCardController {

    //所有已知组件的联系
    @FXML private HBox headerRow;
    @FXML private Label courseIdLabel;
    @FXML private Hyperlink detailsLink;
    @FXML private Label courseNameLabel;
    @FXML private Label courseTypeLabel;
    @FXML private Label departmentLabel;
    @FXML private Label statusLabel;
    @FXML private Label sessionCountLabel;
    @FXML private FlowPane sessionsContainer;

    private Course course;//dto的工具类，代表了对象，里面包含了传递数据所需要的数据

    /**
     * 由上层(AcademicController)调用，用于填充数据和设置回调
     * @param course 该卡片代表的课程数据
     * @param onSelectCallback 最终要执行的回调函数 (需要继续向下传递)
     */
    public void setData(Course course, Consumer<ClassSession> onSelectCallback) {
        this.course = course;

        // 1. 填充标题行信息
        courseIdLabel.setText(course.getCourseId());
        courseNameLabel.setText(course.getCourseName());
        courseTypeLabel.setText(course.getCourseType());
        departmentLabel.setText(course.getDepartment());
        int sessionCount = course.getSessions() != null ? course.getSessions().size() : 0;
        sessionCountLabel.setText(String.valueOf(sessionCount));
        updateStatusLabel(course);

        // 2. ⭐ 串联的实现：加载“最低层”的 FXML
        sessionsContainer.getChildren().clear();
        //这个应该是组件，吧组件加载后再对内部的逻辑进行处理

        if (sessionCount > 0) {
            for (ClassSession session : course.getSessions()) {
                try {
                    //改一下内部关联的例子
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/academic/CourseCard.fxml"));
                    Node sessionCardNode = loader.load();
                    ClassSessionCardController sessionController = loader.getController();

                    // 3. 将数据和回调函数“接力”传递给最内层的 Controller
                    sessionController.setData(session, onSelectCallback);

                    sessionsContainer.getChildren().add(sessionCardNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @FXML
    private void toggleSessions() {
        boolean isVisible = sessionsContainer.isVisible();
        sessionsContainer.setVisible(!isVisible);
        sessionsContainer.setManaged(!isVisible);
        if (sessionsContainer.isVisible()) {
            headerRow.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 15 20;");
        } else {
            headerRow.setStyle("-fx-background-color: transparent; -fx-padding: 15 20;");
        }
    }


    @FXML
    private void handleDetailsLinkClick(MouseEvent event) {
        System.out.println("点击了课程详情: " + course.getCourseId());
        event.consume();
    }

    //处理前端逻辑
    //根据课程状态更新标签
    //我认为这里应该是message的变化，参数有可能是message
    private void updateStatusLabel(Course course) {
        switch (course.getStatus()) {
            case SELECTED:
                statusLabel.setText("✔ 已选");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                break;
            case CONFLICT:
                statusLabel.setText("时间冲突");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #c62828;");
                break;
            case FULL:
                statusLabel.setText("选课已满");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ef6c00;");
                break;
            case NOT_SELECTED:
            default:
                statusLabel.setText("未选");
                statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888;");
                break;
        }
    }


}
