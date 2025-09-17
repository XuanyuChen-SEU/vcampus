package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ILibraryDao;
import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.UserBorrowStatus;
import com.vcampus.database.mapper.LibraryMapper;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class LibraryDao implements ILibraryDao {

    @Override
    public List<Book> getAllBooks() {

        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.selectAllBooks();
        }
    }

    @Override
    public Book getBookById(String bookId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.selectBookById(bookId);
        }

    }

    // ... 为 ILibraryDao 中所有其他方法提供空的实现 ...
    // 例如:
    @Override
    public boolean insertBook(Book book) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            libraryMapper.insertBook(book);
            sqlSession.commit();
        }
        return true;
    }

    @Override
    public boolean updateBook(Book book) {
        int count=0;
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            count=libraryMapper.updateBook(book);
            sqlSession.commit();
        }
        return count>0;
    }

    @Override
    public boolean deleteBookById(String bookId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            libraryMapper.deleteBookById(bookId);
            sqlSession.commit();
        }
        return true;
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.searchBooks(keyword);
        }
    }
    @Override
    public boolean insertBorrowLog(BorrowLog log) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            libraryMapper.insertBorrowLog(log);
            sqlSession.commit();
        }
        return true;

    }
    @Override
    public boolean  updateBorrowLog(BorrowLog log)
    {
        int count=0;
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            count = libraryMapper.updateBorrowLog(log);
            sqlSession.commit();
        }
        return count>0;
    }

    @Override
    public boolean deleteBorrowLogById(String logId) {
        return false;
    }

    @Override
    public List<BorrowLog> getBorrowLogsByUserId(String userId)
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.selectBorrowLogsByUserId(userId);
        }
    }

    @Override
    public List<BorrowLog> getAllBorrowLogs()
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.selectAllBorrowLogs();
        }

    }

    @Override
    public List<BorrowLog> searchAllBorrowLogs(String keyword)
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.searchAllBorrowLogs(keyword);
        }
    }


    @Override
    public List<UserBorrowStatus> getAllUserBorrowStatus(){

        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            return libraryMapper.selectAllUserBorrowStatus();
        }
    }

    @Override
    public List<UserBorrowStatus> searchUserBorrowStatus(String keyword){
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            // 假设 LibraryMapper 接口中已有 searchUserBorrowStatus 方法的定义
            return libraryMapper.searchUserBorrowStatus(keyword);
        }
    }
    @Override
    public String findUsernameByUserId(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            // 假设 LibraryMapper 接口中已有 searchUserBorrowStatus 方法的定义
            return libraryMapper.findUsernameByUserId(userId);
        }
    }
    @Override
    public boolean borrowBook(String userId, String bookId)
    {
// 1. 开启一个 SqlSession，并将自动提交设为 false
        try (SqlSession session = MyBatisUtil.openSession()) {
            try {
                // 2. 从这个 session 中获取 Mapper，后续所有操作都用这个 mapper
                LibraryMapper librarymapper = session.getMapper(LibraryMapper.class);

                // 3. 【检查】获取图书信息并检查状态
                Book book = librarymapper.selectBookById(bookId);
                if (book == null || !"在馆".equals(book.getBorrowStatus().trim())) {
                    return false; // 图书不存在或已被借出，操作失败
                }

                // 4. 【准备数据】
                String username = librarymapper.findUsernameByUserId(userId); // 复用已有的查询
                String bookName = book.getBookName();
                String borrowDate = LocalDate.now().toString();
                String dueDate = LocalDate.now().plusMonths(1).toString();
                BorrowLog log = new BorrowLog(bookId, bookName, userId, username, borrowDate, dueDate);
                log.setLogId(UUID.randomUUID().toString());

                // 5. 【操作1：插入】执行插入借阅记录操作
                librarymapper.insertBorrowLog(log);

                // 6. 【操作2：更新】更新图书状态
                book.setBorrowStatus("已借出");
                librarymapper.updateBook(book);

                // 7. 【提交事务】所有操作成功，手动提交事务
                session.commit();
                return true;

            } catch (Exception e) {
                // 8. 【回滚事务】如果过程中出现任何异常，回滚所有操作
                session.rollback();
                System.err.println("借书事务执行失败，已回滚！");
                e.printStackTrace();
                return false;
            }
        } // try-with-resources 会确保 session 在最后被关闭







    }
    /**
     * 【重构】实现归还图书的事务操作
     * @param logId 要删除的借阅记录ID
     * @param bookId 要更新状态的图书ID
     * @return 操作是否成功
     */
    @Override
    public boolean returnBook(String logId, String bookId) {
        // 使用与 borrowBook 相同的事务管理模式
        try (SqlSession session = MyBatisUtil.openSession()) {
            try {
                // 1. 获取 Mapper 实例
                LibraryMapper libraryMapper = session.getMapper(LibraryMapper.class);

                // 2. 【操作1：删除】删除借阅记录
                int deletedRows = libraryMapper.deleteBorrowLogById(logId);
                if (deletedRows == 0) {
                    // 如果没有记录被删除，说明logId无效，直接回滚
                    session.rollback();
                    return false;
                }

                // 3. 【操作2：更新】获取图书，并更新其状态为“在馆”
                Book bookToReturn = libraryMapper.selectBookById(bookId);
                if (bookToReturn == null) {
                    // 如果找不到这本书，数据异常，回滚
                    session.rollback();
                    return false;
                }
                bookToReturn.setBorrowStatus("在馆");
                int updatedRows = libraryMapper.updateBook(bookToReturn);
                if (updatedRows == 0) {
                    // 更新失败，回滚
                    session.rollback();
                    return false;
                }

                // 4. 【提交事务】所有操作成功，提交
                session.commit();
                return true;

            } catch (Exception e) {
                // 5. 【回滚事务】出现任何异常，回滚
                session.rollback();
                System.err.println("还书事务执行失败，已回滚！");
                e.printStackTrace();
                return false;
            }
        }

    }
    /**
     * 【新增】实现管理员创建借阅记录的事务
     * @param bookId 要借阅的书籍ID
     * @param userId 借阅用户的ID
     * @return 操作是否成功
     */
    @Override
    public boolean adminCreateBorrowLog(String bookId, String userId) {
        // 使用 try-with-resources 确保 session 总能被关闭，并手动控制事务
        try (SqlSession session = MyBatisUtil.openSession()) {
            try {
                LibraryMapper libraryMapper = session.getMapper(LibraryMapper.class);

                // ========== 约束检查 ==========

                // 1. 检查书籍是否存在且状态为“在馆”
                Book book = libraryMapper.selectBookById(bookId);
                if (book == null) {
                    System.err.println("创建借阅记录失败：书籍ID " + bookId + " 不存在。");
                    return false; // 约束失败
                }
                if (!"在馆".equals(book.getBorrowStatus().trim())) {
                    System.err.println("创建借阅记录失败：书籍《" + book.getBookName() + "》已被借出。");
                    return false; // 约束失败
                }

                // 2. 检查用户是否存在 (通过查询用户名来间接验证)
                String username = libraryMapper.findUsernameByUserId(userId);
                if (username == null || username.isEmpty()) {
                    System.err.println("创建借阅记录失败：用户ID " + userId + " 不存在。");
                    return false; // 约束失败
                }

                // ========== 执行操作 ==========



                // 3. 准备新的 BorrowLog 对象
                String bookName = book.getBookName();
                String borrowDate = LocalDate.now().toString();
                String dueDate = LocalDate.now().plusMonths(1).toString(); // 默认借期一个月
                BorrowLog newLog = new BorrowLog(bookId, bookName, userId, username, borrowDate, dueDate);
                long timestamp = System.currentTimeMillis();
                int randomNumber = ThreadLocalRandom.current().nextInt(10, 100);
                newLog.setLogId("L" + timestamp + randomNumber); // 生成唯一的记录ID

                // 4. 操作1：插入新的借阅记录
                libraryMapper.insertBorrowLog(newLog);

                // 5. 操作2：更新书籍的借阅状态
                book.setBorrowStatus("已借出");
                libraryMapper.updateBook(book);

                // 6. 提交事务：所有操作成功，将更改写入数据库
                session.commit();
                System.out.println("成功创建借阅记录，书籍：" + bookName + "，用户：" + username);
                return true;

            } catch (Exception e) {
                // 7. 回滚事务：过程中出现任何异常，撤销所有已执行的操作
                session.rollback();
                System.err.println("创建借阅记录时发生异常，事务已回滚！");
                e.printStackTrace();
                return false;
            }
        }
    }



}