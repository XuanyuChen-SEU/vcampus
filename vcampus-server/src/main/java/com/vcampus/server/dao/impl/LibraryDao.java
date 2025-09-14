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
     * 【新增】实现 ILibraryDao 接口中定义的 returnBook 方法
     * @param logId 要删除的借阅记录ID
     * @param bookId 要更新状态的图书ID
     * @return 操作是否成功
     */
    @Override
    public boolean returnBook(String logId, String bookId) {
        SqlSession session = null;
        try {
            // 获取数据库会话，并开启事务
            session = MyBatisUtil.getSqlSessionFactory().openSession();

            // 1. 删除借阅记录 (调用 LibraryMapper.xml 中的 deleteBorrowLog)
            int deletedRows = session.delete("com.vcampus.common.dao.ILibraryDao.deleteBorrowLog", logId);

            // 2. 更新图书状态为“在馆” (调用 LibraryMapper.xml 中的 updateBookStatus)
            int updatedRows = session.update("com.vcampus.common.dao.ILibraryDao.updateBookStatus", bookId);

            // 3. 如果两个操作都成功，则提交事务
            if (deletedRows > 0 && updatedRows > 0) {
                session.commit();
                return true;
            } else {
                // 否则回滚事务
                session.rollback();
                return false;
            }
        } catch (Exception e) {
            // 如果发生任何异常，都回滚事务
            if (session != null) {
                session.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            // 确保最后关闭会话
            if (session != null) {
                session.close();
            }
        }
    }
}