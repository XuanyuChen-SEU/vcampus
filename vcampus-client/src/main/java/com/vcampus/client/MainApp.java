package com.vcampus.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 客户端主应用程序类
 * JavaFX应用程序的入口点
 * 编写人：cursor
 */
public class MainApp extends Application {
    
    private static final String APP_TITLE = "VCampus 客户端";
    private static final String LOGIN_FXML = "/fxml/LoginView.fxml";
    private static final String MAIN_FXML = "/fxml/MainView.fxml";
    
    private Stage primaryStage;
    private static MainApp instance;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;
        
        // 设置窗口标题
        primaryStage.setTitle(APP_TITLE);
        
        // 设置窗口不可调整大小
        primaryStage.setResizable(false);
        
        // 加载登录界面
        showLoginView();
        
        // 显示主窗口
        primaryStage.show();
        
        System.out.println("=== VCampus 客户端启动成功 ===");
    }
    
    /**
     * 显示登录界面
     */
    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            
            // 设置窗口居中显示
            primaryStage.centerOnScreen();
            
        } catch (Exception e) {
            System.err.println("加载登录界面失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 显示主界面
     */
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_FXML));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            
            // 设置窗口居中显示
            primaryStage.centerOnScreen();
            
        } catch (Exception e) {
            System.err.println("加载主界面失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 获取主窗口
     * @return 主窗口Stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * 获取应用程序实例
     * @return 应用程序实例
     */
    public static MainApp getInstance() {
        return instance;
    }
    
    /**
     * 关闭应用程序
     */
    public void closeApp() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
    }
    
    /**
     * 应用程序启动方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== VCampus 客户端启动中 ===");
        
        // 启动JavaFX应用程序
        launch(args);
    }
}
