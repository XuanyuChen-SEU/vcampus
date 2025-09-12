package com.vcampus.client.controller;

import java.io.IOException;
import java.net.URL;

import com.vcampus.client.service.LoginService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Message;

import javafx.animation.Timeline;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    
    // 轮播相关组件
    @FXML
    private VBox carouselContainer;
    
    @FXML
    private ImageView carouselImageView;
    
    @FXML
    private HBox indicatorContainer;
    
    @FXML
    private Circle indicator1;
    
    @FXML
    private Circle indicator2;
    
    @FXML
    private Circle indicator3;
    
    @FXML
    private Circle indicator4;
    
    // 轮播相关变量
    private final String[] carouselImages = {
        "/images/carousel/carousel1.png",
        "/images/carousel/carousel2.png", 
        "/images/carousel/carousel3.png",
        "/images/carousel/carousel4.png"
    };
    private int currentImageIndex = 0;
    private Timeline carouselTimeline;
    
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
        
        // 初始化轮播
        initializeCarousel();
        
        // 将自己注册到MessageController
        registerToMessageController();
    }
    
    /**
     * 初始化轮播功能
     */
    private void initializeCarousel() {
        if (carouselImageView != null) {
            // 设置初始图片
            updateCarouselImage(0);
            
            // 创建轮播时间线，每3秒切换一次
            carouselTimeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(3), e -> nextCarouselImage())
            );
            carouselTimeline.setCycleCount(Timeline.INDEFINITE);
            carouselTimeline.play();
        }
    }
    
    /**
     * 切换到下一张图片
     */
    private void nextCarouselImage() {
        currentImageIndex = (currentImageIndex + 1) % carouselImages.length;
        updateCarouselImage(currentImageIndex);
    }
    
    /**
     * 更新轮播图片和指示器
     */
    private void updateCarouselImage(int index) {
        try {
            Image image = new Image(getClass().getResourceAsStream(carouselImages[index]));
            carouselImageView.setImage(image);
        } catch (Exception e) {
            System.err.println("加载轮播图片失败: " + e.getMessage());
        }
        
        // 更新指示器
        updateIndicators(index);
    }
    
    /**
     * 更新指示器状态
     */
    private void updateIndicators(int activeIndex) {
        Circle[] indicators = {indicator1, indicator2, indicator3, indicator4};
        
        for (int i = 0; i < indicators.length; i++) {
            if (indicators[i] != null) {
                if (i == activeIndex) {
                    indicators[i].getStyleClass().setAll("indicator", "active");
                } else {
                    indicators[i].getStyleClass().setAll("indicator");
                }
            }
        }
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
    //这个是服务器返回处理信息，然后controller根据这个信息处理前端变化
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
            
            // 根据用户ID首位数字跳转到不同界面
            switchToRoleBasedView(username);
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

    /**
     * 根据用户角色跳转到相应的界面
     * @param username 用户ID
     */
    private void switchToRoleBasedView(String username) {
        try {
            // 1. 关闭当前的登录窗口
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

            // 2. 创建一个新的 Stage 用于主界面
            Stage mainStage = new Stage();

            // 3. 根据用户ID首位数字确定角色和界面
            String firstChar = username.substring(0, 1);
            String fxmlPath;
            String windowTitle;

            switch (firstChar) {
                case "1": // 学生
                    fxmlPath = "/fxml/MainView.fxml";
                    windowTitle = "VCampus 虚拟校园系统 - 学生端";
                    break;
                case "2": // 教师
                    fxmlPath = "/fxml/MainView.fxml";
                    windowTitle = "VCampus 虚拟校园系统 - 教师端";
                    break;
                default: // 其他角色为管理员
                    fxmlPath = "/fxml/AdminView.fxml";
                    windowTitle = "VCampus 虚拟校园系统 - 管理员端";
                    break;
            }

            // 4. 加载对应界面的 FXML 文件
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                System.err.println("严重错误: 找不到界面 FXML 文件 " + fxmlPath);
                showError("无法加载应用程序界面，请联系管理员。");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Scene scene = new Scene(loader.load());

            // 5. 为界面加载 CSS
            URL cssLocation = getClass().getResource("/css/styles.css");
            if(cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }

            // 6. 设置并显示界面窗口
            mainStage.setTitle(windowTitle);
            mainStage.setScene(scene);
            mainStage.setMinWidth(800);
            mainStage.setMinHeight(600);
            mainStage.setWidth(1000);
            mainStage.setHeight(700);
            mainStage.centerOnScreen();
            mainStage.show();

        } catch (IOException e) {
            System.err.println("加载界面时发生严重错误: " + e.getMessage());
            showError("加载界面时发生严重错误: " + e.getMessage());
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
