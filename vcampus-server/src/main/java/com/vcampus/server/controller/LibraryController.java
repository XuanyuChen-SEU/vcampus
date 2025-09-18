package com.vcampus.server.controller;

import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.UserBorrowStatus;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.service.LibraryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * 【参照 ShopController】图书馆模块的控制器 - 服务端
 * 负责接收路由过来的图书馆相关请求，调用 LibraryService 处理业务逻辑，并将结果打包成 Message 返回。
 */
public class LibraryController {

    private final LibraryService libraryService = new LibraryService();

    /**
     * 【新增】总的消息分发器
     * 这是ServerMessageController调用此Controller的入口。
     * 它会根据ActionType，将请求分发给下面具体的handle方法。
     */

    /**
     * 【重大更新】总的消息分发器
     * 现已包含所有图书馆相关的操作路由。
     */
    public Message dispatch(Message message) {
        ActionType action = message.getAction();
        switch (action) {
            case LIBRARY_GET_ALL_BOOKS:
                return handleGetAllBooks(message);
            case LIBRARY_SEARCH_BOOKS:
                return handleSearchBooks(message);
            case LIBRARY_ADD_BOOK:
                return handleAddBook(message);
            case LIBRARY_DELETE_BOOK:
                return handleDeleteBook(message);
            case LIBRARY_MODIFY_BOOK:
                return handleModifyBook(message);
            case LIBRARY_BORROW_BOOK:
                return handleBorrowBook(message);




            // --- 【新增】处理借阅相关的请求 ---
            case LIBRARY_GET_MY_BORROWS:
                return handleGetMyBorrows(message);
            case LIBRARY_GET_ADMIN_BORROW_HISTORY:
                return handleGetAdminBorrowHistory(message);
            case LIBRARY_GET_ALL_USERS_STATUS:
                return handleGetAllUsersStatus(message);
            case LIBRARY_RENEW_ALL:
                return handleRenewAll(message);
            // 【新增】添加对新指令的处理
            case LIBRARY_UPDATE_BORROW_LOG:
                return handleUpdateBorrowLog(message);
            case LIBRARY_CREATE_BORROW_LOG:
                return handleCreateBorrowLog(message);
            // --- 【新增】处理上下文相关的搜索请求 ---
            case LIBRARY_SEARCH_HISTORY:
                return handleSearchBorrowHistory(message);
            case LIBRARY_SEARCH_USERS:
                return handleSearchUserStatus(message);
            case LIBRARY_SEARCH_MY_BORROWS:
                return handleSearchMyBorrows(message);

            case LIBRARY_RETURN_BOOK:
                return handleReturnBook(message);

            case LIBRARY_GET_BOOK_PDF: // 【新增】
                return handleGetBookPdf(message);



            default:
                return Message.failure(action, "不支持的图书馆操作: " + action);
        }
    }



    private Message handleGetAllBooks(Message message) {
        try {
            List<Book> books = libraryService.getAllBooks();
            // 【诊断代码】在这里加一句打印
            System.out.println("服务端准备发送的书籍数量: " + (books != null ? books.size() : "null"));
            return message.success(ActionType.LIBRARY_GET_ALL_BOOKS, books, "获取所有图书列表成功");
        } catch (Exception e) {
            e.printStackTrace();
            return message.failure(ActionType.LIBRARY_GET_ALL_BOOKS, "服务器获取图书失败: " + e.getMessage());
        }
    }

