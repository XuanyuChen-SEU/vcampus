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
 * 用户管理员界面控制器
 * 负责用户管理相关功能的具体实现
 * 编写人：AI Assistant
 */
public class UserAdminViewController {

    // 功能按钮
    @FXML
    private Button viewUsersButton;
    
    @FXML
    private Button createUserButton;
    
    @FXML
    private Button resetPasswordButton;

    /**
     * 初始化方法，由JavaFX在FXML文件加载完成后自动调用
     */
    @FXML
    public void initialize() {
        // 初始化完成，无需特殊处理
    }
    
    /**
     * 处理查看所有用户按钮点击事件
     */
    @FXML
    private void handleViewUsers(ActionEvent event) {
        loadView("/fxml/admin/user/UserListView.fxml");
    }

    /**
     * 处理创建新用户按钮点击事件
     */
    @FXML
    private void handleCreateUser(ActionEvent event) {
        loadView("/fxml/admin/user/UserCreateView.fxml");
    }

    /**
     * 处理重置用户密码按钮点击事件
     */
    @FXML
    private void handleResetPassword(ActionEvent event) {
        loadView("/fxml/admin/user/UserPasswordResetView.fxml");
    }
    
    /**
     * 加载视图
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
            
            // 获取AdminView的mainContentPane（类似MainView的方式）
            Node root = viewUsersButton.getScene().getRoot();
            AnchorPane mainContentPane = (AnchorPane) root.lookup("#mainContentPane");
            
            if (mainContentPane != null) {
                // 将加载好的视图设置为中央内容区的唯一子节点，实现页面切换
                mainContentPane.getChildren().setAll(view);
            } else {
                System.err.println("错误: 找不到mainContentPane");
            }
        } catch (IOException e) {
            System.err.println("加载视图时发生错误: " + e.getMessage());
        }
    }
}
