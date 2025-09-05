package com.vcampus.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

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

    /**
     * 初始化方法，由JavaFX在FXML文件加载完成后自动调用。
     * 用于设置应用程序的初始状态，例如加载默认的欢迎页面。
     */
    @FXML
    public void initialize() {
        // ------------------ 接口点: MainViewController -> WelcomeView.fxml ------------------
        loadView("/fxml/WelcomeView.fxml"); // 默认加载欢迎界面
        // --------------------------------------------------------------------------------
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