package com.vcampus.client.service;

import com.vcampus.client.MainApp;
import com.vcampus.client.net.SocketClient;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.dto.Book;

import java.util.HashMap;

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
     * 【为 handleSearch() 服务】
     * 根据用户输入的关键词搜索商品。
     * @param keyword 搜索关键词 (String)。
     * @return 包含搜索结果商品列表的响应 Message 对象。
     */
    public Message searchBooks(String keyword) {
        // 将关键词作为数据发送给服务器
        Message request = new Message(ActionType.LIBRARY_SEARCH_BOOKS, keyword);
        return socketClient.sendMessage(request);
    }
    /**
     * 【新增】请求归还一本书
     * @param logId  借阅记录的ID
     * @param bookId 图书的ID
     * @return 服务器的响应消息
     */
    public Message returnBook(String logId, String bookId) {
        try {
            // 将两个ID打包到一个HashMap中发送
            HashMap<String, String> params = new HashMap<>();
            params.put("logId", logId);
            params.put("bookId", bookId);

            Message request = new Message(ActionType.LIBRARY_RETURN_BOOK, params);
            return socketClient.sendMessage(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(ActionType.LIBRARY_RETURN_BOOK, null, false, "客户端请求异常");
        }
    }
    /**
     * 【新增】【为 handleBorrowBook() 服务】
     * 发送用户借阅书籍的请求。
     * @param userId  借阅用户的ID
     * @param bookId  要借阅的书籍ID
     */
    public Message borrowBook(String userId, String bookId) {
        // 可以将 userId 和 bookId 打包成一个数组或专用的DTO对象
        String[] params = {userId, bookId};
        // 这里需要一个您在通用模块中新定义的 ActionType，例如 LIBRARY_BORROW_BOOK
        Message request = new Message(ActionType.LIBRARY_BORROW_BOOK, params);
        return socketClient.sendMessage(request);
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
    /**
     * 【为 initialize() 服务】
     * 获取初始显示的书籍列表。
     * @return 包含书籍列表的响应 Message 对象。
     */
    public Message getAllBooks() {
        // 创建一个不带数据的请求消息，因为服务器知道这个指令是获取所有书籍
        Message request = new Message(ActionType.LIBRARY_GET_ALL_BOOKS, null);

        // 发送请求并同步等待服务器的响应
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 showUserMyBorrowsView() 服务】
     * 获取指定用户的借阅记录。
     * @param userId 用户ID
     */
    public Message getMyBorrows(String userId) {
        Message request = new Message(ActionType.LIBRARY_GET_MY_BORROWS, userId);
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 showAdminBorrowHistoryView() 服务】
     * (管理员) 获取所有用户的借阅记录。
     */
    public Message getAdminBorrowHistory() {
        Message request = new Message(ActionType.LIBRARY_GET_ADMIN_BORROW_HISTORY, null);
        return socketClient.sendMessage(request);
    }


    /**
     * 【为 showAllUsersStatusView() 服务】
     * (管理员) 获取所有用户的借阅状态统计。
     */
    public Message getAllUsersStatus() {
        Message request = new Message(ActionType.LIBRARY_GET_ALL_USERS_STATUS, null);
        return socketClient.sendMessage(request);
    }


    /**
     * 【为 handleAddBook() 服务】
     * 发送添加新书的请求。
     * @param book 包含新书信息的 Book 对象
     */
    public Message addBook(Book book) {
        Message request = new Message(ActionType.LIBRARY_ADD_BOOK, book);
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 handleDeleteBook() 服务】
     * 发送删除书籍的请求。
     * @param bookId 要删除的书籍ID
     */
    public Message deleteBook(String bookId) {
        Message request = new Message(ActionType.LIBRARY_DELETE_BOOK, bookId);
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 handleModifyBook() 服务】
     * 发送修改书籍信息的请求。
     * @param book 包含更新后书籍信息的 Book 对象
     */
    public Message modifyBook(Book book) {
        Message request = new Message(ActionType.LIBRARY_MODIFY_BOOK, book);
        return socketClient.sendMessage(request);
    }

    /**
     * 【为 handleRenewAll() 服务】
     * 发送一键续借的请求。
     * @param userId 需要续借的用户的ID
     */
    public Message renewAllBooks(String userId) {
        Message request = new Message(ActionType.LIBRARY_RENEW_ALL, userId);
        return socketClient.sendMessage(request);
    }
    /**
     * 【新增】(管理员) 搜索借阅历史。
     * @param keyword 搜索关键词 (例如: 书名, 用户名)
     */
    public Message searchBorrowHistory(String keyword) {
        // 注意：这里需要一个新的 ActionType
        Message request = new Message(ActionType.LIBRARY_SEARCH_HISTORY, keyword);
        return socketClient.sendMessage(request);
    }

    /**
     * 【新增】(管理员) 搜索用户借阅情况。
     * @param keyword 搜索关键词 (例如: 用户ID, 姓名)
     */
    public Message searchUserStatus(String keyword) {
        // 注意：这里需要一个新的 ActionType
        Message request = new Message(ActionType.LIBRARY_SEARCH_USERS, keyword);
        return socketClient.sendMessage(request);
    }

    /**
     * 【新增】(用户) 在自己的借阅记录中进行搜索。
     * @param userId  当前用户的ID
     * @param keyword 搜索关键词 (例如: 书名)
     */
    public Message searchMyBorrows(String userId, String keyword) {
        // 将用户ID和关键词打包发送
        // 您也可以定义一个DTO来包装这两个参数
        String[] params = {userId, keyword};
        Message request = new Message(ActionType.LIBRARY_SEARCH_MY_BORROWS, params);
        return socketClient.sendMessage(request);
    }


    /**
     * 【异步模式】向服务器请求获取图书PDF文件。
     * 这个方法只负责发送请求，不等待响应。
     * @param bookId 需要获取PDF的图书ID
     */
    public void getBookPdf(String bookId) {
        // 1. 创建请求 Message
        Message request = new Message(ActionType.LIBRARY_GET_BOOK_PDF, bookId);

        // 2. 发送请求，然后方法立即结束
        socketClient.sendMessage(request);
    }


    /**
     * 【修正后】发送更新借阅记录的请求
     * @param borrowLog 包含更新后信息的借阅记录对象 (主要是 dueDate)
     * @return 服务器的响应
     */
    public Message updateBorrowLog(BorrowLog borrowLog) {
        // 【修正】使用正确的 ActionType
        Message request = new Message(ActionType.LIBRARY_UPDATE_BORROW_LOG, borrowLog);
        return socketClient.sendMessage(request);
    }
    /**
     * 【新增】(管理员) 创建一条新的借阅记录
     * @param bookId 要借阅的书籍ID
     * @param userId 借阅用户的ID
     * @return 服务器的响应
     */
    public Message createBorrowLog(String bookId, String userId) {
        // 将两个ID打包成数组发送
        String[] params = {bookId, userId};
        // 注意：这里需要一个新的 ActionType
        Message request = new Message(ActionType.LIBRARY_CREATE_BORROW_LOG, params);
        return socketClient.sendMessage(request);
    }
}
