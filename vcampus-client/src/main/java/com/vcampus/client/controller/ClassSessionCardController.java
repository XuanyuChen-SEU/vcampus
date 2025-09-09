package com.vcampus.client.controller;

//import com.vcampus.client.model.ClassSession;(目前暂时不用）
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

    private ClassSession session;
    private Consumer<ClassSession> onSelectCallback; // 接收到的回调命令

    /**
     * 由上层(CourseCardController)调用，用于填充数据
     * @param session 该卡片代表的教学班数据
     * @param onSelectCallback 当“选择/退选”按钮被点击时要执行的回调
     */
    //这个回调我觉得后期可以换成message,就是怎么通过message来实现回调
    public void setData(ClassSession session, Consumer<ClassSession> onSelectCallback) {
        this.session = session;
        this.onSelectCallback = onSelectCallback;

        teacherNameLabel.setText(session.getTeacherName());
        //scheduleLabel.setText(session.getScheduleInfo());
        capacityLabel.setText("课容量: " + session.getCapacity() + "人");
        enrolledLabel.setText("已选人数: " + session.getEnrolledCount() + "人");

        selectedTagLabel.setVisible(session.isSelectedByStudent());
        updateButtonState();
    }


    /**
     * 处理“选择”或“退选”按钮的点击事件
     */
    @FXML
    private void handleSelectAction() {
        // ⭐ 串联的触发点：执行回调，将自己的 session 数据传递出去
        //将session传给service，调用serbice层的东西传输给服务层
        if (onSelectCallback != null) {
            onSelectCallback.accept(this.session);
        }
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
