package com.vcampus.client.net;

import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import javafx.application.Platform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 全局消息分发器
 * 负责将接收到的消息分发给对应的处理器
 * 编写人：谌宣羽
 */
public class MessageDispatcher {
    
    private static MessageDispatcher instance;
    private final Map<ActionType, MessageHandler> handlers = new ConcurrentHashMap<>();
    
    /**
     * 消息处理器接口
     */
    public interface MessageHandler {
        void handleMessage(Message message);
    }
    
    /**
     * 单例模式获取实例
     */
    public static MessageDispatcher getInstance() {
        if (instance == null) {
            instance = new MessageDispatcher();
        }
        return instance;
    }
    
    /**
     * 注册消息处理器
     * @param actionType 消息类型
     * @param handler 处理器
     */
    public void registerHandler(ActionType actionType, MessageHandler handler) {
        handlers.put(actionType, handler);
        System.out.println("注册消息处理器: " + actionType);
    }
    
    /**
     * 注销消息处理器
     * @param actionType 消息类型
     */
    public void unregisterHandler(ActionType actionType) {
        handlers.remove(actionType);
        System.out.println("注销消息处理器: " + actionType);
    }
    
    /**
     * 分发消息
     * @param message 接收到的消息
     */
    public void dispatchMessage(Message message) {
        ActionType actionType = message.getAction();
        MessageHandler handler = handlers.get(actionType);
        
        if (handler != null) {
            // 在JavaFX应用线程中处理消息
            Platform.runLater(() -> {
                try {
                    handler.handleMessage(message);
                } catch (Exception e) {
                    System.err.println("处理消息时发生错误: " + e.getMessage());
                }
            });
        } else {
            System.out.println("未找到消息处理器: " + actionType);
        }
    }
    
    /**
     * 获取已注册的处理器数量
     * @return 处理器数量
     */
    public int getHandlerCount() {
        return handlers.size();
    }
    
    /**
     * 清空所有处理器
     */
    public void clearHandlers() {
        handlers.clear();
        System.out.println("清空所有消息处理器");
    }
}