    private Message handleSearchBooks(Message message) {
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_SEARCH_BOOKS, "参数错误，应为关键词(String)");
            }
            String keyword = (String) message.getData();
            List<Book> books = libraryService.searchBooks(keyword);
            return Message.success(ActionType.LIBRARY_SEARCH_BOOKS, books, "搜索成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_SEARCH_BOOKS, "搜索图书失败: " + e.getMessage());
        }
    }

    private Message handleAddBook(Message message) {
        try {
            if (!(message.getData() instanceof Book)) {
                return Message.failure(ActionType.LIBRARY_ADD_BOOK, "参数错误，应为Book对象");
            }
            Book book = (Book) message.getData();
            boolean success = libraryService.addBook(book);
            if (success) {
                return Message.success(ActionType.LIBRARY_ADD_BOOK, null, "添加书籍成功");
            } else {
                return Message.failure(ActionType.LIBRARY_ADD_BOOK, "添加书籍失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_ADD_BOOK, "添加书籍异常: " + e.getMessage());
        }
    }

    private Message handleDeleteBook(Message message) {
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_DELETE_BOOK, "参数错误，应为bookId(String)");
            }
            String bookId = (String) message.getData();
            boolean success = libraryService.deleteBook(bookId);
            if (success) {
                return Message.success(ActionType.LIBRARY_DELETE_BOOK, null, "删除书籍成功");
            } else {
                return Message.failure(ActionType.LIBRARY_DELETE_BOOK, "删除失败，可能书籍不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_DELETE_BOOK, "删除书籍异常: " + e.getMessage());
        }
    }

    private Message handleModifyBook(Message message) {
        try {
            if (!(message.getData() instanceof Book)) {
                return Message.failure(ActionType.LIBRARY_MODIFY_BOOK, "参数错误，应为Book对象");
            }
            Book book = (Book) message.getData();
            boolean success = libraryService.modifyBook(book);
            if (success) {
                return Message.success(ActionType.LIBRARY_MODIFY_BOOK, null, "修改书籍成功");
            } else {
                return Message.failure(ActionType.LIBRARY_MODIFY_BOOK, "修改失败，可能书籍不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_MODIFY_BOOK, "修改书籍异常: " + e.getMessage());
        }
    }
// ==========================================================
    //
    // 【新增】下面是所有新增的请求处理方法
    //
    // ==========================================================

    private Message handleGetMyBorrows(Message message) {
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_GET_MY_BORROWS, "参数错误，应为userId(String)");
            }
            String userId = (String) message.getData();
            List<BorrowLog> logs = libraryService.getMyBorrows(userId);
            return Message.success(ActionType.LIBRARY_GET_MY_BORROWS, logs, "获取我的借阅成功");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_GET_MY_BORROWS, "服务器异常: " + e.getMessage());
        }
    }

    private Message handleGetAdminBorrowHistory(Message message) {
        try {
            List<BorrowLog> logs = libraryService.getAdminBorrowHistory();
            return Message.success(ActionType.LIBRARY_GET_ADMIN_BORROW_HISTORY, logs, "获取借阅历史成功");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_GET_ADMIN_BORROW_HISTORY, "服务器异常: " + e.getMessage());
        }
    }

    private Message handleGetAllUsersStatus(Message message) {
        try {
            List<UserBorrowStatus> statuses = libraryService.getAllUsersStatus();
            return Message.success(ActionType.LIBRARY_GET_ALL_USERS_STATUS, statuses, "获取用户借阅情况成功");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_GET_ALL_USERS_STATUS, "服务器异常: " + e.getMessage());
        }
    }

    private Message handleRenewAll(Message message) {
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_RENEW_ALL, "参数错误，应为userId(String)");
            }
            String userId = (String) message.getData();
            boolean success = libraryService.renewAllBooks(userId);
            if(success) return Message.success(ActionType.LIBRARY_RENEW_ALL, null, "续借成功");
            else return Message.failure(ActionType.LIBRARY_RENEW_ALL, "续借失败");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_RENEW_ALL, "服务器异常: " + e.getMessage());
        }
    }

    private Message handleSearchBorrowHistory(Message message) {
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_SEARCH_HISTORY, "参数错误，应为keyword(String)");
            }
            String keyword = (String) message.getData();
            List<BorrowLog> logs = libraryService.searchBorrowHistory(keyword);
            return Message.success(ActionType.LIBRARY_SEARCH_HISTORY, logs, "搜索借阅历史成功");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_SEARCH_HISTORY, "服务器异常: " + e.getMessage());
        }
    }

    private Message handleSearchUserStatus(Message message) {
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_SEARCH_USERS, "参数错误，应为keyword(String)");
            }
            String keyword = (String) message.getData();
            List<UserBorrowStatus> statuses = libraryService.searchUserStatus(keyword);
            return Message.success(ActionType.LIBRARY_SEARCH_USERS, statuses, "搜索用户情况成功");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_SEARCH_USERS, "服务器异常: " + e.getMessage());
        }
    }

    private Message handleSearchMyBorrows(Message message) {
        try {
            if (!(message.getData() instanceof String[])) {
                return Message.failure(ActionType.LIBRARY_SEARCH_MY_BORROWS, "参数错误，应为String[]{userId, keyword}");
            }
            String[] params = (String[]) message.getData();
            List<BorrowLog> logs = libraryService.searchMyBorrows(params[0]);
            return Message.success(ActionType.LIBRARY_SEARCH_MY_BORROWS, logs, "在我的借阅中搜索成功");
        } catch (Exception e) {
            return Message.failure(ActionType.LIBRARY_SEARCH_MY_BORROWS, "服务器异常: " + e.getMessage());
        }
    }
    // 【新增】处理借书请求的方法
    private Message handleBorrowBook(Message message) {
        try {
            // 客户端发送的是一个包含 [userId, bookId] 的字符串数组
            if (!(message.getData() instanceof String[])) {
                return Message.failure(ActionType.LIBRARY_BORROW_BOOK, "参数错误，应为 String[]{userId, bookId}");
            }
            String[] params = (String[]) message.getData();
            if (params.length < 2) {
                return Message.failure(ActionType.LIBRARY_BORROW_BOOK, "参数不足，需要 userId 和 bookId");
            }

            String userId = params[0];
            String bookId = params[1];

            boolean success = libraryService.borrowBook(userId, bookId);

            if (success) {
                return Message.success(ActionType.LIBRARY_BORROW_BOOK, null, "借书成功");
            } else {
                return Message.failure(ActionType.LIBRARY_BORROW_BOOK, "借书失败，书籍可能已被借出或不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_BORROW_BOOK, "服务器处理借书请求时发生异常: " + e.getMessage());
        }
    }
    /**
     * 【新增】处理获取PDF文件的请求
     */
    public Message getBookPdf(Message requestMsg) {
        // 1. 【解析请求】使用 .getData() 获取负载
        String bookId = (String) requestMsg.getData();
        String resourcePath = "/db_pdf/" + bookId + ".pdf";

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                // 2. 【构建失败响应】使用正确的构造函数或静态方法
                return new Message(ActionType.LIBRARY_GET_BOOK_PDF, false, "服务器上未找到对应的PDF文件。");
                // 或者使用您定义的静态方法，更优雅:
                // return Message.failure(ActionType.LIBRARY_GET_BOOK_PDF, "服务器上未找到对应的PDF文件。");
            }

            byte[] pdfBytes = readAllBytes(inputStream);

            // 3. 【构建成功响应】将 pdfBytes 作为 data 传入
            return new Message(ActionType.LIBRARY_GET_BOOK_PDF, pdfBytes, true, "PDF文件获取成功");
            // 或者使用静态方法:
            // return Message.success(ActionType.LIBRARY_GET_BOOK_PDF, pdfBytes, "PDF文件获取成功");

        } catch (IOException e) {
            e.printStackTrace();
            // 4. 【构建异常响应】
            return new Message(ActionType.LIBRARY_GET_BOOK_PDF, false, "读取服务器文件时发生错误。");
            // 或者使用静态方法:
            // return Message.failure(ActionType.LIBRARY_GET_BOOK_PDF, "读取服务器文件时发生错误。");
        }
    }

    /**
     * 工具方法：将 InputStream 转换为 byte[]
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
    /**
     * 处理用户还书的请求。
     * @param message 包含 logId 和 bookId 的 HashMap
     * @return 操作结果 Message
     */
    private Message handleReturnBook(Message message) {
        try {
            // 1. 参数类型校验
            if (!(message.getData() instanceof HashMap)) {
                return Message.failure(ActionType.LIBRARY_RETURN_BOOK, "参数错误，应为HashMap对象");
            }

            // 2. 提取参数
            HashMap<String, String> params = (HashMap<String, String>) message.getData();
            String logId = params.get("logId");
            String bookId = params.get("bookId");

            // (可选) 进一步校验参数是否存在
            if (logId == null || bookId == null) {
                return Message.failure(ActionType.LIBRARY_RETURN_BOOK, "参数不完整，需要logId和bookId");
            }

            // 3. 调用服务层处理业务逻辑
            boolean success = libraryService.returnBook(logId, bookId);

            // 4. 根据业务结果，返回成功或失败的消息
            if (success) {
                return Message.success(ActionType.LIBRARY_RETURN_BOOK, null, "归还书籍成功");
            } else {
                return Message.failure(ActionType.LIBRARY_RETURN_BOOK, "归还书籍失败，数据库操作未成功");
            }
        } catch (Exception e) {
            // 5. 统一异常处理
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_RETURN_BOOK, "归还书籍异常: " + e.getMessage());
        }
    }


    /**
     * 【服务端】处理获取PDF文件的请求。
     * @param message 包含 bookId
     * @return 包含PDF字节或错误信息的 Message
     */
    public Message handleGetBookPdf(Message message) { // 使用 public
        try {
            if (!(message.getData() instanceof String)) {
                return Message.failure(ActionType.LIBRARY_GET_BOOK_PDF, "参数错误，应为bookId(String)");
            }
            String bookId = (String) message.getData();

            // 调用【服务端】的 LibraryService 来获取文件字节
            byte[] pdfBytes = libraryService.getBookPdfBytes(bookId);

            if (pdfBytes != null) {
                return Message.success(ActionType.LIBRARY_GET_BOOK_PDF, pdfBytes, "PDF文件获取成功");
            } else {
                return Message.failure(ActionType.LIBRARY_GET_BOOK_PDF, "服务器上未找到对应的PDF文件。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Message.failure(ActionType.LIBRARY_GET_BOOK_PDF, "获取PDF文件异常: " + e.getMessage());
        }
    }
    /**
     * 【新增】处理修改借阅记录请求的方法
     * @param request 包含 BorrowLog 对象的请求消息
     * @return 包含操作结果的响应消息
     */
    private Message handleUpdateBorrowLog(Message request) {
        BorrowLog logToUpdate = (BorrowLog) request.getData();
        if (libraryService.updateBorrowLog(logToUpdate)) {
            return Message.success(ActionType.LIBRARY_UPDATE_BORROW_LOG, "借阅记录更新成功。");
        } else {
            return Message.failure(ActionType.LIBRARY_UPDATE_BORROW_LOG,"借阅记录更新失败或数据未变动。");
        }
    }


    /**
     * 【推荐的修正方案】
     * 处理管理员创建借阅记录的请求
     * @param request 包含 [bookId, userId] 数组的请求消息
     * @return 包含操作结果和原始ActionType的响应消息
     */
    private Message handleCreateBorrowLog(Message request) {
        String[] params = (String[]) request.getData();
        if (params == null || params.length < 2) {
            // 使用构造函数创建响应，并传入原始请求的 ActionType
            return new Message(request.getAction(), null, false, "请求参数不足。");
        }
        String bookId = params[0];
        String userId = params[1];

        if (libraryService.createBorrowLog(bookId, userId)) {
            // 成功时，返回一个status为true的消息
            return new Message(request.getAction(), null, true, "借阅记录创建成功！");
        } else {
            // 失败时，返回一个status为false的消息
            return new Message(request.getAction(), null, false, "创建失败，请检查书籍ID和用户ID是否正确，以及书籍是否在馆。");
        }
    }
}