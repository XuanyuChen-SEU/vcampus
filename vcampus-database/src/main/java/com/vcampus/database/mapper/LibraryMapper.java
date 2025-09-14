package com.vcampus.database.mapper;

import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.UserBorrowStatus;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface LibraryMapper {




    // --- CSV 数据加载 ---
    void loadBooksFromCsv(String filePath);
    void loadBorrowLogsFromCsv(String filePath);

    // --- Book (图书) 操作 ---
    List<Book> selectAllBooks();
    Book selectBookById(String bookId);
    List<Book> searchBooks(String keyword);
    int insertBook(Book book);
    int updateBook(Book book);
    int deleteBookById(String bookId);

    // --- BorrowLog (借阅记录) 操作 ---
    List<BorrowLog> selectAllBorrowLogs();
    List<BorrowLog> selectBorrowLogsByUserId(String userId);
    int insertBorrowLog(BorrowLog borrowLog);
    int updateBorrowLog(BorrowLog borrowLog);
    List<BorrowLog> searchAllBorrowLogs(String keyword);
    List<BorrowLog> searchMyBorrows(@Param("userId") String userId, @Param("keyword") String keyword);




    List<UserBorrowStatus> selectAllUserBorrowStatus();
    List<UserBorrowStatus> searchUserBorrowStatus(String keyword);
    // --- 辅助查询 ---
    String findUsernameByUserId(String userId);
}