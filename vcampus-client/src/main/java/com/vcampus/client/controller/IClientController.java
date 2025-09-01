package com.vcampus.client.controller;

/**
 * 客户端控制器接口
 * 所有客户端控制器都应该实现此接口
 * 编写人：谌宣羽
 */
public interface IClientController {
    
    /**
     * 注册到MessageController
     * 每个客户端控制器都需要实现此方法，将自己注册到MessageController中
     */
    void registerToMessageController();
}
