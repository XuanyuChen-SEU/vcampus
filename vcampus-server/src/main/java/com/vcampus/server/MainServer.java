package com.vcampus.server;

import com.vcampus.server.net.SocketServer;

/**
 * 主服务器类
 * 服务端程序的入口点，启动Socket服务器
 * 编写人：谌宣羽
 */
public class MainServer {
    private static MainServer instance;//自己的一个类实例
    private static SocketServer server;//Socket服务器实例

    public static SocketServer getGlobalSocketServer() {
        return server;
    }
    public static MainServer getInstance() {
        return instance;
    }

    public static void main(String[] args) {

        System.out.println("=== VCampus 服务器启动 ===");
        instance = new MainServer();
        // 创建并启动Socket服务器
        server = new SocketServer();
        
        // 添加关闭钩子，确保程序退出时正确关闭服务器
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在关闭服务器...");
            instance.server.stop();
        }));
        
        try {
            // 启动服务器
            instance.server.start();
        } catch (Exception e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            System.exit(1);
        }
    }
}
