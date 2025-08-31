package com.vcampus.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.vcampus.common.dto.Message;

/**
 * 客户端连接处理器
 * 负责处理单个客户端连接的消息收发
 * 编写人：谌宣羽
 */
public class ClientHandler implements Runnable {
    
    private final Socket clientSocket;
    private final IMessageServerSrv messageServer;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean isRunning;
    private String connectionId;
    private String userId;
    
    public ClientHandler(Socket clientSocket, IMessageServerSrv messageServer) {
        this.clientSocket = clientSocket;
        this.messageServer = messageServer;
        this.isRunning = true;
    }
    
    @Override
    public void run() {    
        try {
            // 初始化输入输出流
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            
            // 注册到连接管理器
            connectionId = ConnectionManager.getInstance().registerConnection(this);
            
            // 发送连接成功消息
            sendMessage(Message.success(null, "连接成功"));
            
            // 开始处理消息
            while (isRunning && !clientSocket.isClosed()) {
                try {
                    // 读取客户端消息
                    Message request = (Message) inputStream.readObject();
                    
                    // 处理消息
                    Message response = messageServer.handleMessage(request);
                    
                    // 发送响应
                    sendMessage(response);
                    
                } catch (ClassNotFoundException e) {
                    sendMessage(Message.failure(null, "消息格式错误"));
                } catch (IOException e) {
                    if (isRunning) {
                        // 连接异常，退出循环
                    }
                    break;
                }
            }
            
        } catch (IOException e) {
            // 连接建立失败
        } finally {
            // 注销连接
            if (connectionId != null) {
                ConnectionManager.getInstance().unregisterConnection(connectionId);
            }
            // 清理资源
            cleanup();
        }
    }
    
    /**
     * 发送消息给客户端
     * @param message 要发送的消息
     * @return 发送是否成功
     */
    public boolean sendMessage(Message message) {
        try {
            if (outputStream != null && isRunning && !clientSocket.isClosed()) {
                outputStream.writeObject(message);
                outputStream.flush();
                return true;
            } else {
                // 记录发送失败的原因
                if (outputStream == null) {
                    System.out.println("客户端 " + getClientAddress() + " 输出流未初始化");
                } else if (!isRunning) {
                    System.out.println("客户端 " + getClientAddress() + " 处理器已停止");
                } else if (clientSocket.isClosed()) {
                    System.out.println("客户端 " + getClientAddress() + " Socket已关闭");
                }
                return false;
            }
        } catch (IOException e) {
            System.out.println("客户端 " + getClientAddress() + " 发送消息失败: " + e.getMessage());
            isRunning = false;
            return false;
        } catch (Exception e) {
            System.out.println("客户端 " + getClientAddress() + " 发送消息时发生未知异常: " + e.getMessage());
            isRunning = false;
            return false;
        }
    }
    
    /**
     * 停止处理器
     */
    public void stop() {
        isRunning = false;
        cleanup();
    }
    
    /**
     * 清理资源
     */
    private void cleanup() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            // 清理资源异常
        }
    }
    
    /**
     * 检查处理器是否正在运行
     * @return 如果正在运行返回true，否则返回false
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * 获取客户端地址
     * @return 客户端IP地址
     */
    public String getClientAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }
    
    /**
     * 获取连接ID
     * @return 连接ID
     */
    public String getConnectionId() {
        return connectionId;
    }
    
    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * 获取用户ID
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }
}
