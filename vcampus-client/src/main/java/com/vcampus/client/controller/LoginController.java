package com.vcampus.client.controller;

import java.io.IOException;
import java.net.URL;

import com.vcampus.client.service.LoginService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 客户端登录控制器
 * 处理登录相关的响应消息和前端交互
 * 编写人：谌宣羽
 */
public class LoginController implements IClientController {
    
    // 前端UI组件
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Hyperlink forgotPasswordLink;
    
    @FXML
    private Label passwordLabel;
    
    @FXML
    private javafx.scene.layout.VBox confirmPasswordContainer;
    
    @FXML
    private Label confirmPasswordLabel;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    // 登录服务实例
    private final LoginService loginService = new LoginService();
    
    // 当前模式：true为忘记密码模式，false为登录模式
    private boolean isForgotPasswordMode = false;
    
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
        confirmPasswordField.setOnAction(event -> handleLogin());
        
        // 为忘记密码链接添加点击事件
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
        
        // 设置状态标签初始文本
        statusLabel.setText("请输入用户名和密码");
        
        // 将自己注册到MessageController
        registerToMessageController();
    }
    
    /**
     * 注册到MessageController
     */
    @Override
    public void registerToMessageController() {
        // 获取全局SocketClient中的MessageController
        com.vcampus.client.controller.MessageController messageController = 
            loginService.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setLoginController(this);
        }
    }
    
    /**
     * 处理登录按钮点击事件
     */
    private void handleLogin() {
        if (isForgotPasswordMode) {
            // 在忘记密码模式下，调用密码重置逻辑
            handlePasswordReset();
            return;
        }
        
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
        // 注意：这里不直接处理结果，而是等待MessageController调用handleLoginResponse
        loginService.login(username, password);
    }
    
    /**
     * 处理忘记密码链接点击事件
     */
    private void handleForgotPassword() {
        if (!isForgotPasswordMode) {
            // 切换到忘记密码模式
            switchToForgotPasswordMode();
        } else {
            // 切换回登录模式
            switchToLoginMode();
        }
    }
    
    /**
     * 切换到忘记密码模式
     */
    private void switchToForgotPasswordMode() {
        isForgotPasswordMode = true;
        
        // 更新UI显示
        passwordLabel.setText("新密码");
        passwordField.setPromptText("请输入新密码");
        confirmPasswordLabel.setText("确认密码");
        confirmPasswordField.setPromptText("请再次输入新密码");
        loginButton.setText("提交申请");
        forgotPasswordLink.setText("返回登录");
        statusLabel.setText("请输入账号、新密码并确认密码");
        
        // 显示确认密码输入框
        confirmPasswordContainer.setVisible(true);
        confirmPasswordContainer.setManaged(true);
        
        // 清空输入框
        passwordField.clear();
        confirmPasswordField.clear();
    }
    
    /**
     * 切换到登录模式
     */
    private void switchToLoginMode() {
        isForgotPasswordMode = false;
        
        // 更新UI显示
        passwordLabel.setText("密码");
        passwordField.setPromptText("请输入密码");
        loginButton.setText("登录");
        forgotPasswordLink.setText("忘记密码？");
        statusLabel.setText("请输入用户名和密码");
        
        // 隐藏确认密码输入框
        confirmPasswordContainer.setVisible(false);
        confirmPasswordContainer.setManaged(false);
        
        // 清空输入框
        passwordField.clear();
        confirmPasswordField.clear();
    }
    
    /**
     * 处理密码重置申请
     */
    private void handlePasswordReset() {
        String username = usernameField.getText().trim();
        String newPassword = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        // 验证输入
        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("请填写完整信息");
            return;
        }
        
        // 验证密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            showError("两次输入的密码不一致，请重新输入");
            confirmPasswordField.clear();
            return;
        }
        
        // 更新状态标签
        statusLabel.setText("正在提交密码重置申请...");
        
        // 调用LoginService的密码重置方法
        loginService.submitPasswordResetRequest(username, newPassword);
    }
    
    /**
     * 处理密码重置响应（从MessageController调用）
     * @param message 密码重置响应消息
     */
    public void handleForgetPasswordResponse(Message message) {
        // 在JavaFX应用线程中处理UI更新
        Platform.runLater(() -> {
            handlePasswordResetResult(message);
        });
    }
    
    /**
     * 处理密码重置结果
     * @param result 密码重置结果消息
     */
    private void handlePasswordResetResult(Message result) {
        if (result.isSuccess()) {
            showSuccess("密码重置申请", result.getMessage());
            statusLabel.setText("密码重置申请已提交，请等待管理员审核");
            // 清空输入框并切换回登录模式
            clearFields();
            switchToLoginMode();
        } else {
            showError("密码重置申请失败: " + result.getMessage());
            statusLabel.setText("密码重置申请失败，请检查输入信息");
        }
    }
    
    /**
     * 处理登录响应（从MessageController调用）
     * @param message 登录响应消息
     */
    public void handleLoginResponse(Message message) {
        // 在JavaFX应用线程中处理UI更新
        Platform.runLater(() -> {
            handleLoginResult(message);
        });
    }
    
    /**
     * 处理登录结果
     * @param result 登录结果消息
     */
    private void handleLoginResult(Message result) {
        if (result.isSuccess()) {
            // 获取用户名并保存到全局会话
            String username = usernameField.getText().trim();
            UserSession.getInstance().setCurrentUser(username);
            
            showSuccess("登录成功", result.getMessage());
            statusLabel.setText("登录成功，欢迎使用VCampus系统");
            passwordField.clear();
            
            switchToMainView();
        } else {
            showError("登录失败: " + result.getMessage());
            statusLabel.setText("登录失败，请检查用户名和密码");
            passwordField.clear();
        }
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

    private void switchToMainView() {
        try {
            // 1. 关闭当前的登录窗口
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

            // 2. 创建一个新的 Stage 用于主界面
            Stage mainStage = new Stage();

            // 3. 加载主界面的 FXML 文件
            URL fxmlLocation = getClass().getResource("/fxml/MainView.fxml");
            if (fxmlLocation == null) {
                System.err.println("严重错误: 找不到主界面 FXML 文件 /fxml/MainView.fxml");
                showError("无法加载应用程序主界面，请联系管理员。");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(loader.load());

            // 4. 为主界面加载 CSS
            URL cssLocation = getClass().getResource("/css/styles.css");
            if(cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            // 5. 设置并显示主界面窗口
            mainStage.setTitle("VCampus 虚拟校园系统");
            mainStage.setScene(scene);
            mainStage.setMinWidth(800);
            mainStage.setMinHeight(600);
            mainStage.setWidth(1000);
            mainStage.setHeight(700);
            mainStage.centerOnScreen();
            mainStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("加载主界面时发生严重错误: " + e.getMessage());
        }
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
        confirmPasswordField.clear();
        statusLabel.setText("请输入用户名和密码");
        
        // 确保回到登录模式
        if (isForgotPasswordMode) {
            switchToLoginMode();
        }
    }
}
