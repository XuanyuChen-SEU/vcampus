package com.vcampus.server.net;

import com.vcampus.common.dto.Message;
import com.vcampus.server.controller.MessageController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Socket服务器类
 * 处理客户端连接和消息通信
 * 编写人：cursor
 */
public class SocketServer {
    
    private static final int PORT = 9090;
    private static final int MAX_CLIENTS = 100;
    
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning = false;
    private final MessageController messageController;
    private final List<ClientConnection> clientConnections;
    
    public SocketServer() {
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.messageController = new MessageController();
        this.clientConnections = new CopyOnWriteArrayList<>();
    }
    
    /**
     * 启动服务器
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("服务器启动成功，监听端口: " + PORT);
            
            // 接受客户端连接
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接: " + clientSocket.getInetAddress().getHostAddress());
                
                // 为每个客户端创建独立的处理线程
                threadPool.submit(() -> handleClient(clientSocket));
            }
            
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * 停止服务器
     */
    public void stop() {
        isRunning = false;
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("关闭服务器Socket失败: " + e.getMessage());
            }
        }
        
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
        
        System.out.println("服务器已停止");
    }
    
    /**
     * 处理客户端连接
     * @param clientSocket 客户端Socket
     */
    private void handleClient(Socket clientSocket) {
        ClientConnection clientConnection = null;
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            
            // 创建客户端连接对象并添加到连接列表
            clientConnection = new ClientConnection(clientSocket, out);
            clientConnections.add(clientConnection);
            System.out.println("客户端连接: " + clientSocket.getInetAddress().getHostAddress() + 
                             " (当前连接数: " + clientConnections.size() + ")");
            
            while (isRunning && !clientSocket.isClosed()) {
                try {
                    // 读取客户端消息
                    Message request = (Message) in.readObject();
                    System.out.println("收到客户端消息: " + request.toString());
                    
                    // 委托给Controller处理消息
                    Message response = messageController.handleMessage(request);
                    
                    // 发送响应给客户端
                    out.writeObject(response);
                    out.flush();
                    
                    System.out.println("发送响应: " + response.getAction() + " - " + response.isSuccess());
                    
                } catch (EOFException e) {
                    // 客户端断开连接
                    System.out.println("客户端断开连接: " + clientSocket.getInetAddress().getHostAddress());
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("消息反序列化失败: " + e.getMessage());
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("处理客户端连接时发生错误: " + e.getMessage());
        } finally {
            // 从连接列表中移除
            if (clientConnection != null) {
                clientConnections.remove(clientConnection);
                System.out.println("客户端断开: " + clientSocket.getInetAddress().getHostAddress() + 
                                 " (当前连接数: " + clientConnections.size() + ")");
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("关闭客户端Socket失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 广播消息给所有客户端
     * @param message 要广播的消息
     */
    public void broadcastMessage(Message message) {
        System.out.println("广播消息: " + message.getAction() + " 给 " + clientConnections.size() + " 个客户端");
        
        // 使用CopyOnWriteArrayList的迭代器，避免并发修改异常
        for (ClientConnection connection : clientConnections) {
            try {
                connection.sendMessage(message);
            } catch (Exception e) {
                System.err.println("广播消息失败: " + e.getMessage());
                // 标记连接为无效，下次迭代时会清理
                connection.markInvalid();
            }
        }
        
        // 清理无效连接
        clientConnections.removeIf(ClientConnection::isInvalid);
    }
    
    /**
     * 获取当前连接的客户端数量
     * @return 客户端数量
     */
    public int getClientCount() {
        return clientConnections.size();
    }
    
    /**
     * 客户端连接内部类
     */
    private static class ClientConnection {
        private final Socket socket;
        private final ObjectOutputStream out;
        private volatile boolean invalid = false;
        
        public ClientConnection(Socket socket, ObjectOutputStream out) {
            this.socket = socket;
            this.out = out;
        }
        
        public void sendMessage(Message message) throws IOException {
            if (!invalid && !socket.isClosed()) {
                out.writeObject(message);
                out.flush();
            }
        }
        
        public void markInvalid() {
            this.invalid = true;
        }
        
        public boolean isInvalid() {
            return invalid || socket.isClosed();
        }
    }
    

    
    /**
     * 检查服务器是否正在运行
     * @return 运行状态
     */
    public boolean isRunning() {
        return isRunning;
    }
}
