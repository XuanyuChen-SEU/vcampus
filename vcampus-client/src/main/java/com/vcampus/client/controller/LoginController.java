package com.vcampus.client.controller;

import com.vcampus.client.service.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 登录界面控制器
 * 专注于处理登录界面的用户交互逻辑
 * 网络通信逻辑委托给LoginService处理
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label statusLabel;
    
    // 登录服务实例
    private final LoginService loginService = new LoginService();
    
    /**
     * 初始化方法，在FXML加载完成后自动调用
     */
    @FXML
    private void initialize() {
        // 为登录按钮添加点击事件
        loginButton.setOnAction(event -> handleLogin());
        
        // 为输入框添加回车键事件
        usernameField.setOnAction(event -> handleLogin());
        passwordField.setOnAction(event -> handleLogin());
        
        // 设置状态标签初始文本
        statusLabel.setText("请输入用户名和密码");
        
        // 设置登录服务回调
        setupLoginCallback();
    }
    
    /**
     * 设置登录服务回调
     */
    private void setupLoginCallback() {
        loginService.setLoginCallback(new LoginService.LoginCallback() {
            @Override
            public void onLoginSuccess(String message) {
                showSuccess("登录成功", message);
                statusLabel.setText("登录成功，欢迎使用VCampus系统");
                passwordField.clear();
                
                // TODO: 这里可以跳转到主界面
                // switchToMainView();
            }
            
            @Override
            public void onLoginFailure(String error) {
                showError("登录失败: " + error);
                statusLabel.setText("登录失败，请检查用户名和密码");
                passwordField.clear();
            }
            
            @Override
            public void onConnectionError(String error) {
                showError(error);
                statusLabel.setText("网络连接错误");
            }
        });
    }
    
    /**
     * 处理登录按钮点击事件
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // 客户端基础验证
        if (!loginService.validateInputFormat(username, password)) {
            showError("请输入用户名和密码");
            return;
        }
        
        // 更新状态标签
        statusLabel.setText("正在发送登录请求...");
        
        // 委托给登录服务处理网络通信
        loginService.login(username, password);
    }
    
    /**
     * 显示成功信息
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("操作成功");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示错误信息
     */
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("输入错误");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 获取用户名（供外部调用）
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }
    
    /**
     * 获取密码（供外部调用）
     */
    public String getPassword() {
        return passwordField.getText();
    }
    
    /**
     * 清空输入框
     */
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        statusLabel.setText("请输入用户名和密码");
    }
}
