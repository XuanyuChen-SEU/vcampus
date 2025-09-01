package com.vcampus.client.net;

import com.vcampus.common.dto.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * 简化的Socket客户端
 * 采用类似服务端的同步处理模式，简化网络连接逻辑
 * 编写人：cursor
 */
public class SimpleSocketClient {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9090;
    private static final int CONNECTION_TIMEOUT = 5000; // 5秒连接超时
    private static final int READ_TIMEOUT = 10000; // 10秒读取超时
    
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    /**
     * 使用默认配置创建Socket客户端
     */
    public SimpleSocketClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * 使用指定配置创建Socket客户端
     * @param host 服务端地址
     * @param port 服务端端口
     */
    public SimpleSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * 连接到服务端
     * @return 连接是否成功
     */
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
            return true;
            
        } catch (Exception e) {
            System.err.println("连接服务端失败: " + e.getMessage());
            disconnect();
            return false;
        }
    }
    
    /**
     * 发送消息并等待响应（同步模式）
     * @param message 要发送的消息
     * @return 服务端响应消息
     */
    public Message sendMessage(Message message) {
        if (!isConnected()) {
            return Message.failure(message.getAction(), "网络连接未建立");
        }
        
        try {
            // 发送消息
            out.writeObject(message);
            out.flush();
            System.out.println("已发送消息: " + message);
            
            // 等待响应
            Object obj = in.readObject();
            if (obj instanceof Message) {
                Message response = (Message) obj;
                System.out.println("已接收响应: " + response);
                return response;
            } else {
                System.err.println("接收到无效的响应类型: " + obj.getClass());
                return Message.failure(message.getAction(), "接收到无效的响应类型");
            }
            
        } catch (Exception e) {
            System.err.println("发送消息失败: " + e.getMessage());
            disconnect();
            return Message.failure(message.getAction(), "发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 断开与服务端的连接
     */
    public void disconnect() {
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
    
    /**
     * 检查连接状态
     * @return 是否已连接
     */
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
}
