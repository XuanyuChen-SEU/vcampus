package com.vcampus.common.dao;

import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.UserBorrowStatus;

import java.util.List;

/**
 * 【最终版】图书馆数据访问对象接口
 * 定义了所有与图书馆相关的数据库操作，包括图书管理、借阅记录管理和复合查询。
 */
public interface ILibraryDao {

    // ==========================================================
    // --- (1) 图书管理 (Book Operations) ---
    // 您已有的部分，非常完善，基本无需改动
    // ==========================================================

    List<Book> getAllBooks(); // 获取所有图书
    Book getBookById(String bookId); // 根据ID获取图书
    List<Book> searchBooks(String keyword);
    boolean insertBook(Book book); // 插入一本新书
    boolean updateBook(Book book); // 更新图书信息
    boolean deleteBookById(String bookId); // 根据ID删除图书


    List<BorrowLog> getAllBorrowLogs();
    List<BorrowLog> getBorrowLogsByUserId(String userId);
    List<BorrowLog> searchAllBorrowLogs(String keyword);
    boolean insertBorrowLog(BorrowLog log);
    boolean updateBorrowLog(BorrowLog log);
    boolean deleteBorrowLogById(String logId);
    // 【新增】定义管理员创建借阅记录的接口方法
    boolean adminCreateBorrowLog(String bookId, String userId);



    List<UserBorrowStatus> getAllUserBorrowStatus();
    List<UserBorrowStatus> searchUserBorrowStatus(String keyword);

    String findUsernameByUserId(String userId);
    boolean borrowBook(String userId, String bookId);

    boolean returnBook(String logId, String bookId);

}