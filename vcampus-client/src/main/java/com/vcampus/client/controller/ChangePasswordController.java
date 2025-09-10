package com.vcampus.client.controller;

import java.net.URL;

import com.vcampus.client.MainApp;
import com.vcampus.client.service.ChangePasswordService;
import com.vcampus.client.service.ChangePasswordService.ChangePasswordResult;
import com.vcampus.client.service.ChangePasswordService.PasswordValidationResult;
import com.vcampus.common.dto.Message;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * 修改密码界面控制器
 * 处理用户修改密码的相关操作
 * 编写人：Assistant
 */
public class ChangePasswordController implements IClientController{

    /**
     * 注册到MessageController
     */
    @Override
    public void registerToMessageController() {
        // 获取全局SocketClient中的MessageController
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setChangePasswordController(this);
        }
    }

    @FXML
    private PasswordField oldPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label statusLabel;
    
    // 服务类实例
    private ChangePasswordService changePasswordService;

    /**
     * 初始化方法，由JavaFX在FXML文件加载完成后自动调用
     */
    @FXML
    public void initialize() {
        // 注册到MessageController
        registerToMessageController();
        
        // 初始化服务类
        changePasswordService = new ChangePasswordService();
        
        // 清空状态信息
        statusLabel.setText("");
        
        // 清空所有输入框
        clearFields();
    }
    
    /**
     * 处理修改密码按钮点击事件
     */
    @FXML
    private void handleChangePassword(ActionEvent event) {
        String oldPassword = oldPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        // 基本验证
        if (oldPassword.isEmpty()) {
            showStatus("请输入原密码", false);
            return;
        }
        
        if (newPassword.isEmpty()) {
            showStatus("请输入新密码", false);
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            showStatus("请确认新密码", false);
            return;
        }
        
        // 使用服务类进行密码确认验证
        PasswordValidationResult confirmationResult = changePasswordService.validatePasswordConfirmation(newPassword, confirmPassword);
        if (!confirmationResult.isValid()) {
            showStatus(confirmationResult.getMessage(), false);
            return;
        }
        
        // 显示处理中状态
        showStatus("正在修改密码，请稍候...", true);
        
        // 使用服务类执行密码修改
        ChangePasswordResult result = changePasswordService.changePassword(oldPassword, newPassword);
        
        // 显示结果
        showStatus(result.getMessage(), result.isSuccess());
        
        // 如果成功，清空输入框
        if (result.isSuccess()) {
            clearFields();
        }
    }

    public void handleChangePasswordResponse(Message message) {
        // 使用Platform.runLater确保UI更新在JavaFX应用线程中执行
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                // 显示成功提示框
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("密码修改成功");
                successAlert.setHeaderText("密码修改成功");
                successAlert.setContentText("密码修改成功！为了安全起见，请重新登录。");
                successAlert.showAndWait();
                
                // 清空用户会话
                MainApp.getGlobalUserSession().clearSession();
                
                // 返回登录页面
                showLoginView();
            } else {
                // 显示失败信息
                Alert failureAlert = new Alert(AlertType.ERROR);
                failureAlert.setTitle("密码修改失败");
                failureAlert.setHeaderText("密码修改失败");
                failureAlert.setContentText("密码修改失败：" + message.getMessage());
                failureAlert.showAndWait();
            }
        });
    }
    
    /**
     * 显示状态信息
     * @param message 状态信息
     * @param isSuccess 是否为成功信息
     */
    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText(message);
        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: #4CAF50;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #f44336;");
        }
    }
    
    /**
     * 清空所有输入框
     */
    private void clearFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    
    /**
     * 获取原密码（供外部调用）
     */
    public String getOldPassword() {
        return oldPasswordField.getText().trim();
    }
    
    /**
     * 获取新密码（供外部调用）
     */
    public String getNewPassword() {
        return newPasswordField.getText().trim();
    }
    
    /**
     * 显示登录页面
     */
    private void showLoginView() {
        try {
            // 关闭当前窗口
            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.close();
            
            // 创建新的登录窗口
            Stage loginStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            
            // 添加CSS样式
            URL cssLocation = getClass().getResource("/css/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }
            
            loginStage.setTitle("VCampus 客户端 - 登录");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.setMinWidth(400);
            loginStage.setMinHeight(500);
            loginStage.centerOnScreen();
            loginStage.show();
            
        } catch (Exception e) {
            System.err.println("加载登录界面时发生错误: " + e.getMessage());
            // 记录错误日志
        }
    }
}