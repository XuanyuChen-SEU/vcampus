package com.vcampus.server.net;

import com.vcampus.common.dto.Message;

/**
 * 服务端消息处理接口
 * 定义服务端处理客户端消息的方法
 * 编写人：谌宣羽
 */
public interface IMessageServerSrv {
    
    /**
     * 处理客户端消息
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    Message handleMessage(Message message);
    
    /**
     * 启动服务
     */
    void start();
    
    /**
     * 停止服务
     */
    void stop();
    
    /**
     * 检查服务是否正在运行
     * @return 如果服务正在运行返回true，否则返回false
     */
    boolean isRunning();
}
