package com.vcampus.server.service;

import com.vcampus.common.dao.ILibraryDao;
import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.UserBorrowStatus;
import com.vcampus.server.dao.impl.LibraryDao; // 在实际项目中，这里会通过依赖注入传入ILibraryDao的实现

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * 【最终业务逻辑版】图书馆模块的服务层 (LibraryService)
 * 负责处理所有图书馆相关的业务逻辑，所有数据操作均通过 ILibraryDao 接口与数据层解耦。
 */
public class LibraryService {

    // Service层持有一个DAO接口的引用
    private final ILibraryDao libraryDao;

    public LibraryService() {
        this.libraryDao = new LibraryDao();
    }

    // --- 图书管理业务 ---
    // 这些方法已经在使用 DAO，无需修改
    public List<Book> getAllBooks() { return libraryDao.getAllBooks(); }
    public boolean addBook(Book book) { return libraryDao.insertBook(book); }
    public boolean deleteBook(String bookId) {


        return libraryDao.deleteBookById(bookId);



    }
    public boolean modifyBook(Book book) { return libraryDao.updateBook(book); }

    // --- 借阅查询业务 ---
    // 【核心修改】所有方法都改为直接调用 DAO
    public List<BorrowLog> getMyBorrows(String userId) {
        return libraryDao.getBorrowLogsByUserId(userId);
    }
    public List<BorrowLog> getAdminBorrowHistory() {
        return libraryDao.getAllBorrowLogs();
    }
    /**
     * 【新增】处理管理员创建借阅记录的业务逻辑
     * @param bookId 书籍ID
     * @param userId 用户ID
     * @return 操作是否成功
     */
    public boolean createBorrowLog(String bookId, String userId) {
        return libraryDao.adminCreateBorrowLog(bookId, userId);
    }
    // --- 借阅搜索业务 ---
    // 【核心修改】将搜索逻辑完全委托给 DAO，以利用数据库的查询性能
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return libraryDao.getAllBooks();
        }
        return libraryDao.searchBooks(keyword);
    }
    public List<BorrowLog> searchBorrowHistory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return libraryDao.getAllBorrowLogs();
        }
        return libraryDao.searchAllBorrowLogs(keyword);
    }
    public List<BorrowLog> searchMyBorrows( String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return libraryDao.getBorrowLogsByUserId(keyword);
        }
        return libraryDao.searchAllBorrowLogs(keyword);
    }


    // --- 续借业务 ---
    public boolean renewAllBooks(String userId) {
        List<BorrowLog> userLogs = libraryDao.getBorrowLogsByUserId(userId);
        if (userLogs == null || userLogs.isEmpty()) {
            return true; // 没有可续借的书，也算作成功
        }
        // 业务逻辑：将所有书的应还日期延长30天
        for (BorrowLog log : userLogs) {
            LocalDate newBorrowDate = LocalDate.now(); // 借书日期更新为今天
            LocalDate newDueDate = newBorrowDate.plusMonths(1);
            log.setBorrowDate(newBorrowDate.toString());
            log.setDueDate(newDueDate.toString());

            // 逐条更新数据库
            if (!libraryDao.updateBorrowLog(log)) {
                return false; // 如果任何一条更新失败，则整体失败（可以引入事务管理）
            }
        }
        return true;
    }

    /**
     * 【最终业务逻辑版】处理用户借书的核心业务逻辑。
     * @param userId  借书用户的ID
     * @param bookId  要借阅的书籍ID
     * @return 如果借阅成功，返回 true；否则返回 false。
     */
    public boolean borrowBook(String userId, String bookId) {
        // 业务层现在只需调用DAO层那个封装好的事务方法即可
        // 不再关心具体的数据库操作步骤
        if (userId == null || bookId == null) {
            return false;
        }
        return libraryDao.borrowBook(userId, bookId);
    }

    public List<UserBorrowStatus> getAllUsersStatus() {

        return libraryDao.getAllUserBorrowStatus();

    }

    public List<UserBorrowStatus> searchUserStatus(String keyword) {
        return  libraryDao.searchUserBorrowStatus(keyword);
    }
    // 在您的 LibraryService.java 或类似的业务逻辑类中

    public boolean returnBook(String logId, String bookId) {
        // 这里调用DAO层执行数据库操作
        return libraryDao.returnBook(logId, bookId);
    }

    /**
     * 【服务端】根据 bookId 读取 PDF 文件的字节内容。
     * 这是真正的文件读取操作。
     * @param bookId 图书ID
     * @return 文件的字节数组，如果找不到或出错则返回 null
     */
    public byte[] getBookPdfBytes(String bookId) {
        String resourcePath = "/db_pdf/" + bookId + ".pdf";
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.out.println("服务端错误: 找不到资源 " + resourcePath);
                return null; // 文件不存在
            }
            return readAllBytes(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 发生异常
        }
    }
    // 文件读取工具方法
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
    /**
     * 【新增】处理更新借阅记录的业务逻辑
     * @param log 要更新的借阅记录对象
     * @return 操作是否成功
     */
    public boolean updateBorrowLog(BorrowLog log) {
        // 直接调用DAO层的方法
        return libraryDao.updateBorrowLog(log);
    }


}