package com.vcampus.client.controller.userAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.userAdmin.UserCreateService;
import com.vcampus.common.dto.Message;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 用户创建控制器
 * 负责创建新用户的功能
 * 编写人：谌宣羽
 */
public class UserCreateViewController implements IClientController{

    // Service层
    private final UserCreateService userCreateService;
    
    // 表单组件
    @FXML
    private TextField userIdField;
    
    
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
     * 构造函数
     */
    public UserCreateViewController() {
        this.userCreateService = new UserCreateService();
    }

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            //messageController.setUserCreateViewController(this);
        }
        
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 设置输入验证
        setupInputValidation();
        registerToMessageController();
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
    }
    
    /**
     * 根据用户ID获取角色描述
     */
    private String getRoleDescription(String userId) {
        if (userId.length() >= 1) {
            String firstChar = userId.substring(0, 1);
            switch (firstChar) {
                case "1": return "学生";
                case "2": return "教师";
                case "3": return "用户管理员";
                default: return "未知";
            }
        }
        return "";
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
        String password = passwordField.getText();
        String roleDescription = getRoleDescription(userId);
        
        try {
            // 使用Service层发送创建用户请求
            Message result = userCreateService.createUser(userId, password);
            
            if (result.isSuccess()) {
                // 发送请求成功
                System.out.println("成功发送创建用户请求: " + userId);
                // 注意：这里只确认请求发送成功，实际创建结果会在后续的响应处理中处理
                
                // 显示发送成功消息
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("请求已发送");
                alert.setHeaderText("创建用户请求已发送");
                alert.setContentText("用户 " + userId + " (" + roleDescription + ") 的创建请求已发送到服务器，请等待处理结果。");
                alert.showAndWait();
                
                // 重置表单
                resetForm();
            } else {
                // 发送请求失败
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("发送失败");
                alert.setHeaderText("创建用户请求发送失败");
                alert.setContentText(result.getMessage());
                alert.showAndWait();
                System.err.println("发送创建用户请求失败: " + result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("发送创建用户请求时发生异常: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("发送失败");
            alert.setHeaderText("创建用户请求发送失败");
            alert.setContentText("发送请求时发生异常: " + e.getMessage());
            alert.showAndWait();
        }
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
        
        // 验证ID首位是否有效
        String firstChar = userId.substring(0, 1);
        if (!firstChar.matches("[1-2]")) {
            showError("用户ID首位必须是1（学生）、2（教师）");
            userIdField.requestFocus();
            return false;
        }
        
        // 验证密码
        if (password.isEmpty()) {
            showError("请输入初始密码");
            passwordField.requestFocus();
            return false;
        }
        
        
        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 重置表单
     */
    private void resetForm() {
        userIdField.clear();
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

    public void handleCreateUserResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("创建用户成功: " + message.getMessage());
        } else {
            System.err.println("创建用户失败: " + message.getMessage());
        }
    }
}
