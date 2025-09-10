package com.vcampus.client.controller.userAdmin;

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
 * 编写人：AI Assistant
 */
public class UserPasswordResetViewController {

    // 表单组件
    @FXML
    private TextField userIdField;
    
    @FXML
    private TextField roleField;
    
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
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 设置用户ID输入监听
        userIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() == 7 && newValue.matches("\\d{7}")) {
                // 根据ID前缀自动设置角色
                String role = getRoleFromId(newValue);
                roleField.setText(role);
            } else {
                roleField.clear();
            }
        });
    }
    
    /**
     * 根据用户ID获取角色
     */
    private String getRoleFromId(String userId) {
        if (userId.length() >= 1) {
            String firstChar = userId.substring(0, 1);
            switch (firstChar) {
                case "1": return "学生";
                case "2": return "教师";
                case "3": return "管理员";
                default: return "未知";
            }
        }
        return "";
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
        confirmAlert.setContentText("确定要重置用户 " + userId + " 的密码吗？\n用户需要使用新密码重新登录。");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: 发送重置密码请求到服务器
                System.out.println("重置密码 - ID: " + userId);
                
                // 显示成功消息
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("重置成功");
                successAlert.setHeaderText("密码重置成功");
                successAlert.setContentText("用户 " + userId + " 的密码已成功重置！\n请通知用户使用新密码登录。");
                successAlert.showAndWait();
                
                // 清空密码字段
                clearPasswordFields();
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
        
        if (newPassword.length() < 6) {
            showError("密码长度不能少于6位");
            newPasswordField.requestFocus();
            return false;
        }
        
        if (newPassword.length() > 20) {
            showError("密码长度不能超过20位");
            newPasswordField.requestFocus();
            return false;
        }
        
        // 验证密码强度（简单验证）
        if (!newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*\\d.*")) {
            showError("密码必须包含字母和数字");
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
}
