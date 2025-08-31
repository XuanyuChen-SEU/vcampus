package com.vcampus.client.net;

import com.vcampus.common.dto.Message;

/**
 * 客户端消息处理接口
 * 定义客户端处理服务端消息的方法
 * 编写人：谌宣羽
 */
public interface IMessageClientSrv {
    
    /**
     * 发送消息给服务端
     * @param message 要发送的消息
     * @return 服务端响应消息
     */
    Message sendMessage(Message message);
    
    /**
     * 连接服务端
     * @return 连接是否成功
     */
    boolean connect();
    
    /**
     * 断开连接
     */
    void disconnect();
    
    /**
     * 检查是否已连接
     * @return 如果已连接返回true，否则返回false
     */
    boolean isConnected();
}
