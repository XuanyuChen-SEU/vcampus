//package com.vcampus.database;
//import com.vcampus.common.dto.Book;
//import com.vcampus.common.dto.BorrowLog;
//import com.vcampus.database.mapper.LibraryMapper;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.junit.Test;
//
//import java.io.IOException;
//
//
//
//
//import org.apache.ibatis.io.Resources;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//
//
//
//public class Librarymappertest {
//
//
//
//    /**
//     * 测试创建图书表和借阅记录表
//     */
//    @Test
//    public void testCreateTables() throws IOException {
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//            libraryMapper.createBookTable();
//            System.out.println("创建图书表(tb_book)成功");
//
//            libraryMapper.createBorrowLogTable();
//            System.out.println("创建借阅记录表(tb_borrow_log)成功");
//
//            sqlSession.commit();
//        }
//    }
//    /**
//     * 测试删除图书表和借阅记录表
//     */
//    @Test
//    public void testDropTables() throws IOException {
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//        String dbName = "vcampus_db";
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//            // 注意：通常先删除有外键的表
//            libraryMapper.dropBorrowLogTable(dbName);
//            System.out.println("删除借阅记录表(tb_borrow_log)成功");
//
//            libraryMapper.dropBookTable(dbName);
//            System.out.println("删除图书表(tb_book)成功");
//
//            sqlSession.commit();
//        }
//    }
//
//
//    /**
//     * 测试从CSV文件加载图书数据
//     */
//    @Test
//    public void testLoadBooksFromCsv() throws Exception {
//        String csvResourcePath = "db/tb_book.csv"; // 假设的CSV文件名
//        URL resourceUrl = getClass().getClassLoader().getResource(csvResourcePath);
//        if (resourceUrl == null) {
//            throw new RuntimeException("在 resources 目录中找不到文件: " + csvResourcePath);
//        }
//        File csvFile = new File(resourceUrl.toURI());
//        String absolutePath = csvFile.getAbsolutePath();
//        System.out.println("正在从文件加载: " + absolutePath);
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//        libraryMapper.loadBooksFromCsv(absolutePath);
//        sqlSession.commit();
//        System.out.println("图书CSV数据批量加载成功！");
//    }
//    /**
//     * 测试从CSV文件加载借阅记录数据
//     */
//    @Test
//    public void testLoadBorrowLogsFromCsv() throws Exception {
//        String csvResourcePath = "db/tb_borrow_log.csv"; // 假设的CSV文件名
//        URL resourceUrl = getClass().getClassLoader().getResource(csvResourcePath);
//        if (resourceUrl == null) {
//            throw new RuntimeException("在 resources 目录中找不到文件: " + csvResourcePath);
//        }
//        String absolutePath = new File(resourceUrl.toURI()).getAbsolutePath();
//        System.out.println("正在从文件加载借阅记录: " + absolutePath);
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//            libraryMapper.loadBorrowLogsFromCsv(absolutePath);
//            sqlSession.commit();
//            System.out.println("借阅记录CSV数据批量加载成功！");
//        }
//    }
//    // ==========================================================
//    // Book (图书) 操作测试
//    // ==========================================================
//
//    @Test
//    public void testInsertBook() throws IOException {
//        Book newBook = new Book("B999", "测试驱动开发", "Kent Beck", "9787115143313", "测试出版社", "TDD经典", "在馆");
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        int count = libraryMapper.insertBook(newBook);
//        System.out.println("插入图书影响行数: " + count);
//        sqlSession.commit();
//        sqlSession.close();
//    }
//
//    @Test
//    public void testSelectAllBooks() throws IOException {
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        List<Book> books = libraryMapper.selectAllBooks();
//        books.forEach(System.out::println);
//        sqlSession.close();
//    }
//
//    @Test
//    public void testSelectBookById() throws IOException {
//        String bookId = "B001"; // 假设这个ID存在
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        Book book = libraryMapper.selectBookById(bookId);
//        System.out.println(book);
//        sqlSession.close();
//    }
//
//    @Test
//    public void testUpdateBook() throws IOException {
//        Book bookToUpdate = new Book();
//        bookToUpdate.setBookId("B001"); // 假设要更新这本书
//        bookToUpdate.setBorrowStatus("已借出"); // 将其状态改为已借出
//        bookToUpdate.setDescription("这是更新后的描述");
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        int count = libraryMapper.updateBook(bookToUpdate);
//        System.out.println("更新图书影响行数: " + count);
//        sqlSession.commit();
//        sqlSession.close();
//    }
//
//    // ==========================================================
//    // BorrowLog (借阅记录) 操作测试
//    // ==========================================================
//
//    //
//    @Test
//    public void testInsertBorrowLog() throws IOException {
//        // 【修正】第一步：为新的借阅记录生成一个唯一的 logId
//        String logId = System.currentTimeMillis() + "_" + (int)((Math.random() * 9000) + 1000);
//
//        // 【修正】第二步：将 logId 作为第一个参数传入构造函数
//        BorrowLog newLog = new BorrowLog();
//
//// 1. 手动生成一个唯一的ID
//        String uniqueLogId = "11213";
//
//// 2. 将生成的ID设置到对象中
//        newLog.setLogId(uniqueLogId); // 假设你的set方法叫setLogId
//
//// 3. 设置其他属性
//        newLog.setBookId("B002");
//        newLog.setBookName("Effective Java");
//        newLog.setUserId("5678901");
//        newLog.setUsername("测试用户");
//        newLog.setBorrowDate("2025-09-13"); // 请注意日期格式
//        newLog.setDueDate("2025-10-13");   // 请注意日期格式
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        int count = libraryMapper.insertBorrowLog(newLog);
//        System.out.println("插入借阅记录影响行数: " + count);
//        sqlSession.commit();
//        sqlSession.close();
//    }
//
//    @Test
//    public void testSelectBorrowLogsByUserId() throws IOException {
//        String userId = "5678901"; // 假设这个用户存在借阅记录
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        List<BorrowLog> logs = libraryMapper.selectBorrowLogsByUserId(userId);
//        logs.forEach(System.out::println);
//        sqlSession.close();
//    }
//
//    // ==========================================================
//    // 辅助查询测试
//    // ==========================================================
//
//    @Test
//    public void testFindUsernameByUserId() throws IOException {
//        String userId = "1234567"; // 假设这个用户ID在tb_student表中存在
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
//
//        String username = libraryMapper.findUsernameByUserId(userId);
//        System.out.println("查询到用户 " + userId + " 的姓名为: " + username);
//        sqlSession.close();
//    }
//
//
//}
