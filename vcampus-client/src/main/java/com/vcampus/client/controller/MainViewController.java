package com.vcampus.client.controller;

import java.io.IOException;
import java.net.URL;

import com.vcampus.client.MainApp;
import com.vcampus.client.session.UserSession;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 主界面 (MainView.fxml) 的控制器。
 * 它的核心职责是：
 * 1. 响应左侧导航栏的按钮点击。
 * 2. 在中央内容区动态加载和切换不同的功能模块视图。
 * 编写人：王思懿
 */
public class MainViewController {

    // 这个AnchorPane对应MainView.fxml中<center>区域的容器，fx:id="mainContentPane"
    @FXML
    private AnchorPane mainContentPane;
    
    // 用户信息显示标签
    @FXML
    private Label userInfoLabel;
    
    // Logo相关组件
    @FXML
    private ImageView logoIconView;
    
    @FXML
    private ImageView logoTextView;
    
    // 登出按钮
    @FXML
    private Button logoutButton;
    
    // 侧边栏相关字段
    @FXML
    private VBox sidebarContainer;
    
    @FXML
    private Button homeButton;
    
    @FXML
    private Button storeButton;
    
    @FXML
    private Button libraryButton;
    
    @FXML
    private Button studentRecordButton;
    
    @FXML
    private Button academicButton;

    @FXML
    private Button emailButton;

    @FXML
    private Button chatButton;
    // 动画相关字段
    private boolean isSidebarExpanded = false;
    private Timeline expandTimeline;
    private Timeline collapseTimeline;

    /**
     * 初始化方法，由JavaFX在FXML文件加载完成后自动调用。
     * 用于设置应用程序的初始状态，例如加载默认的欢迎页面。
     */
    @FXML
    public void initialize() {
        // 初始化侧边栏动画
        initializeSidebar();
        
        // 显示当前用户信息
        updateUserInfo();

        // ------------------ 接口点: MainViewController -> WelcomeView.fxml ------------------
        loadView("/fxml/WelcomeView.fxml"); // 默认加载欢迎界面
        // --------------------------------------------------------------------------------
    }
    
    /**
     * 更新用户信息显示
     */
    private void updateUserInfo() {
        if (userInfoLabel != null) {
            UserSession userSession = MainApp.getGlobalUserSession();
            if (userSession.isLoggedIn()) {
                userInfoLabel.setText("👤 当前用户: " + userSession.getCurrentUserDisplayName());
            } else {
                userInfoLabel.setText("❌ 未登录");
            }
        }
    }
    
    /**
     * 处理修改密码按钮点击事件
     * @param event 点击事件
     */
    @FXML
    private void handleChangePassword(ActionEvent event) {
        // 加载修改密码界面到中央内容区
        loadView("/fxml/ChangePasswordView.fxml");
    }
    
