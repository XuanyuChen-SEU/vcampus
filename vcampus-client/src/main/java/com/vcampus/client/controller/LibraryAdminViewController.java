package com.vcampus.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

/**
 * 图书管理员框架控制器
 * 负责导航栏和子视图管理 (参照 UserAdminViewController 编写)
 */
public class LibraryAdminViewController {

    // 导航按钮
    @FXML
    private Button bookListButton;
    @FXML
    private Button createBookButton;
    @FXML
    private Button borrowLogListButton;
    @FXML
    private Button createBorrowLogButton;

    // 子视图容器
    @FXML
    private AnchorPane subViewContainer;

    // 当前选中的视图名称
    private String currentView = "bookList";

    /**
     * 初始化方法，在FXML加载完成后自动调用
     */
    @FXML
    public void initialize() {
        // 默认加载图书列表视图
        loadBookListView();
        updateButtonStyles();
    }

    /**
     * 处理“图书列表”按钮点击事件
     */
    @FXML
    private void handleBookList(ActionEvent event) {
        currentView = "bookList";
        loadBookListView();
        updateButtonStyles();
    }

    /**
     * 处理“创建图书”按钮点击事件
     */
    @FXML
    private void handleCreateBook(ActionEvent event) {
        currentView = "createBook";
        loadCreateBookView();
        updateButtonStyles();
    }

    /**
     * 处理“借阅记录”按钮点击事件
     */
    @FXML
    private void handleBorrowLogList(ActionEvent event) {
        currentView = "borrowLogList";
        loadBorrowLogListView();
        updateButtonStyles();
    }

    /**
     * 处理“创建借阅记录”按钮点击事件
     */
    @FXML
    private void handleCreateBorrowLog(ActionEvent event) {
        currentView = "createBorrowLog";
        loadCreateBorrowLogView();
        updateButtonStyles();
    }

    // ==========================================================
    // 子视图加载方法
    // ==========================================================

    private void loadBookListView() {
        // 假设您的图书列表FXML文件在这个路径
        loadSubView("/fxml/admin/Library/BookListView.fxml");
    }

    private void loadCreateBookView() {
        // 假设您的创建图书FXML文件在这个路径
        loadSubView("/fxml/admin/Library/BookCreatView.fxml");
    }

    private void loadBorrowLogListView() {
        // 假设您的借阅记录FXML文件在这个路径
        loadSubView("/fxml/admin/Library/BorrowLogListView.fxml");
    }

    private void loadCreateBorrowLogView() {
        // 假设您的创建借阅记录FXML文件在这个路径
        loadSubView("/fxml/admin/Library/BorrowLogCreateView.fxml");
    }

    /**
     * 通用的子视图加载器
     * @param fxmlPath 子视图的FXML文件路径
     */
    private void loadSubView(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("错误: 找不到视图文件 " + fxmlPath);
                return;
            }

            Node view = FXMLLoader.load(fxmlUrl);

            // 设置锚点约束，让子视图占满整个容器
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            // 将加载好的视图设置为子视图容器的唯一子节点
            subViewContainer.getChildren().setAll(view);

        } catch (IOException e) {
            System.err.println("加载子视图时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据 currentView 的值，更新导航按钮的样式
     * 使当前选中的按钮高亮显示
     */
    private void updateButtonStyles() {
        // 首先重置所有按钮的样式，移除高亮样式
        bookListButton.getStyleClass().remove("nav-button-active");
        createBookButton.getStyleClass().remove("nav-button-active");
        borrowLogListButton.getStyleClass().remove("nav-button-active");
        createBorrowLogButton.getStyleClass().remove("nav-button-active");

        // 然后为当前选中的按钮添加高亮样式
        switch (currentView) {
            case "bookList":
                bookListButton.getStyleClass().add("nav-button-active");
                break;
            case "createBook":
                createBookButton.getStyleClass().add("nav-button-active");
                break;
            case "borrowLogList":
                borrowLogListButton.getStyleClass().add("nav-button-active");
                break;
            case "createBorrowLog":
                createBorrowLogButton.getStyleClass().add("nav-button-active");
                break;
        }
    }
}