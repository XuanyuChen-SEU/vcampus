package com.vcampus.server.controller;

import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.LibraryService;


/**
 * 服务端 图书控制器
 * 负责处理来自客户端的学生相关请求
 * 编写人：崔镇宇
 */
public class LibraryController {
    private final LibraryService libraryService;
    public LibraryController() {this.libraryService = new LibraryService();}


    /*
     * 处理客户端发来的消息
     * */
    public Message handle(Message request)
    {
        ActionType action = request.getAction();
        Object data=     request.getData();
        switch (action)
        {
            case INFO_BOOK:
                if (!(data instanceof String)) {
                    return Message.failure(ActionType.INFO_BOOK, "参数错误，应为 bookId(String)");
                }
                String bookId = (String) data;
                Book book = libraryService.getBookById(bookId);
                if (book != null) {
                    return Message.success(ActionType.INFO_BOOK, book, "查询成功");
                }
                else{
                    return Message.failure(ActionType.INFO_STUDENT, "未找到该图书信息");
                }

        }
        defalut:
        return Message.failure(action, "不支持的学生或老师操作: " + action);
    }






}
