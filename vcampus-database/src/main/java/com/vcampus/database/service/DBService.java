package com.vcampus.database.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vcampus.database.mapper.*;
import org.apache.ibatis.session.SqlSession;

import com.vcampus.database.mapper.ClassSessionMapper;
import com.vcampus.database.mapper.CourseMapper;
import com.vcampus.database.mapper.CourseSelectionMapper;
import com.vcampus.database.mapper.EmailMapper;
import com.vcampus.database.mapper.LibraryMapper;
import com.vcampus.database.mapper.Mapper;
import com.vcampus.database.mapper.PasswordResetApplicationMapper;
import com.vcampus.database.mapper.ShopMapper;
import com.vcampus.database.mapper.StudentLeaveApplicationMapper;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.utils.MyBatisUtil;

public class DBService {

    private static MyBatisUtil myBatisUtil;
    private static Mapper mapper;

    /**
     * 初始化整个数据库
     */
    public void initializeDatabase() {
        // 使用 try-with-resources 确保 sqlSession 总能被关闭
        // MyBatisUtil 实例的创建也放入 try 中
        SqlSession sqlSession = null;
        File userCsvTempFile = null;
        File studentCsvTempFile = null;
        File studentLeaveApplicationCsvTempFile = null;
        File teacherCsvTempFile = null;
        File bookCsvTempFile = null;
        File borrowLogCsvTempFile = null;
        File passwordResetApplicationCsvTempFile = null;
        File productCsvTempFile = null;
        File orderCsvTempFile = null;
        File favoriteCsvTempFile = null;
        File balanceCsvTempFile = null;
        File courseCsvTempFile = null;
        File courseSelectionCsvTempFile = null;
        File classSessionCsvTempFile = null;
        File emailCsvTempFile = null;

        try {
            MyBatisUtil myBatisUtil = new MyBatisUtil();
            sqlSession = myBatisUtil.openSession();
            Mapper mapper = sqlSession.getMapper(Mapper.class);

            String dbName = "vcampus_db";

            mapper.dropDatabase(dbName);
            System.out.println("成功删除数据库: " + dbName);
            mapper.createDatabase(dbName);
            System.out.println("成功创建数据库: " + dbName);
            mapper.useDatabase(dbName);

            mapper.createUserTable();
            mapper.createStudentTable();
            mapper.createStudentLeaveApplicationTable();
            mapper.createTeacherTable();
            mapper.createPasswordResetApplicationTable();
            mapper.createProductTable();
            mapper.createOrderTable();
            mapper.createFavoriteTable();
            mapper.createBalanceTable();
            mapper.createCoursesTable();
            mapper.createClassSessionsTable();
            mapper.createCourseSelectionsTable();
            mapper.createBookTable();
            mapper.createBorrowLogTable();
            mapper.createEmailTable();

            System.out.println("成功在数据库 " + dbName + " 中创建表结构。");
            System.out.println("准备从CSV文件加载数据...");

            String userCSVPath = "db/tb_user.csv";
            String studentCSVPath = "db/tb_student.csv";
            String teacherCSVPath = "db/tb_teacher.csv";
            String studentLeaveApplicationCSVPath = "db/tb_student_leave_application.csv";
            String BookCSVPath = "db/tb_book.csv";
            String BorrowLogCSVPath = "db/tb_borrow_log.csv";
            String passwordResetApplicationCSVPath = "db/tb_password_reset_application.csv";
            String productCSVPath = "db/tb_product.csv";
            String orderCSVPath = "db/tb_order.csv";
            String favoriteCSVPath = "db/tb_favorite.csv";
            String BalanceCSVPath = "db/tb_balance.csv";
            String CourseCSVPath = "db/tb_courses.csv";
            String ClassSessionCSVPath = "db/tb_class_sessions.csv";
            String CourseSelectionCSVPath = "db/tb_course_selections.csv";
            String EmailCSVPath = "db/tb_email.csv";

            // ★ 修改点 1: 指定临时文件存放的目录
            // 在当前程序运行目录下创建一个名为 "temp_csv" 的子目录
            Path tempDirectory = Paths.get(System.getProperty("user.dir"), "temp_csv");
            Files.createDirectories(tempDirectory); // 如果目录不存在，则创建它

            // 将资源文件写入我们指定的临时目录中
            userCsvTempFile = createTempFileFromResource(userCSVPath, tempDirectory.toFile());
            studentCsvTempFile = createTempFileFromResource(studentCSVPath, tempDirectory.toFile());
            studentLeaveApplicationCsvTempFile = createTempFileFromResource(studentLeaveApplicationCSVPath, tempDirectory.toFile());
            teacherCsvTempFile = createTempFileFromResource(teacherCSVPath, tempDirectory.toFile());
            bookCsvTempFile = createTempFileFromResource(BookCSVPath, tempDirectory.toFile());
            borrowLogCsvTempFile = createTempFileFromResource(BorrowLogCSVPath, tempDirectory.toFile());
            passwordResetApplicationCsvTempFile = createTempFileFromResource(passwordResetApplicationCSVPath,tempDirectory.toFile());
            productCsvTempFile = createTempFileFromResource(productCSVPath,tempDirectory.toFile());
            orderCsvTempFile = createTempFileFromResource(orderCSVPath,tempDirectory.toFile());
            favoriteCsvTempFile = createTempFileFromResource(favoriteCSVPath,tempDirectory.toFile());
            balanceCsvTempFile = createTempFileFromResource(BalanceCSVPath,tempDirectory.toFile());
            courseCsvTempFile = createTempFileFromResource(CourseCSVPath,tempDirectory.toFile());
            classSessionCsvTempFile = createTempFileFromResource(ClassSessionCSVPath,tempDirectory.toFile());
            courseSelectionCsvTempFile = createTempFileFromResource(CourseSelectionCSVPath,tempDirectory.toFile());
            emailCsvTempFile = createTempFileFromResource(EmailCSVPath,tempDirectory.toFile());

            // getAbsolutePath() 在某些系统上可能包含'..'，改用 getCanonicalPath() 获取更规范的路径
            String userPath = userCsvTempFile.getCanonicalPath().replace('\\', '/');
            String studentPath = studentCsvTempFile.getCanonicalPath().replace('\\', '/');
            String studentLeaveApplicationPath = studentLeaveApplicationCsvTempFile.getAbsolutePath();
            String teacherPath = teacherCsvTempFile.getCanonicalPath().replace('\\', '/');
            String bookPath = bookCsvTempFile.getCanonicalPath().replace('\\', '/');
            String borrowLogPath = borrowLogCsvTempFile.getCanonicalPath().replace('\\', '/');
            String passwordResetApplicationPath = passwordResetApplicationCsvTempFile.getAbsolutePath();
            String productPath = productCsvTempFile.getAbsolutePath();
            String orderPath = orderCsvTempFile.getAbsolutePath();
            String favoritePath = favoriteCsvTempFile.getAbsolutePath();
            String balancePath = balanceCsvTempFile.getAbsolutePath();
            String coursePath = courseCsvTempFile.getAbsolutePath();
            String classSessionPath = classSessionCsvTempFile.getAbsolutePath();
            String courseSelectionPath = courseSelectionCsvTempFile.getAbsolutePath();
            String emailPath = emailCsvTempFile.getCanonicalPath().replace('\\', '/');

            System.out.println("正在从临时文件加载: " + userPath);
            System.out.println("正在从临时文件加载: " + studentPath);
            System.out.println("正在从临时文件加载: " + studentLeaveApplicationPath);
            System.out.println("正在从临时文件加载教师数据: " + teacherPath);
            System.out.println("正在从临时文件加载: " + passwordResetApplicationPath);
            System.out.println("正在从临时文件加载: " + productPath);
            System.out.println("正在从临时文件加载: " + orderPath);
            System.out.println("正在从临时文件加载: " + favoritePath);
            System.out.println("正在从临时文件加载: " + balancePath);
            System.out.println("正在从临时文件加载: " + coursePath);
            System.out.println("正在从临时文件加载: " + courseSelectionPath);
            System.out.println("正在从临时文件加载: " + classSessionPath);
            System.out.println("正在从临时文件加载: " + bookPath);
            System.out.println("正在从临时文件加载: " + borrowLogPath);
            System.out.println("正在从临时文件加载: " + emailPath);

            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            StudentLeaveApplicationMapper studentLeaveApplicationMapper = sqlSession.getMapper(StudentLeaveApplicationMapper.class);
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            LibraryMapper libraryMapper = sqlSession.getMapper(LibraryMapper.class);
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            ClassSessionMapper classSessionMapper = sqlSession.getMapper(ClassSessionMapper.class);
            CourseSelectionMapper courseSelectionMapper = sqlSession.getMapper(CourseSelectionMapper.class);
            EmailMapper emailMapper = sqlSession.getMapper(EmailMapper.class);

            userMapper.loadUsersFromCsv(userPath);
            studentMapper.loadStudentsFromCsv(studentPath);
            studentLeaveApplicationMapper.loadStudentLeaveApplicationsFromCsv(studentLeaveApplicationPath);
            teacherMapper.loadTeachersFromCsv(teacherPath);
            libraryMapper.loadBooksFromCsv(bookPath);
            libraryMapper.loadBorrowLogsFromCsv(borrowLogPath);
            passwordResetApplicationMapper.loadPasswordResetApplicationsFromCsv(passwordResetApplicationPath);
            shopMapper.loadProductsFromCsv(productPath);
            shopMapper.loadOrdersFromCsv(orderPath);
            shopMapper.loadFavoritesFromCsv(favoritePath);
            shopMapper.loadBalancesFromCsv(balancePath);
            courseMapper.loadCoursesFromCsv(coursePath);
            classSessionMapper.loadClassSessionsFromCsv(classSessionPath);
            courseSelectionMapper.loadCourseSelectionsFromCsv(courseSelectionPath);
            emailMapper.loadEmailsFromCsv(emailPath);
            sqlSession.commit(); // 提交事务

            System.out.println("CSV数据批量加载成功！");
            System.out.println("数据库初始化成功，所有数据已提交。");

        } catch (IOException e) {
            System.err.println("从资源创建临时文件或文件IO失败: " + e.getMessage());
            e.printStackTrace();
            if (sqlSession != null) sqlSession.rollback();
        } catch (Exception e) {
            System.err.println("数据库初始化过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            if (sqlSession != null) sqlSession.rollback();
        } finally {
            // 4. 确保临时文件在操作结束后被删除
            if (userCsvTempFile != null && userCsvTempFile.exists()) {
                userCsvTempFile.delete();
            }
            if (studentCsvTempFile != null && studentCsvTempFile.exists()) {
                studentCsvTempFile.delete();
            }
            if (studentLeaveApplicationCsvTempFile != null && studentLeaveApplicationCsvTempFile.exists()) {
                studentLeaveApplicationCsvTempFile.delete();
            }
            if (bookCsvTempFile != null) {
                bookCsvTempFile.delete();
            }
            if (borrowLogCsvTempFile != null) {
                borrowLogCsvTempFile.delete();
            }
            // 尝试删除临时目录，如果目录为空则会被删除
            Path tempDirPath = Paths.get(System.getProperty("user.dir"), "temp_csv");
            if(Files.exists(tempDirPath)) {
                try {
                    // 仅当目录为空时才能删除成功
                    Files.deleteIfExists(tempDirPath);
                } catch (IOException e) {
                    System.err.println("临时目录 temp_csv 不为空，无法删除。可能需要手动清理。");
                }
            }
            if (passwordResetApplicationCsvTempFile != null && passwordResetApplicationCsvTempFile.exists()) {
                passwordResetApplicationCsvTempFile.delete();
            }
            if (productCsvTempFile != null && productCsvTempFile.exists()) {
                productCsvTempFile.delete();
            }
            if (orderCsvTempFile != null && orderCsvTempFile.exists()) {
                orderCsvTempFile.delete();
            }
            if (favoriteCsvTempFile != null && favoriteCsvTempFile.exists()) {
                favoriteCsvTempFile.delete();
            }
            if (balanceCsvTempFile != null && balanceCsvTempFile.exists()) {
                balanceCsvTempFile.delete();
            }
            if (courseCsvTempFile != null && courseCsvTempFile.exists()) {
                courseCsvTempFile.delete();
            }
            if (courseSelectionCsvTempFile != null && courseSelectionCsvTempFile.exists()) {
                courseSelectionCsvTempFile.delete();
            }
            if (classSessionCsvTempFile != null && classSessionCsvTempFile.exists()) {
                classSessionCsvTempFile.delete();
            }
            if (emailCsvTempFile != null && emailCsvTempFile.exists()) {
                emailCsvTempFile.delete();
            }
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    /**
     * 从classpath的资源路径创建一个临时文件到【指定目录】
     *
     * @param resourcePath 资源文件的路径 (例如 "db/tb_user.csv")
     * @param directory    用于存放临时文件的目录
     * @return 创建的临时文件对象
     * @throws IOException 如果资源找不到或文件写入失败
     */
    private File createTempFileFromResource(String resourcePath, File directory) throws IOException {
        // ★ 修改点 2: 确保目标目录存在
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("无法创建临时目录: " + directory.getAbsolutePath());
            }
        }

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("在 classpath 中找不到资源文件: " + resourcePath);
        }

        // ★ 修改点 3: 使用指定目录的 createTempFile 方法
        // 从资源路径中提取文件名作为参考
        String fileName = new File(resourcePath).getName();
        String prefix = fileName.substring(0, fileName.lastIndexOf('.'));
        String suffix = fileName.substring(fileName.lastIndexOf('.'));

        File tempFile = File.createTempFile(prefix + "_", suffix, directory);

        // 使用 try-with-resources 确保流能被自动关闭
        try (OutputStream outputStream = new FileOutputStream(tempFile);
             InputStream finalInputStream = inputStream) { // 确保 inputStream 也能被关闭
            byte[] buffer = new byte[4096]; // 缓冲区可以大一点，效率更高
            int bytesRead;
            while ((bytesRead = finalInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // 返回创建好的临时文件
        return tempFile;
    }
}