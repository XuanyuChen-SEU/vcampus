package com.vcampus.client.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 登录界面测试类
 * 用于测试角色跳转功能
 */
public class LoginRoleTest extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 加载登录界面FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(loader.load());
        
        // 加载CSS样式
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        // 设置窗口属性
        primaryStage.setTitle("VCampus 登录测试 - 角色跳转功能");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(500);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
