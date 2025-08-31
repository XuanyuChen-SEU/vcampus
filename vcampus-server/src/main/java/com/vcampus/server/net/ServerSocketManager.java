package com.vcampus.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务端Socket管理器
 * 负责启动服务器、监听客户端连接并创建客户端处理器
 * 编写人：谌宣羽
 */
public class ServerSocketManager {
    
    private static final int DEFAULT_PORT = 8888;
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    
    private final int port;
    private final int threadPoolSize;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private final AtomicBoolean isRunning;
    private final IMessageServerSrv messageServer;
    private final ConcurrentHashMap<String, ClientHandler> activeConnections;
    
    public ServerSocketManager() {
        this(DEFAULT_PORT, DEFAULT_THREAD_POOL_SIZE);
    }
    
    public ServerSocketManager(int port) {
        this(port, DEFAULT_THREAD_POOL_SIZE);
    }
    
    public ServerSocketManager(int port, int threadPoolSize) {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.isRunning = new AtomicBoolean(false);
        this.messageServer = new MessageServerSrvImpl();
        this.activeConnections = new ConcurrentHashMap<>();
    }
    
    /**
     * 启动服务器
     */
    public void start() {
        if (isRunning.get()) {
            return;
        }
        
        try {
            // 创建ServerSocket
            serverSocket = new ServerSocket(port);
            
            // 创建线程池
            threadPool = Executors.newFixedThreadPool(threadPoolSize);
            
            // 启动消息服务
            messageServer.start();
            
            // 设置运行状态
            isRunning.set(true);
            
            // 开始监听客户端连接
            acceptConnections();
            
        } catch (IOException e) {
            stop();
        }
    }
    
    /**
     * 停止服务器
     */
    public void stop() {
        if (!isRunning.get()) {
            return;
        }
        
        // 设置停止状态
        isRunning.set(false);
        
        // 关闭所有活跃连接
        closeAllConnections();
        
        // 停止消息服务
        if (messageServer != null) {
            messageServer.stop();
        }
        
        // 关闭线程池
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            // 等待线程池完全关闭
            try {
                if (!threadPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // 关闭ServerSocket
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // 关闭异常
            }
        }
    }
    
    /**
     * 接受客户端连接
     */
    private void acceptConnections() {
        while (isRunning.get() && !serverSocket.isClosed()) {
            try {
                // 等待客户端连接
                Socket clientSocket = serverSocket.accept();
                
                // 创建客户端处理器并提交到线程池
                ClientHandler clientHandler = new ClientHandler(clientSocket, messageServer);
                
                // 注册连接
                String clientKey = getClientKey(clientSocket);
                activeConnections.put(clientKey, clientHandler);
                
                // 提交到线程池
                threadPool.submit(() -> {
                    try {
                        clientHandler.run();
                    } finally {
                        // 连接结束后自动移除
                        activeConnections.remove(clientKey);
                    }
                });
                
            } catch (IOException e) {
                if (isRunning.get()) {
                    // 接受连接异常
                }
            }
        }
    }
    
    /**
     * 关闭所有活跃连接
     */
    private void closeAllConnections() {
        activeConnections.values().forEach(ClientHandler::stop);
        activeConnections.clear();
    }
    
    /**
     * 关闭指定客户端连接
     * @param clientKey 客户端标识
     */
    public void closeConnection(String clientKey) {
        ClientHandler handler = activeConnections.remove(clientKey);
        if (handler != null) {
            handler.stop();
        }
    }
    
    /**
     * 获取客户端标识
     * @param socket 客户端Socket
     * @return 客户端标识
     */
    private String getClientKey(Socket socket) {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }
    
    /**
     * 获取活跃连接数量
     * @return 活跃连接数量
     */
    public int getActiveConnectionCount() {
        return activeConnections.size();
    }
    
    /**
     * 检查服务器是否正在运行
     * @return 如果正在运行返回true，否则返回false
     */
    public boolean isRunning() {
        return isRunning.get();
    }
    
    /**
     * 获取服务器端口
     * @return 服务器端口
     */
    public int getPort() {
        return port;
    }
    
    /**
     * 获取线程池大小
     * @return 线程池大小
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
}
