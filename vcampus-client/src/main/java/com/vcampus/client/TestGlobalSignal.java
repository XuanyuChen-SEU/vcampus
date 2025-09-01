package com.vcampus.client;

import com.vcampus.client.service.LoginService;
import com.vcampus.common.dto.Message;

/**
 * 测试全局信号接收功能
 * 验证客户端能否持续监听服务器的全局通知
 */
public class TestGlobalSignal {
    
    public static void main(String[] args) {
        System.out.println("=== 测试全局信号接收 ===");
        
        // 创建登录服务
        LoginService loginService = new LoginService();
        
        // 连接到服务端
        System.out.println("正在连接服务端...");
        boolean connected = loginService.getGlobalSocketClient().connect();
        
        if (!connected) {
            System.err.println("连接服务端失败，退出测试");
            return;
        }
        
        System.out.println("连接成功！");
        System.out.println("连接信息: " + loginService.getGlobalSocketClient().getConnectionInfo());
        System.out.println("异步接收线程已启动，正在监听全局信号...");
        
        // 测试登录
        System.out.println("\n=== 测试登录 ===");
        Message result = loginService.login("admin", "123456");
        System.out.println("登录结果: " + result.isSuccess() + " - " + result.getMessage());
        
        // 保持连接，等待全局信号
        System.out.println("\n=== 等待全局信号 ===");
        System.out.println("客户端正在监听服务器的全局通知...");
        System.out.println("您可以：");
        System.out.println("1. 在服务端发送全局通知");
        System.out.println("2. 在服务端发送系统广播");
        System.out.println("3. 在服务端发送紧急通知");
        System.out.println("4. 按Ctrl+C退出测试");
        
        // 保持程序运行
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println("测试被中断");
        }
        
        // 断开连接
        System.out.println("\n=== 断开连接 ===");
        loginService.getGlobalSocketClient().disconnect();
        System.out.println("测试完成！");
    }
}
