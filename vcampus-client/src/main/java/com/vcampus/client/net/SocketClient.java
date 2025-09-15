package com.vcampus.client.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.vcampus.common.dto.Message;

/**
 * 线程安全的 Socket 客户端
 * 采用同步发送 + 独立接收线程的模式
 * 编写人：谌宣羽,周蔚钺
 */
public class SocketClient implements IMessageClientSrv {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9090;
    private static final int CONNECTION_TIMEOUT = 5000; // 5秒连接超时
    private static final int READ_TIMEOUT = 10000; // 10秒读取超时
    
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private com.vcampus.client.controller.MessageController messageController;
    
    // 异步接收相关
    private Thread receiveThread;
    private volatile boolean running = false;
    
    /**
     * 使用默认配置创建Socket客户端
     */
    public SocketClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * 使用指定配置创建Socket客户端
     * @param host 服务端地址
     * @param port 服务端端口
     */
    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.messageController = new com.vcampus.client.controller.MessageController();
        //公共messageController(全局唯一）
    }
    
    @Override
    public boolean connect() {
        try {
            // 创建Socket连接
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(host, port), CONNECTION_TIMEOUT);
            socket.setSoTimeout(READ_TIMEOUT);
            
            // 创建输入输出流
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("成功连接到服务端: " + host + ":" + port);
            
            // 启动异步接收线程
            startReceiveThread();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("连接服务端失败: " + e.getMessage());
            disconnect();
            return false;
        }
    }
    
    /**
     * 启动异步接收线程
     */
    private void startReceiveThread() {
        running = true;
        receiveThread = new Thread(this::receiveLoop, "ReceiveThread");
        receiveThread.setDaemon(true);
        receiveThread.start();
        System.out.println("异步接收线程已启动");
    }
    
    /**
     * 接收循环 - 优化版本
     */
    private void receiveLoop() {
        while (running && isConnected()) {
            try {
                // 接收消息
                Object obj = in.readObject();
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    // 减少控制台输出，提升性能
                    System.out.println("接收到消息: " + message);
                    
                    // 使用MessageController处理消息
                    if (messageController != null) {
                        messageController.handleMessage(message);
                    }
                } else {
                    System.err.println("接收到无效的消息类型: " + obj.getClass());
                }
            } catch (Exception e) {
                if (running) {
                    // 减少错误日志输出频率
                    if (System.currentTimeMillis() % 10000 < 100) { // 每10秒最多输出一次
                        System.err.println("接收消息失败: " + e.getMessage());
                    }
                    // 如果是连接断开，退出循环
                    if (e instanceof java.net.SocketException || e instanceof EOFException) {
                        break;
                    }
                }
            }
        }
        System.out.println("接收线程已停止");
    }
    
    @Override
    public void disconnect() {
        // 停止接收线程
        running = false;
        
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
        }
        
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            System.out.println("已断开与服务端的连接");
        } catch (IOException e) {
            System.err.println("断开连接时发生错误: " + e.getMessage());
        }
    }
    
    @Override
    public Message sendMessage(Message message) {
        if (!isConnected()) {
            return Message.failure(message.getAction(), "网络连接未建立");
        }
        
        try {
            // 发送消息
            //这里是建立管道传递到服务器的
            out.writeObject(message);
            out.flush();
            System.out.println("已发送消息: " + message);
            
            // 返回成功消息，响应会由异步接收线程处理
            return Message.success(message.getAction(), "消息已发送");
            
        } catch (Exception e) {
            System.err.println("发送消息失败: " + e.getMessage());
            disconnect();
            return Message.failure(message.getAction(), "发送消息失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getConnectionInfo() {
        if (isConnected()) {
            return String.format("已连接到 %s:%d", host, port);
        } else {
            return "未连接";
        }
    }
    
    /**
     * 获取MessageController实例
     * @return MessageController实例
     */
    public com.vcampus.client.controller.MessageController getMessageController() {
        return messageController;
    }
}
