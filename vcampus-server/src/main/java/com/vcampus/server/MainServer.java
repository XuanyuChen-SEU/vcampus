package com.vcampus.server;

import java.util.concurrent.CountDownLatch;

import com.vcampus.server.net.ServerSocketManager;

/**
 * 服务端主程序
 * 启动服务器并监听客户端连接
 * 编写人：谌宣羽
 */
public class MainServer {
    
    private static final int DEFAULT_PORT = 9090;
    private static ServerSocketManager serverManager;
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    
    public static void main(String[] args) {
        System.out.println("=== VCampus 服务端启动 ===");
        
        try {
            // 解析命令行参数，如果有端口就接入，如果没有端口默认9090
            int port = parsePort(args);
            
            // 创建并启动服务器
            serverManager = new ServerSocketManager(port);
            serverManager.start();
            
            System.out.println("服务器启动成功，监听端口: " + port);
            System.out.println("等待客户端连接...");
            
            // 添加关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("收到关闭信号，正在停止服务器...");
                if (serverManager != null) {
                    serverManager.stop();
                }
                shutdownLatch.countDown(); // 通知主线程可以退出了
                System.out.println("服务器已停止");
            }));
            
            // 等待关闭信号
            shutdownLatch.await();
            
        } catch (Exception e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 解析端口参数
     * @param args 命令行参数
     * @return 端口号
     */
    private static int parsePort(String[] args) {
        if (args.length > 0) {
            try {
                int port = Integer.parseInt(args[0]);
                if (port > 0 && port <= 65535) {
                    return port;
                } else {
                    System.out.println("端口号无效: " + port + "，使用默认端口: " + DEFAULT_PORT);
                }
            } catch (NumberFormatException e) {
                System.out.println("端口参数格式错误: " + args[0] + "，使用默认端口: " + DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }
    
    /**
     * 停止服务器
     */
    public static void stopServer() {
        if (serverManager != null) {
            serverManager.stop();
        }
    }
    
    /**
     * 检查服务器是否正在运行
     * @return 如果正在运行返回true，否则返回false
     */
    public static boolean isServerRunning() {
        return serverManager != null && serverManager.isRunning();
    }
}
