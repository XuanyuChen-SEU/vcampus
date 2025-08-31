package com.vcampus.server.controller;

import com.vcampus.common.dto.Message;

/**
 * 控制器接口
 * 定义所有控制器的通用方法
 * 编写人：谌宣羽
 */
public interface IController {
    
    /**
     * 处理客户端请求
     * @param message 客户端发送的消息
     * @return 处理结果消息
     */
    Message handleRequest(Message message);
    
    /**
     * 获取控制器支持的操作类型
     * @return 支持的操作类型数组
     */
    String[] getSupportedActions();
}
