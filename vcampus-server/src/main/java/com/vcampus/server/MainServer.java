package com.vcampus.server;

import com.vcampus.server.net.SocketServer;

/**
 * 主服务器类
 * 服务端程序的入口点，启动Socket服务器
 * 编写人：cursor
 */
public class MainServer {
    
    public static void main(String[] args) {
        System.out.println("=== VCampus 服务器启动 ===");
        
        // 创建并启动Socket服务器
        SocketServer server = new SocketServer();
        
        // 添加关闭钩子，确保程序退出时正确关闭服务器
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在关闭服务器...");
            server.stop();
        }));
        
        try {
            // 启动服务器
            server.start();
        } catch (Exception e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            System.exit(1);
        }
    }
}
