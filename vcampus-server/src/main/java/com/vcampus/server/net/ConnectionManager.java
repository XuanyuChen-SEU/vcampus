package com.vcampus.server.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.vcampus.common.dto.Message;

/**
 * 连接管理器
 * 管理所有客户端连接，支持定向消息发送
 * 编写人：谌宣羽
 */
public class ConnectionManager {
    
    private static final ConnectionManager instance = new ConnectionManager();
    private final Map<String, ClientHandler> connections;
    private final Map<String, String> userIdToConnectionId; // 用户ID到连接ID的映射
    private final AtomicLong connectionIdGenerator;
    
    private ConnectionManager() {
        this.connections = new ConcurrentHashMap<>();
        this.userIdToConnectionId = new ConcurrentHashMap<>();
        this.connectionIdGenerator = new AtomicLong(1);
    }
    
    /**
     * 获取单例实例
     * @return 连接管理器实例
     */
    public static ConnectionManager getInstance() {
        return instance;
    }
    
    /**
     * 注册客户端连接
     * @param clientHandler 客户端处理器
     * @return 连接ID
     */
    public String registerConnection(ClientHandler clientHandler) {
        String connectionId = generateConnectionId();
        connections.put(connectionId, clientHandler);
        return connectionId;
    }
    
    /**
     * 注销客户端连接
     * @param connectionId 连接ID
     */
    public void unregisterConnection(String connectionId) {
        ClientHandler handler = connections.remove(connectionId);
        if (handler != null && handler.getUserId() != null) {
            userIdToConnectionId.remove(handler.getUserId());
        }
    }
    
    /**
     * 绑定用户ID到连接
     * @param userId 用户ID
     * @param connectionId 连接ID
     */
    public void bindUserIdToConnection(String userId, String connectionId) {
        if (connections.containsKey(connectionId)) {
            userIdToConnectionId.put(userId, connectionId);
            connections.get(connectionId).setUserId(userId);
        }
    }
    
    /**
     * 向指定客户端发送消息
     * @param connectionId 连接ID
     * @param message 要发送的消息
     * @return 发送是否成功
     */
    public boolean sendToClient(String connectionId, Message message) {
        ClientHandler clientHandler = connections.get(connectionId);
        if (clientHandler != null && clientHandler.isRunning()) {
            return clientHandler.sendMessage(message);
        }
        return false;
    }
    
    /**
     * 向所有客户端广播消息
     * @param message 要广播的消息
     * @return 成功发送的客户端数量
     */
    public int broadcastToAll(Message message) {
        int successCount = 0;
        for (ClientHandler clientHandler : connections.values()) {
            if (clientHandler.isRunning() && clientHandler.sendMessage(message)) {
                successCount++;
            }
        }
        return successCount;
    }
    
    /**
     * 向指定用户ID的客户端发送消息
     * @param userId 用户ID
     * @param message 要发送的消息
     * @return 发送是否成功
     */
    public boolean sendToUser(String userId, Message message) {
        String connectionId = userIdToConnectionId.get(userId);
        if (connectionId != null) {
            return sendToClient(connectionId, message);
        }
        return false;
    }
    
    /**
     * 获取连接处理器
     * @param connectionId 连接ID
     * @return 客户端处理器，如果不存在返回null
     */
    public ClientHandler getConnection(String connectionId) {
        return connections.get(connectionId);
    }
    
    /**
     * 根据用户ID获取连接ID
     * @param userId 用户ID
     * @return 连接ID，如果不存在返回null
     */
    public String getConnectionIdByUserId(String userId) {
        return userIdToConnectionId.get(userId);
    }
    
    /**
     * 获取当前连接数
     * @return 连接数量
     */
    public int getConnectionCount() {
        return connections.size();
    }
    
    /**
     * 检查连接是否存在
     * @param connectionId 连接ID
     * @return 如果存在返回true，否则返回false
     */
    public boolean hasConnection(String connectionId) {
        return connections.containsKey(connectionId);
    }
    
    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 如果在线返回true，否则返回false
     */
    public boolean isUserOnline(String userId) {
        return userIdToConnectionId.containsKey(userId);
    }
    
    /**
     * 获取所有连接ID
     * @return 连接ID集合
     */
    public java.util.Set<String> getAllConnectionIds() {
        return connections.keySet();
    }
    
    /**
     * 获取所有在线用户ID
     * @return 用户ID集合
     */
    public java.util.Set<String> getAllOnlineUserIds() {
        return userIdToConnectionId.keySet();
    }
    
    /**
     * 生成连接ID
     * @return 唯一的连接ID
     */
    private String generateConnectionId() {
        return "conn_" + connectionIdGenerator.getAndIncrement();
    }
    
    /**
     * 清理所有连接
     */
    public void clearAllConnections() {
        for (ClientHandler clientHandler : connections.values()) {
            clientHandler.stop();
        }
        connections.clear();
        userIdToConnectionId.clear();
    }
}
