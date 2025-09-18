package com.vcampus.client.controller;

import com.vcampus.common.dto.ClassSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SessionDetailDialogController {

    // ⭐ 1. 移除与 Course 相关的 @FXML 变量
    @FXML private Label lblSessionId;
    @FXML private Label lblTeacherName;
    @FXML private Label lblScheduleInfo;
    @FXML private Label lblCapacity;
    @FXML private Label lblEnrolledCount;

    /**
     * ⭐ 2. 简化 initData 方法，现在只接收 ClassSession 对象
     * @param session 要显示的教学班数据
     */
    public void initData(ClassSession session) {
        if (session == null) return;

        // 只填充教学班自身的信息
        lblSessionId.setText(session.getSessionId());
        lblTeacherName.setText(session.getTeacherName());
        lblScheduleInfo.setText(session.getScheduleInfo());
        lblCapacity.setText(String.valueOf(session.getCapacity()));
        lblEnrolledCount.setText(String.valueOf(session.getEnrolledCount()));
    }
}