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
 * UserAdminViewController 是用户管理模块的主框架控制器。
 * <p>
 * 主要功能：
 * <ul>
 *     <li>管理导航栏按钮和子视图的切换</li>
 *     <li>加载不同的用户管理子视图（用户列表、创建用户、重置密码、忘记密码申请）</li>
 *     <li>维护当前激活视图状态并更新按钮样式</li>
 * </ul>
 * <p>
 * 每个子视图通过 FXML 文件加载，并占满 subViewContainer 容器。
 * 编写人：谌宣羽
 */
public class UserAdminViewController {

    /** 导航栏按钮：用户列表 */
    @FXML
    private Button userListButton;

    /** 导航栏按钮：创建用户 */
    @FXML
    private Button createUserButton;

    /** 导航栏按钮：重置密码 */
    @FXML
    private Button resetPasswordButton;

    /** 导航栏按钮：忘记密码申请 */
    @FXML
    private Button forgetPasswordTableButton;

    /** 用于承载子视图的容器 */
    @FXML
    private AnchorPane subViewContainer;

    /** 当前选中的子视图标识 */
    private String currentView = "userList";

    /**
     * 初始化方法，在界面加载时自动调用。
     * <p>
     * 默认加载用户列表视图，并设置对应按钮样式。
     */
    @FXML
    public void initialize() {
        loadUserListView();
        updateButtonStyles();
    }

    /**
     * 处理“用户列表”按钮点击事件。
     *
     * @param event 点击事件对象
     */
    @FXML
    private void handleUserList(ActionEvent event) {
        currentView = "userList";
        loadUserListView();
        updateButtonStyles();
    }

    /**
     * 处理“创建用户”按钮点击事件。
     *
     * @param event 点击事件对象
     */
    @FXML
    private void handleCreateUser(ActionEvent event) {
        currentView = "createUser";
        loadCreateUserView();
        updateButtonStyles();
    }

    /**
     * 处理“重置密码”按钮点击事件。
     *
     * @param event 点击事件对象
     */
    @FXML
    private void handleResetPassword(ActionEvent event) {
        currentView = "resetPassword";
        loadResetPasswordView();
        updateButtonStyles();
    }

    /**
     * 处理“忘记密码申请”按钮点击事件。
     *
     * @param event 点击事件对象
     */
    @FXML
    private void handleForgetPasswordTable(ActionEvent event) {
        currentView = "forgetPasswordTable";
        loadForgetPasswordTableView();
        updateButtonStyles();
    }

    /**
     * 加载用户列表子视图。
     */
    private void loadUserListView() {
        loadSubView("/fxml/admin/user/UserListView.fxml", null);
    }

    /**
     * 加载创建用户子视图。
     */
    private void loadCreateUserView() {
        loadSubView("/fxml/admin/user/UserCreateView.fxml", null);
    }

    /**
     * 加载重置密码子视图。
     */
    private void loadResetPasswordView() {
        loadSubView("/fxml/admin/user/UserPasswordResetView.fxml", null);
    }

    /**
     * 加载忘记密码申请子视图。
     */
    private void loadForgetPasswordTableView() {
        loadSubView("/fxml/admin/user/ForgetPasswordTableView.fxml", null);
    }

    /**
     * 加载指定的子视图 FXML，并可传递用户ID参数（仅用于重置密码视图）。
     *
     * @param fxmlPath 子视图 FXML 文件路径
     * @param userId   用户ID参数（仅在重置密码视图中使用，可为 null）
     */
    public void loadSubView(String fxmlPath, String userId) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("错误: 找不到视图文件 " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Node view = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                // 将父控制器引用存储到子视图中，方便子控制器调用
                view.setUserData(this);
            }

            // 如果是重置密码视图且有用户ID参数，则设置用户ID
            if (fxmlPath.contains("UserPasswordResetView") && userId != null) {
                if (controller instanceof com.vcampus.client.controller.userAdmin.UserPasswordResetViewController) {
                    ((com.vcampus.client.controller.userAdmin.UserPasswordResetViewController) controller).setUserId(userId);
                }
            }

            // 设置锚点约束，让子视图填满容器
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            subViewContainer.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("加载子视图时发生错误: " + e.getMessage());
        }
    }

    /**
     * 设置当前视图状态，并更新导航按钮样式。
     *
     * @param viewName 视图标识名称
     */
    public void setCurrentView(String viewName) {
        currentView = viewName;
        updateButtonStyles();
    }

    /**
     * 更新导航按钮样式，使当前选中的按钮高亮。
     */
    private void updateButtonStyles() {
        // 移除所有按钮的激活样式
        userListButton.getStyleClass().removeAll("nav-button-active");
        createUserButton.getStyleClass().removeAll("nav-button-active");
        resetPasswordButton.getStyleClass().removeAll("nav-button-active");
        forgetPasswordTableButton.getStyleClass().removeAll("nav-button-active");

        // 给当前选中按钮添加激活样式
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
            case "forgetPasswordTable":
                forgetPasswordTableButton.getStyleClass().add("nav-button-active");
                break;
        }
    }
}
