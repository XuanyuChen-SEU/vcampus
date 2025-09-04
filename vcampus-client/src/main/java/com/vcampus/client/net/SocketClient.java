package com.vcampus.client.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final com.vcampus.client.controller.MessageController messageController;

    // 异步接收相关
    private Thread receiveThread;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean connected = new AtomicBoolean(false);

    // 锁对象
    private final Object connectionLock = new Object();
    private final Object sendLock = new Object();

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
    }

    @Override
    public boolean connect() {
        synchronized (connectionLock) {
            if (connected.get()) {
                return true; // 已连接
            }
            try {
                // 创建Socket连接
                socket = new Socket();
                socket.connect(new java.net.InetSocketAddress(host, port), CONNECTION_TIMEOUT);
                socket.setSoTimeout(READ_TIMEOUT);

                // 创建输入输出流
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                System.out.println("成功连接到服务端: " + host + ":" + port);

                // 标记已连接
                connected.set(true);
                running.set(true);

                // 启动异步接收线程
                startReceiveThread();

                return true;

            } catch (Exception e) {
                System.err.println("连接服务端失败: " + e.getMessage());
                disconnect();
                return false;
            }
        }
    }

    /**
     * 启动异步接收线程
     */
    private void startReceiveThread() {
        receiveThread = new Thread(this::receiveLoop, "ReceiveThread");
        receiveThread.setDaemon(true);
        receiveThread.start();
        System.out.println("异步接收线程已启动");
    }

    /**
     * 接收循环
     */
    private void receiveLoop() {
        try {
            while (running.get() && isConnected()) {
                Object obj = in.readObject();
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    System.out.println("接收到消息: " + message);
                    messageController.handleMessage(message);
                } else {
                    System.err.println("接收到无效的消息类型: " + obj.getClass());
                }
            }
        } catch (SocketException | EOFException e) {
            System.err.println("连接已关闭: " + e.getMessage());
        } catch (Exception e) {
            if (running.get()) {
                System.err.println("接收消息失败: " + e.getMessage());
            }
        } finally {
            running.set(false);
            connected.set(false);
            System.out.println("接收线程已停止");
        }
    }

    @Override
    public void disconnect() {
        synchronized (connectionLock) {
            if (!connected.get()) return;

            running.set(false);
            connected.set(false);

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.err.println("关闭输入流时出错: " + e.getMessage());
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                System.err.println("关闭输出流时出错: " + e.getMessage());
            }

            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("关闭socket时出错: " + e.getMessage());
            }

            socket = null;
            out = null;
            in = null;

            System.out.println("已断开与服务端的连接");
        }
    }

    @Override
    public Message sendMessage(Message message) {
        if (!isConnected()) {
            return Message.failure(message.getAction(), "网络连接未建立");
        }

        try {
            synchronized (sendLock) {
                out.writeObject(message);
                out.flush();
            }
            System.out.println("已发送消息: " + message);
            return Message.success(message.getAction(), "消息已发送");
        } catch (Exception e) {
            System.err.println("发送消息失败: " + e.getMessage());
            disconnect();
            return Message.failure(message.getAction(), "发送消息失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return connected.get()
                && socket != null
                && socket.isConnected()
                && !socket.isClosed();
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
