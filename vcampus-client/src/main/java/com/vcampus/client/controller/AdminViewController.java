package com.vcampus.client.controller;

import java.io.IOException;
import java.net.URL;

import com.vcampus.client.MainApp;
import com.vcampus.client.session.UserSession;

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
import javafx.stage.Stage;

/**
 * 通用管理员界面控制器
 * 负责管理员框架的通用功能，支持动态加载不同的管理员内容
 * 编写人：AI Assistant
 */
public class AdminViewController {

    // 主内容面板
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
    
    // 回到主页按钮
    @FXML
    private Button backToHomeButton;
    
    // 登出按钮
    @FXML
    private Button logoutButton;

    /**
     * 初始化方法，由JavaFX在FXML文件加载完成后自动调用
     */
    @FXML
    public void initialize() {
        // 显示当前用户信息
        updateUserInfo();
        
        // 验证用户权限
        validateAdminAccess();
        
        // 根据用户角色加载相应的内容
        loadAdminContent();
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
     * 验证管理员访问权限
     */
    private void validateAdminAccess() {
        UserSession userSession = MainApp.getGlobalUserSession();
        if (!userSession.isLoggedIn() || !userSession.hasRole(com.vcampus.common.enums.Role.ADMIN)) {
            // 如果不是管理员，显示错误并返回登录界面
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("访问被拒绝");
            alert.setHeaderText("权限不足");
            alert.setContentText("您没有管理员权限，无法访问此页面。");
            alert.showAndWait();
            
            // 返回登录界面
            showLoginView();
        }
    }
    
    /**
     * 根据用户角色加载相应的管理员内容
     */
    private void loadAdminContent() {
        UserSession userSession = MainApp.getGlobalUserSession();
        if (userSession.isLoggedIn()) {
            String userId = userSession.getCurrentUserId();
            String firstChar = userId.substring(0, 1);
            
            String contentPath;
            switch (firstChar) {
                case "3": // 用户管理员
                    contentPath = "/fxml/admin/UserAdminView.fxml";
                    break;
                case "4": // 系统管理员
                    contentPath = "/fxml/admin/SystemAdminView.fxml";
                    break;
                case "5": // 内容管理员
                    contentPath = "/fxml/admin/ContentAdminView.fxml";
                    break;
                case "6": // 财务管理员
                    contentPath = "/fxml/admin/FinanceAdminView.fxml";
                    break;
                default: // 默认用户管理员
                    contentPath = "/fxml/admin/UserAdminView.fxml";
                    break;
            }
            
            loadView(contentPath);
        }
    }
    
    
    /**
     * 处理回到主页按钮点击事件
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        // 重新加载管理员内容，回到主页
        loadAdminContent();
    }
    
    /**
     * 处理登出按钮点击事件
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
     * 通用的视图加载方法
     */
    private void loadView(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("错误: 找不到视图文件 " + fxmlPath);
                return;
            }
            
            // 加载FXML
            Node view = FXMLLoader.load(fxmlUrl);
            
            // 设置锚点约束，让子视图占满整个AnchorPane
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            // 将加载好的视图设置为中央内容区的唯一子节点
            mainContentPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("加载视图时发生错误: " + e.getMessage());
        }
    }
}
