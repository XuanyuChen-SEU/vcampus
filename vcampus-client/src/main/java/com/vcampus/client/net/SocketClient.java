package com.vcampus.client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vcampus.common.dto.Message;

/**
 * 客户端Socket连接类
 * 负责与服务端建立连接并进行消息传输
 * 编写人：谌宣羽
 */
public class SocketClient implements IMessageClientSrv {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9090;
    private static final int DEFAULT_TIMEOUT = 5000;
    
    private final String host;
    private final int port;
    private final int timeout;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final AtomicBoolean isConnected;
    
    public SocketClient() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }
    
    public SocketClient(String host, int port) {
        this(host, port, DEFAULT_TIMEOUT);
    }
    
    public SocketClient(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.isConnected = new AtomicBoolean(false);
    }
    
    @Override
    public boolean connect() {
        if (isConnected.get()) {
            return true;
        }
        
        try {
            // 创建Socket连接
            socket = new Socket(host, port);
            socket.setSoTimeout(timeout);
            
            // 创建输入输出流
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            
            // 设置连接状态
            isConnected.set(true);
            
            // 读取连接成功消息
            Message welcomeMessage = (Message) inputStream.readObject();
            
            return true;
            
        } catch (IOException e) {
            cleanup();
            return false;
        } catch (ClassNotFoundException e) {
            cleanup();
            return false;
        }
    }
    
    @Override
    public Message sendMessage(Message message) {
        if (!isConnected.get()) {
            return Message.failure(message.getAction(), "未连接到服务端");
        }
        
        try {
            // 发送消息
            outputStream.writeObject(message);
            outputStream.flush();
            
            // 接收响应
            Message response = (Message) inputStream.readObject();
            
            return response;
            
        } catch (IOException e) {
            isConnected.set(false);
            return Message.failure(message.getAction(), "网络连接异常");
        } catch (ClassNotFoundException e) {
            return Message.failure(message.getAction(), "响应格式错误");
        }
    }
    
    @Override
    public void disconnect() {
        if (!isConnected.get()) {
            return;
        }
        
        isConnected.set(false);
        cleanup();
    }
    
    @Override
    public boolean isConnected() {
        return isConnected.get() && socket != null && !socket.isClosed();
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
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // 清理资源异常
        } finally {
            inputStream = null;
            outputStream = null;
            socket = null;
        }
    }
    
    /**
     * 获取连接的主机地址
     * @return 主机地址
     */
    public String getHost() {
        return host;
    }
    
    /**
     * 获取连接端口
     * @return 端口号
     */
    public int getPort() {
        return port;
    }
    
    /**
     * 获取连接超时时间
     * @return 超时时间（毫秒）
     */
    public int getTimeout() {
        return timeout;
    }
}
