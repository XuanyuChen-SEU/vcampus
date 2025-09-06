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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
    
    // 登出按钮
    @FXML
    private Button logoutButton;

    /**
     * 初始化方法，由JavaFX在FXML文件加载完成后自动调用。
     * 用于设置应用程序的初始状态，例如加载默认的欢迎页面。
     */
    @FXML
    public void initialize() {
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
                userInfoLabel.setText("当前用户: " + userSession.getCurrentUserDisplayName());
            } else {
                userInfoLabel.setText("未登录");
            }
        }
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
            // 将加载好的视图设置为中央内容区的唯一子节点，实现页面切换
            mainContentPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("加载视图时发生错误: " + e.getMessage());
        }
    }
}