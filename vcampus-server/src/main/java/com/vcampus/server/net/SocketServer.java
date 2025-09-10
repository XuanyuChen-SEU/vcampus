package com.vcampus.server.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vcampus.common.dto.Message;
import com.vcampus.server.controller.MessageController;

/**
 * Socket服务器类（双线程池版本）
 * 连接池 + 工作池
 * 编写人：谌宣羽、周蔚钺
 */
public class SocketServer {

    private static final int PORT = 9090;
    private static final int MAX_CONNECTIONS = 50;   // 最大同时连接数

    private ServerSocket serverSocket;    //#已有
    private ExecutorService connectionPool;  // 接收连接的线程池  #已有
    private ExecutorService workerPool;      // 处理消息的线程池   #已有
    private volatile boolean isRunning = false;            // 服务器运行状态
    private final MessageController messageController;
    // 客户端连接列表（用CopyOnWriteArrayList保证并发安全，适合频繁遍历+修改的场景）
    private final List<ClientConnection> clientConnections;

    public SocketServer() {
        // 固定大小的连接池（保证最多同时处理的客户端数）
        this.connectionPool = Executors.newFixedThreadPool(MAX_CONNECTIONS);

        // 缓存线程池（弹性扩展，适合不同客户端消息高峰）
        this.workerPool = Executors.newCachedThreadPool();

        this.messageController = new MessageController();
        this.clientConnections = new CopyOnWriteArrayList<>();
    }

    /**
     * 启动服务器
     */
    public void start() {
        try {
            // 绑定端口并启动服务器Socket
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("服务器启动成功，监听端口: " + PORT);

            // 接受客户端连接
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();// 等待客户端连接
                System.out.println("客户端连接: " + clientSocket.getInetAddress().getHostAddress());

                // 交给连接线程池
                connectionPool.submit(() -> handleClient(clientSocket));
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

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器Socket失败: " + e.getMessage());
        }

        if (connectionPool != null && !connectionPool.isShutdown()) {
            connectionPool.shutdown();
        }
        if (workerPool != null && !workerPool.isShutdown()) {
            workerPool.shutdown();
        }

        System.out.println("服务器已停止");
    }

    /**
     * 处理客户端连接（消息收发循环）
     */
    private void handleClient(Socket clientSocket) {
        ClientConnection clientConnection = null;
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            // 添加客户端连接
            clientConnection = new ClientConnection(clientSocket, out);
            clientConnections.add(clientConnection);
            System.out.println("客户端连接成功，当前连接数: " + clientConnections.size());

            while (isRunning && !clientSocket.isClosed()) {
                try {
                    // 读取客户端消息
                    Message request = (Message) in.readObject();

                    // 将消息处理交给工作线程池
                    workerPool.submit(() -> {
                        try {
                            System.out.println("收到客户端消息: " + request);
                            // 处理消息
                            Message response = messageController.handleMessage(request);
                            //知道这些方法就行  response一定要注意  服务端一定要注意
                            synchronized (out) { // 确保同一客户端的输出流不乱序
                                out.writeObject(response);
                                out.flush();
                            }
                            System.out.println("发送响应: " + response.getAction() + " - " + response.isSuccess());
                        } catch (IOException e) {
                            System.err.println("发送响应失败: " + e.getMessage());
                        }
                    });

                } catch (EOFException e) {
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
            // 移除连接
            if (clientConnection != null) {
                clientConnections.remove(clientConnection);
                System.out.println("客户端断开，当前连接数: " + clientConnections.size());
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
     */
    public void broadcastMessage(Message message) {
        System.out.println("广播消息: " + message.getAction() + " 给 " + clientConnections.size() + " 个客户端");
        for (ClientConnection connection : clientConnections) {
            workerPool.submit(() -> {
                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("广播消息失败: " + e.getMessage());
                    connection.markInvalid();
                }
            });
        }
        clientConnections.removeIf(ClientConnection::isInvalid);
    }

    /**
     * 获取当前连接的客户端数量
     */
    public int getClientCount() {
        return clientConnections.size();
    }

    /**
     * 内部类：客户端连接
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
                synchronized (out) {
                    out.writeObject(message);
                    out.flush();
                }
            }
        }

        public void markInvalid() {
            this.invalid = true;
        }

        public boolean isInvalid() {
            return invalid || socket.isClosed();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
