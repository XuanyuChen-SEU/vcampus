package com.vcampus.client;

import com.vcampus.client.net.SocketClient;
import com.vcampus.client.session.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 客户端主应用程序类
 * JavaFX应用程序的入口点
 * 编写人：cursorcursor
 */
public class MainApp extends Application {//继承自Application类

    private static final String APP_TITLE = "VCampus 客户端";
    private static final String LOGIN_FXML = "/fxml/LoginView.fxml";
    private static final String MAIN_FXML = "/fxml/MainView.fxml";

    private Stage primaryStage;
    private static MainApp instance;//保证用的都是一个

    // 全局网络连接实例
    private static SocketClient globalSocketClient;
    
    @Override//默认的启动方法（lauch)
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;
        
        // 初始化全局网络连接(用的都是一个socket)
        initializeGlobalNetworkConnection();
        // 加载登录界面FXML
        Parent root = FXMLLoader.load(getClass().getResource(LOGIN_FXML));
        Scene scene = new Scene(root);

        // 应用CSS样式
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * 初始化全局网络连接
     */
    private void initializeGlobalNetworkConnection() {
        try {
            globalSocketClient = new SocketClient();//自己创建（来自net）
            System.out.println("全局网络连接已创建");
            globalSocketClient.connect();//全局网络连接起来//后面封装的不用关心
        } catch (Exception e) {
            System.err.println("初始化全局网络连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取全局网络连接实例
     *
     * @return SocketClient实例
     */
    public static SocketClient getGlobalSocketClient() {
        return globalSocketClient;
    }

    /**
     * 获取全局用户会话实例
     *
     * @return UserSession实例
     */
    public static UserSession getGlobalUserSession() {
        return UserSession.getInstance();
    }

    /**
     * 获取应用程序实例
     *
     * @return MainApp实例
     */
    public static MainApp getInstance() {
        return instance;
    }//

    /**
     * 应用程序启动方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== VCampus 客户端启动中 ===");
        // 启动JavaFX应s用程序（入口）
        launch(args);
    }
}