    /**
     * 处理登出按钮点击事件
     * @param event 点击事件
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        // 显示确认对话框
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认登出");
        confirmAlert.setHeaderText("登出确认");
        confirmAlert.setContentText("您确定要登出吗？");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // 清除用户会话
                UserSession.getInstance().clearSession();
                
                // 关闭当前主界面窗口
                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.close();
                
                // 重新打开登录界面
                showLoginView();
            }
        });
    }
    
    /**
     * 显示登录界面
     */
    private void showLoginView() {
        try {
            Stage loginStage = new Stage();
            
            // 加载登录界面的FXML文件
            URL fxmlLocation = getClass().getResource("/fxml/LoginView.fxml");
            if (fxmlLocation == null) {
                System.err.println("严重错误: 找不到登录界面FXML文件 /fxml/LoginView.fxml");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            
            // 加载CSS样式
            URL cssLocation = getClass().getResource("/css/styles.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            }
            
            // 设置并显示登录界面窗口
            loginStage.setTitle("VCampus 客户端 - 登录");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.setMinWidth(400);
            loginStage.setMinHeight(500);
            loginStage.centerOnScreen();
            loginStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("加载登录界面时发生错误: " + e.getMessage());
        }
    }

    /**
     * 当用户点击"首页"导航按钮时调用。
     * @param event 点击事件
     */
    @FXML
    void handleHomeNav(ActionEvent event) {
        loadView("/fxml/WelcomeView.fxml");
    }

    /**
     * 当用户点击"商店"导航按钮时调用。
     * @param event 点击事件
     */
    @FXML
    void handleStoreNav(ActionEvent event) {
        // ------------------ 接口点: MainViewController -> StoreView.fxml ------------------
        loadView("/fxml/store/StoreView.fxml");
        // ------------------------------------------------------------------------------
    }

    // ... 为 Library, Academic, StudentRecord 重复类似的方法 ...
    @FXML
    void handleLibraryNav(ActionEvent event) {
        loadView("/fxml/library/LibraryView.fxml");
    }



    @FXML
    void handleStudentRecordNav(ActionEvent event) {
        loadView("/fxml/studentrecord/StudentRecordView.fxml");
    }
    
    @FXML
    void handleAcademicNav(ActionEvent event) {
        loadView("/fxml/academic/AcademicView.fxml");
    }
    
    @FXML
    void handleEmailNav(ActionEvent event) {
        loadView("/fxml/email/EmailView.fxml");
    }

    /**
     * 一个通用的、可复用的方法，用于将指定的FXML视图加载到主内容面板中。
     * 这是实现动态页面切换的核心。
     *
     * @param fxmlPath 要加载的FXML文件的资源路径 (例如, "/fxml/user/UserView.fxml")
     */
    private void loadView(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("错误: 找不到视图文件 " + fxmlPath);
                return;
            }
            // 加载FXML，这将实例化该FXML对应的控制器（如UserController）
            Node view = FXMLLoader.load(fxmlUrl);
            
            // 设置锚点约束，让子视图占满整个AnchorPane
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            // 将加载好的视图设置为中央内容区的唯一子节点，实现页面切换
            mainContentPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("加载视图时发生错误: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowChatView() {
        loadView("/fxml/library/ChatView.fxml");
    }
    /**
     * 初始化侧边栏动画
     */
    private void initializeSidebar() {
        // 设置初始状态为折叠
        sidebarContainer.setPrefWidth(60.0);
        updateButtonTexts();
        
        // 创建展开动画
        expandTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(sidebarContainer.prefWidthProperty(), 60.0)),
            new KeyFrame(Duration.millis(300), new KeyValue(sidebarContainer.prefWidthProperty(), 180.0))
        );
        
        // 创建折叠动画
        collapseTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(sidebarContainer.prefWidthProperty(), 180.0)),
            new KeyFrame(Duration.millis(300), new KeyValue(sidebarContainer.prefWidthProperty(), 60.0))
        );
        
        // 设置鼠标事件监听器
        sidebarContainer.setOnMouseEntered(e -> expandSidebar());
        sidebarContainer.setOnMouseExited(e -> collapseSidebar());
    }
    
    /**
     * 展开侧边栏
     */
    private void expandSidebar() {
        if (!isSidebarExpanded) {
            isSidebarExpanded = true;
            expandTimeline.play();
            // 延迟更新按钮文本，让动画更流畅
            Timeline delayTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), e -> updateButtonTexts())
            );
            delayTimeline.play();
        }
    }
    
    /**
     * 折叠侧边栏
     */
    private void collapseSidebar() {
        if (isSidebarExpanded) {
            isSidebarExpanded = false;
            collapseTimeline.play();
            // 立即更新按钮文本
            updateButtonTexts();
        }
    }
    
    /**
     * 更新按钮文本（根据侧边栏状态显示图标或完整文本）
     */
    private void updateButtonTexts() {
        if (isSidebarExpanded) {
            homeButton.setText("🏠 首页");
            storeButton.setText("🛒 商店");
            libraryButton.setText("📚 图书馆");
            studentRecordButton.setText("📋 学籍管理");
            academicButton.setText("🎓 教务管理");
            emailButton.setText("📧 邮件系统");
            chatButton.setText("AI助手");
        } else {
            homeButton.setText("🏠");
            storeButton.setText("🛒");
            libraryButton.setText("📚");
            studentRecordButton.setText("📋");
            academicButton.setText("🎓");
            emailButton.setText("📧");
            // 【新增】当侧边栏折叠时，清空AI助手按钮的文本（只留图标）
            chatButton.setText("");
        }
    }
}