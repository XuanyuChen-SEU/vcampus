package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

public class LibraryService {

    private final SocketClient socketClient;

    public LibraryService(){this.socketClient = MainApp.getGlobalSocketClient();}

    /**
     * 获取全局的 Socket 客户端，用于注册 Controller
     */
    public SocketClient getGlobalSocketClient() {
        return socketClient;
    }

    /**
     * 通过书号获取书籍信息
     * @param bookId 当前需要查找的书籍的ID / 书籍ID
     */
    public void getBookById(String bookId) {
        // 构造请求消息
        Message request = new Message(ActionType.INFO_BOOK, bookId);

        // 发送到服务端，服务端会返回学生信息
        socketClient.sendMessage(request);
    }



}
