package com.vcampus.client.controller;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * 用户管理员框架控制器
 * 负责导航栏和子视图管理
 * 编写人：谌宣羽
 */
public class UserAdminViewController {

    // 导航按钮
    @FXML
    private Button userListButton;
    
    @FXML
    private Button createUserButton;
    
    @FXML
    private Button resetPasswordButton;
    
    // 子视图容器
    @FXML
    private AnchorPane subViewContainer;
    
    // 当前选中的视图
    private String currentView = "userList";

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 默认加载用户列表视图
        loadUserListView();
        updateButtonStyles();
    }
    
    /**
     * 处理用户列表按钮点击
     */
    @FXML
    private void handleUserList(ActionEvent event) {
        currentView = "userList";
        loadUserListView();
        updateButtonStyles();
    }
    
    /**
     * 处理创建用户按钮点击
     */
    @FXML
    private void handleCreateUser(ActionEvent event) {
        currentView = "createUser";
        loadCreateUserView();
        updateButtonStyles();
    }
    
    /**
     * 处理重置密码按钮点击
     */
    @FXML
    private void handleResetPassword(ActionEvent event) {
        currentView = "resetPassword";
        loadResetPasswordView();
        updateButtonStyles();
    }
    
    /**
     * 加载用户列表视图
     */
    private void loadUserListView() {
        loadSubView("/fxml/admin/user/UserListView.fxml", null);
    }
    
    /**
     * 加载创建用户视图
     */
    private void loadCreateUserView() {
        loadSubView("/fxml/admin/user/UserCreateView.fxml", null);
    }
    
    /**
     * 加载重置密码视图
     */
    private void loadResetPasswordView() {
        loadSubView("/fxml/admin/user/UserPasswordResetView.fxml", null);
    }
    
    /**
     * 加载子视图（带参数）
     */
    public void loadSubView(String fxmlPath, String userId) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("错误: 找不到视图文件 " + fxmlPath);
                return;
            }
            
            // 加载FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Node view = loader.load();
            
            // 设置控制器引用，以便子控制器能够找到父控制器
            Object controller = loader.getController();
            if (controller != null) {
                // 将父控制器引用存储到子视图的userData中
                view.setUserData(this);
            }
            
            // 如果是重置密码视图且有用户ID参数，则设置用户ID
            if (fxmlPath.contains("UserPasswordResetView") && userId != null) {
                if (controller instanceof com.vcampus.client.controller.userAdmin.UserPasswordResetViewController) {
                    ((com.vcampus.client.controller.userAdmin.UserPasswordResetViewController) controller).setUserId(userId);
                }
            }
            
            // 设置锚点约束，让子视图占满整个容器
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            // 将加载好的视图设置为子视图容器的唯一子节点
            subViewContainer.getChildren().setAll(view);
            
        } catch (IOException e) {
            System.err.println("加载子视图时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 设置当前视图状态
     */
    public void setCurrentView(String viewName) {
        currentView = viewName;
        updateButtonStyles();
    }
    
    /**
     * 更新按钮样式
     */
    private void updateButtonStyles() {
        // 重置所有按钮样式
        userListButton.getStyleClass().removeAll("nav-button-active");
        createUserButton.getStyleClass().removeAll("nav-button-active");
        resetPasswordButton.getStyleClass().removeAll("nav-button-active");
        
        // 为当前选中的按钮添加激活样式
        switch (currentView) {
            case "userList":
                userListButton.getStyleClass().add("nav-button-active");
                break;
            case "createUser":
                createUserButton.getStyleClass().add("nav-button-active");
                break;
            case "resetPassword":
                resetPasswordButton.getStyleClass().add("nav-button-active");
                break;
        }
    }
}
