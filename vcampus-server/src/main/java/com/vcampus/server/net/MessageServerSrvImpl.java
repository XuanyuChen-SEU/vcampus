package com.vcampus.server.net;

import java.util.concurrent.atomic.AtomicBoolean;

import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.controller.ControllerManager;

/**
 * 消息服务实现类
 * 负责将客户端消息分发到对应的控制器处理
 * 编写人：谌宣羽
 */
public class MessageServerSrvImpl implements IMessageServerSrv {
    
    private final AtomicBoolean isRunning;
    private final ControllerManager controllerManager;
    
    public MessageServerSrvImpl() {
        this.isRunning = new AtomicBoolean(false);
        this.controllerManager = new ControllerManager();
    }
    
    @Override
    public Message handleMessage(Message message) {
        if (!isRunning.get()) {
            return Message.failure(ActionType.LOGIN, "服务器未启动");
        }
        
        if (message == null) {
            return Message.failure(ActionType.LOGIN, "消息为空");
        }
        
        ActionType action = message.getAction();
        if (action == null) {
            return Message.failure(ActionType.LOGIN, "操作类型为空");
        }
        
        try {
            // 委托给控制器管理器处理
            return controllerManager.handleRequest(message);
            
        } catch (Exception e) {
            return Message.failure(action, "服务器内部错误");
        }
    }
    
    @Override
    public void start() {
        if (isRunning.get()) {
            return;
        }
        
        isRunning.set(true);
    }
    
    @Override
    public void stop() {
        if (!isRunning.get()) {
            return;
        }
        
        isRunning.set(false);
    }
    
    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
}
