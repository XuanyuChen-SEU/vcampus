package com.vcampus.client.controller.libraryAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.LibraryService;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * 创建借阅记录子视图控制器
 */
public class BorrowLogCreateController implements IClientController {

    // FXML 控件
    @FXML private TextField bookIdField;
    @FXML private TextField userIdField;
    @FXML private Button createButton;
    @FXML private Button resetButton;

    // 服务
    private final LibraryService libraryService = new LibraryService();

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController =
                MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setBorrowLogCreateController(this);
        }
    }

    @FXML
    public void initialize() {
        registerToMessageController();
    }

    /**
     * 处理“创建记录”按钮点击事件
     */
    @FXML
    private void handleCreate(ActionEvent event) {
        String bookId = bookIdField.getText();
        String userId = userIdField.getText();

        if (bookId.isEmpty() || userId.isEmpty()) {
            showAlert("输入错误", "书籍ID和用户ID均为必填项！");
            return;
        }

        // 发送异步请求到服务器，让服务器处理创建逻辑
        libraryService.createBorrowLog(bookId, userId);
    }

    /**
     * 处理“重置表单”按钮点击事件
     */
    @FXML
    private void handleReset(ActionEvent event) {
        bookIdField.clear();
        userIdField.clear();
    }

    /**
     * 处理来自服务器的创建借阅记录响应
     * @param message 服务器返回的消息
     */
    public void handleCreateBorrowLogResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("操作成功", "新的借阅记录已成功创建！");
                handleReset(null); // 创建成功后清空表单
            } else {
                showAlert("操作失败", "创建借阅记录失败: " + message.getMessage());
            }
        });
    }

    /**
     * 显示提示/错误信息的对话框
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}