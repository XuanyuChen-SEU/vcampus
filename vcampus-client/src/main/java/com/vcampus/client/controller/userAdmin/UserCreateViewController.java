package com.vcampus.client.controller.userAdmin;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 用户创建控制器
 * 负责创建新用户的功能
 * 编写人：AI Assistant
 */
public class UserCreateViewController {

    // 表单组件
    @FXML
    private TextField userIdField;
    
    @FXML
    private ComboBox<String> roleCombo;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    // 按钮组件
    @FXML
    private Button createButton;
    
    @FXML
    private Button resetButton;

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 初始化角色选择下拉框
        initializeRoleCombo();
        
        // 设置输入验证
        setupInputValidation();
    }
    
    /**
     * 初始化角色选择下拉框
     */
    private void initializeRoleCombo() {
        roleCombo.setItems(FXCollections.observableArrayList(
            "学生", "教师", "管理员"
        ));
    }
    
    /**
     * 设置输入验证
     */
    private void setupInputValidation() {
        // 用户ID输入验证
        userIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}")) {
                userIdField.setText(oldValue);
            }
        });
        
        // 角色选择后自动设置ID前缀
        roleCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String currentId = userIdField.getText();
                if (currentId.length() <= 1) {
                    String prefix = getRolePrefix(newValue);
                    userIdField.setText(prefix + currentId.substring(Math.min(1, currentId.length())));
                }
            }
        });
    }
    
    /**
     * 获取角色对应的ID前缀
     */
    private String getRolePrefix(String role) {
        switch (role) {
            case "学生": return "1";
            case "教师": return "2";
            case "管理员": return "3";
            default: return "";
        }
    }
    
    /**
     * 处理创建用户
     */
    @FXML
    private void handleCreate(ActionEvent event) {
        // 验证输入
        if (!validateInput()) {
            return;
        }
        
        // 获取表单数据
        String userId = userIdField.getText().trim();
        String role = roleCombo.getValue();
        String password = passwordField.getText();
        
        // TODO: 发送创建用户请求到服务器
        System.out.println("创建用户 - ID: " + userId + ", 角色: " + role);
        
        // 显示成功消息
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("创建成功");
        alert.setHeaderText("用户创建成功");
        alert.setContentText("用户 " + userId + " (" + role + ") 已成功创建！");
        alert.showAndWait();
        
        // 重置表单
        resetForm();
    }
    
    /**
     * 处理重置表单
     */
    @FXML
    private void handleReset(ActionEvent event) {
        resetForm();
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput() {
        String userId = userIdField.getText().trim();
        String role = roleCombo.getValue();
        String password = passwordField.getText();
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
        
        // 验证角色
        if (role == null || role.isEmpty()) {
            showError("请选择用户角色");
            roleCombo.requestFocus();
            return false;
        }
        
        // 验证密码
        if (password.isEmpty()) {
            showError("请输入初始密码");
            passwordField.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            showError("密码长度不能少于6位");
            passwordField.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // 验证ID前缀与角色是否匹配
        String expectedPrefix = getRolePrefix(role);
        if (!userId.startsWith(expectedPrefix)) {
            showError("用户ID前缀与选择的角色不匹配");
            userIdField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 重置表单
     */
    private void resetForm() {
        userIdField.clear();
        roleCombo.setValue(null);
        passwordField.clear();
        confirmPasswordField.clear();
        userIdField.requestFocus();
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
