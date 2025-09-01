package com.vcampus.client.net;

import com.vcampus.common.dto.Message;

/**
 * 客户端消息处理接口
 * 定义客户端网络通信的基本方法
 * 编写人：谌宣羽
 */
public interface IMessageClientSrv {
    
    /**
     * 连接到服务端
     * @return 连接是否成功
     */
    boolean connect();
    
    /**
     * 断开与服务端的连接
     */
    void disconnect();
    
    /**
     * 发送消息到服务端
     * @param message 要发送的消息
     * @return 服务端响应消息
     */
    Message sendMessage(Message message);
    
    /**
     * 检查连接状态
     * @return 是否已连接
     */
    boolean isConnected();
    
}
