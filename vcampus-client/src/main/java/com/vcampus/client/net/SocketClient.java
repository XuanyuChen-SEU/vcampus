package com.vcampus.client.net;

import com.vcampus.common.dto.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 客户端Socket连接实现类
 * 实现IMessageClientSrv接口，提供双线程网络通信功能
 * 编写人：谌宣羽
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
    
    // 双线程相关
    private Thread sendThread;      // 发送线程
    private Thread receiveThread;   // 接收线程
    private AtomicBoolean running;  // 线程运行状态
    private BlockingQueue<Message> sendQueue;  // 发送消息队列
    private MessageListener messageListener;   // 消息监听器
    
    /**
     * 消息监听器接口
     */
    public interface MessageListener {
        void onMessageReceived(Message message);
        void onConnectionLost();
    }
    
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
        this.running = new AtomicBoolean(false);
        this.sendQueue = new LinkedBlockingQueue<>();
    }
    
    /**
     * 设置消息监听器
     * @param listener 消息监听器
     */
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
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
            
            // 启动双线程
            startThreads();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("连接服务端失败: " + e.getMessage());
            disconnect();
            return false;
        }
    }
    
    /**
     * 启动发送和接收线程
     */
    private void startThreads() {
        running.set(true);
        
        // 启动发送线程
        sendThread = new Thread(this::sendLoop, "SendThread");
        sendThread.setDaemon(true);
        sendThread.start();
        
        // 启动接收线程
        receiveThread = new Thread(this::receiveLoop, "ReceiveThread");
        receiveThread.setDaemon(true);
        receiveThread.start();
        
        System.out.println("双线程已启动");
    }
    
    /**
     * 发送线程循环
     */
    private void sendLoop() {
        while (running.get() && isConnected()) {
            try {
                // 从队列中获取消息，如果没有消息就一直等待
                Message message = sendQueue.take();
                if (message != null) {
                    // 发送消息
                    out.writeObject(message);
                    out.flush();
                    System.out.println("已发送消息: " + message);
                }
            } catch (InterruptedException e) {
                // 线程被中断，退出循环
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("发送消息失败: " + e.getMessage());
                handleConnectionLost();
                break;
            }
        }
    }
    
    /**
     * 接收线程循环
     */
    private void receiveLoop() {
        while (running.get() && isConnected()) {
            try {
                // 接收消息
                Object obj = in.readObject();
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    System.out.println("已接收消息: " + message);
                    
                    // 使用全局消息分发器分发消息
                    MessageDispatcher.getInstance().dispatchMessage(message);
                    
                    // 同时通知监听器（保持向后兼容）
                    if (messageListener != null) {
                        messageListener.onMessageReceived(message);
                    }
                } else {
                    System.err.println("接收到无效的消息类型: " + obj.getClass());
                }
            } catch (Exception e) {
                System.err.println("接收消息失败: " + e.getMessage());
                handleConnectionLost();
                break;
            }
        }
    }
    
    /**
     * 处理连接丢失
     */
    private void handleConnectionLost() {
        running.set(false);
        if (messageListener != null) {
            messageListener.onConnectionLost();
        }
        disconnect();
    }
    
    @Override
    public void disconnect() {
        // 停止线程
        running.set(false);
        
        // 等待线程结束
        if (sendThread != null && sendThread.isAlive()) {
            sendThread.interrupt();
        }
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
        
        // 将消息加入发送队列
        try {
            sendQueue.put(message);
            return Message.success(message.getAction(), "消息已加入发送队列");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Message.failure(message.getAction(), "发送被中断");
        }
    }
    
    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && running.get();
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getConnectionInfo() {
        if (isConnected()) {
            return String.format("已连接到 %s:%d (发送队列: %d)", host, port, sendQueue.size());
        } else {
            return "未连接";
        }
    }
}
