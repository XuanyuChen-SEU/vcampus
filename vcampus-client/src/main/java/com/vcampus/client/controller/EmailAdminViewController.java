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
 * 邮件管理员框架控制器
 * 负责导航栏和子视图管理
 * 编写人：AI Assistant
 */
public class EmailAdminViewController {

    // 导航按钮
    @FXML
    private Button allEmailsButton;
    
    @FXML
    private Button userEmailsButton;
    
    @FXML
    private Button emailStatisticsButton;
    
    @FXML
    private Button emailSearchButton;
    
    // 子视图容器
    @FXML
    private AnchorPane subViewContainer;
    
    // 当前选中的视图
    private String currentView = "allEmails";

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 默认加载所有邮件视图
        loadAllEmailsView();
        updateButtonStyles();
    }
    
    /**
     * 处理所有邮件按钮点击
     */
    @FXML
    private void handleAllEmails(ActionEvent event) {
        currentView = "allEmails";
        loadAllEmailsView();
        updateButtonStyles();
    }
    
    /**
     * 处理用户邮件按钮点击
     */
    @FXML
    private void handleUserEmails(ActionEvent event) {
        currentView = "userEmails";
        loadUserEmailsView();
        updateButtonStyles();
    }
    
    /**
     * 处理邮件统计按钮点击
     */
    @FXML
    private void handleEmailStatistics(ActionEvent event) {
        currentView = "emailStatistics";
        loadEmailStatisticsView();
        updateButtonStyles();
    }
    
    /**
     * 处理邮件搜索按钮点击
     */
    @FXML
    private void handleEmailSearch(ActionEvent event) {
        currentView = "emailSearch";
        loadEmailSearchView();
        updateButtonStyles();
    }
    
    /**
     * 加载所有邮件视图
     */
    private void loadAllEmailsView() {
        loadSubView("/fxml/admin/email/AllEmailsView.fxml");
    }
    
    /**
     * 加载用户邮件视图
     */
    private void loadUserEmailsView() {
        loadSubView("/fxml/admin/email/UserEmailsView.fxml");
    }
    
    /**
     * 加载邮件统计视图
     */
    private void loadEmailStatisticsView() {
        loadSubView("/fxml/admin/email/EmailStatisticsView.fxml");
    }
    
    /**
     * 加载邮件搜索视图
     */
    private void loadEmailSearchView() {
        loadSubView("/fxml/admin/email/EmailSearchView.fxml");
    }
    
    /**
     * 通用的子视图加载方法
     */
    private void loadSubView(String fxmlPath) {
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
            
            // 将加载好的视图设置为子视图容器的唯一子节点
            subViewContainer.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("加载子视图时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 更新按钮样式，突出显示当前选中的按钮
     */
    private void updateButtonStyles() {
        // 清除所有按钮的选中样式
        allEmailsButton.getStyleClass().remove("nav-button-active");
        userEmailsButton.getStyleClass().remove("nav-button-active");
        emailStatisticsButton.getStyleClass().remove("nav-button-active");
        emailSearchButton.getStyleClass().remove("nav-button-active");
        
        // 为当前选中的按钮添加选中样式
        switch (currentView) {
            case "allEmails":
                allEmailsButton.getStyleClass().add("nav-button-active");
                break;
            case "userEmails":
                userEmailsButton.getStyleClass().add("nav-button-active");
                break;
            case "emailStatistics":
                emailStatisticsButton.getStyleClass().add("nav-button-active");
                break;
            case "emailSearch":
                emailSearchButton.getStyleClass().add("nav-button-active");
                break;
        }
    }
}
