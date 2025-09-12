package com.vcampus.client.controller.userAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.userAdmin.UserPasswordResetService;
import com.vcampus.common.dto.Message;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 用户密码重置控制器
 * 负责重置用户密码的功能
 * 编写人：谌宣羽
 */
public class UserPasswordResetViewController implements IClientController{

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setUserPasswordResetViewController(this);
        }
    }

    // Service层
    private final UserPasswordResetService passwordResetService;
    
    // 表单组件
    @FXML
    private TextField userIdField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    // 按钮组件
    @FXML
    private Button resetButton;
    
    @FXML
    private Button clearButton;
    
    /**
     * 构造函数
     */
    public UserPasswordResetViewController() {
        this.passwordResetService = new UserPasswordResetService();
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        registerToMessageController();
    }
    
    /**
     * 设置用户ID（从外部调用）
     * @param userId 用户ID，如果为null则清空表单让用户手动输入
     */
    public void setUserId(String userId) {
        if (userId != null && !userId.isEmpty()) {
            userIdField.setText(userId);
            // 将焦点设置到密码输入框
            newPasswordField.requestFocus();
        } else {
            // 如果userId为null，清空表单让用户手动输入
            userIdField.clear();
            userIdField.requestFocus();
        }
    }
    
    
    /**
     * 处理重置密码
     */
    @FXML
    private void handleReset(ActionEvent event) {
        // 验证输入
        if (!validateInput()) {
            return;
        }
        
        // 获取表单数据
        String userId = userIdField.getText().trim();
        String newPassword = newPasswordField.getText();
        
        // 显示确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认重置");
        confirmAlert.setHeaderText("重置用户密码");
        confirmAlert.setContentText("确定要重置用户 " + userId + " 的密码吗？");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // 使用Service层发送重置密码请求
                    Message result = passwordResetService.resetUserPassword(userId, newPassword);
                    
                    if (result.isSuccess()) {
                        // 发送请求成功
                        System.out.println("成功发送重置密码请求: " + userId);
                        // 注意：这里只确认请求发送成功，实际重置结果会在后续的响应处理中处理
                        
                        // 显示发送成功消息
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("请求已发送");
                        successAlert.setHeaderText("密码重置请求已发送");
                        successAlert.setContentText("用户 " + userId + " 的密码重置请求已发送到服务器，请等待处理结果。");
                        successAlert.showAndWait();
                        
                        // 清空密码字段
                        clearPasswordFields();
                    } else {
                        // 发送请求失败
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("发送失败");
                        errorAlert.setHeaderText("密码重置请求发送失败");
                        errorAlert.setContentText(result.getMessage());
                        errorAlert.showAndWait();
                        System.err.println("发送重置密码请求失败: " + result.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("发送重置密码请求时发生异常: " + e.getMessage());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("发送失败");
                    errorAlert.setHeaderText("密码重置请求发送失败");
                    errorAlert.setContentText("发送请求时发生异常: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    /**
     * 处理清空
     */
    @FXML
    private void handleClear(ActionEvent event) {
        clearPasswordFields();
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput() {
        String userId = userIdField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // 验证用户ID
        if (userId.isEmpty()) {
            showError("请输入用户ID");
            userIdField.requestFocus();
            return false;
        }
        
        if (userId.length() != 7) {
            showError("用户ID必须是7位数字");
            userIdField.requestFocus();
            return false;
        }
        
        if (!userId.matches("\\d{7}")) {
            showError("用户ID只能包含数字");
            userIdField.requestFocus();
            return false;
        }
        
        // 验证新密码
        if (newPassword.isEmpty()) {
            showError("请输入新密码");
            newPasswordField.requestFocus();
            return false;
        }
        
        
        // 验证确认密码
        if (confirmPassword.isEmpty()) {
            showError("请确认新密码");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 清空密码字段
     */
    private void clearPasswordFields() {
        newPasswordField.clear();
        confirmPasswordField.clear();
        newPasswordField.requestFocus();
    }
    
    /**
     * 显示错误信息
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("输入错误");
        alert.setHeaderText("请检查输入信息");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleResetUserPasswordResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("重置用户密码成功: " + message.getMessage());
        } else {
            System.err.println("重置用户密码失败: " + message.getMessage());
        }
    }
}
