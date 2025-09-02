package com.vcampus.server;

import com.vcampus.server.net.SocketServer;

/**
 * 主服务器类
 * 服务端程序的入口点，启动Socket服务器
 * 编写人：谌宣羽
 */
public class MainServer {
    private static MainServer instance;
    private static SocketServer server;

    public static SocketServer getGlobalSocketServer() {
        return server;
    }
    public static MainServer getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        // 设置控制台输出编码
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
            System.setErr(new java.io.PrintStream(System.err, true, "UTF-8"));
        } catch (Exception e) {
            // 如果设置失败，继续运行
        }        
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
