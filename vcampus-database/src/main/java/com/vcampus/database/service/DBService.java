package com.vcampus.database.service;

import com.vcampus.database.mapper.*;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.ibatis.session.SqlSession;

import com.vcampus.database.mapper.Mapper;
import com.vcampus.database.mapper.PasswordResetApplicationMapper;
import com.vcampus.database.mapper.ShopMapper;
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
        MyBatisUtil myBatisUtil = new MyBatisUtil();
        SqlSession sqlSession = myBatisUtil.openSession();
        File userCsvTempFile = null;
        File studentCsvTempFile = null;
        File passwordResetApplicationCsvTempFile = null;
        File productCsvTempFile = null;
        File orderCsvTempFile = null;
        File favoriteCsvTempFile = null;
        File courseCsvTempFile = null;
        File courseSelectionCsvTempFile = null;
        File classSessionCsvTempFile = null;

        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);

            String dbName = "vcampus_db";

            mapper.dropDatabase(dbName);
            System.out.println("成功删除数据库: " + dbName);
            mapper.createDatabase(dbName);
            System.out.println("成功创建数据库: " + dbName);

            mapper.createUserTable();
            mapper.createStudentTable();
            mapper.createPasswordResetApplicationTable();
            mapper.createProductTable();
            mapper.createOrderTable();
            mapper.createFavoriteTable();
            mapper.createCoursesTable();
            mapper.createClassSessionsTable();
            mapper.createCourseSelectionsTable();
            
            System.out.println("成功在数据库 " + dbName + " 中创建表结构。");
            System.out.println("准备从CSV文件加载数据...");

            // 1. 定义资源路径
            String userCSVPath = "db/tb_user.csv";
            String studentCSVPath = "db/tb_student.csv";
            String passwordResetApplicationCSVPath = "db/tb_password_reset_application.csv";
            String productCSVPath = "db/tb_product.csv";
            String orderCSVPath = "db/tb_order.csv";
            String favoriteCSVPath = "db/tb_favorite.csv";
            String CourseCSVPath = "db/tb_courses.csv";
            String ClassSessionCSVPath = "db/tb_class_sessions.csv";
            String CourseSelectionCSVPath = "db/tb_course_selections.csv";

            // ★ 修改点 1: 指定临时文件存放的目录
            // 在当前程序运行目录下创建一个名为 "temp_csv" 的子目录
            Path tempDirectory = Paths.get(System.getProperty("user.dir"), "temp_csv");
            Files.createDirectories(tempDirectory); // 如果目录不存在，则创建它

            // 2. 将资源文件写入临时文件，并获取其路径
            userCsvTempFile = createTempFileFromResource(userCSVPath,tempDirectory.toFile());
            studentCsvTempFile = createTempFileFromResource(studentCSVPath,tempDirectory.toFile());
            passwordResetApplicationCsvTempFile = createTempFileFromResource(passwordResetApplicationCSVPath,tempDirectory.toFile());
            productCsvTempFile = createTempFileFromResource(productCSVPath,tempDirectory.toFile());
            orderCsvTempFile = createTempFileFromResource(orderCSVPath,tempDirectory.toFile());
            favoriteCsvTempFile = createTempFileFromResource(favoriteCSVPath,tempDirectory.toFile());
            courseCsvTempFile = createTempFileFromResource(CourseCSVPath,tempDirectory.toFile());
            classSessionCsvTempFile = createTempFileFromResource(ClassSessionCSVPath,tempDirectory.toFile());
            courseSelectionCsvTempFile = createTempFileFromResource(CourseSelectionCSVPath,tempDirectory.toFile());

            String userPath = userCsvTempFile.getAbsolutePath();
            String studentPath = studentCsvTempFile.getAbsolutePath();
            String passwordResetApplicationPath = passwordResetApplicationCsvTempFile.getAbsolutePath();
            String productPath = productCsvTempFile.getAbsolutePath();
            String orderPath = orderCsvTempFile.getAbsolutePath();
            String favoritePath = favoriteCsvTempFile.getAbsolutePath();
            String coursePath = courseCsvTempFile.getAbsolutePath();
            String classSessionPath = classSessionCsvTempFile.getAbsolutePath();
            String courseSelectionPath = courseSelectionCsvTempFile.getAbsolutePath();

            System.out.println("正在从临时文件加载: " + userPath);
            System.out.println("正在从临时文件加载: " + studentPath);
            System.out.println("正在从临时文件加载: " + passwordResetApplicationPath);
            System.out.println("正在从临时文件加载: " + productPath);
            System.out.println("正在从临时文件加载: " + orderPath);
            System.out.println("正在从临时文件加载: " + favoritePath);
            System.out.println("正在从临时文件加载: " + coursePath);
            System.out.println("正在从临时文件加载: " + courseSelectionPath);
            System.out.println("正在从临时文件加载: " + classSessionPath);
            
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            ClassSessionMapper classSessionMapper = sqlSession.getMapper(ClassSessionMapper.class);
            CourseSelectionMapper courseSelectionMapper = sqlSession.getMapper(CourseSelectionMapper.class);


            // 3. 调用Mapper方法执行批量加载，传入临时文件的路径
            userMapper.loadUsersFromCsv(userPath);
            studentMapper.loadStudentsFromCsv(studentPath);
            passwordResetApplicationMapper.loadPasswordResetApplicationsFromCsv(passwordResetApplicationPath);
            shopMapper.loadProductsFromCsv(productPath);
            shopMapper.loadOrdersFromCsv(orderPath);
            shopMapper.loadFavoritesFromCsv(favoritePath);
            courseMapper.loadCoursesFromCsv(coursePath);
            classSessionMapper.loadClassSessionsFromCsv(classSessionPath);
            courseSelectionMapper.loadCourseSelectionsFromCsv(courseSelectionPath);
            sqlSession.commit(); // 提交事务




            System.out.println("CSV数据批量加载成功！");
            System.out.println("数据库初始化成功，所有数据已提交。");

        } catch (IOException e) {
            // 捕获创建临时文件时可能发生的IO异常
            System.err.println("从资源创建临时文件失败: " + e.getMessage());
            e.printStackTrace();
            sqlSession.rollback(); // 出现异常，回滚事务
        } catch (Exception e) {
            // 捕获其他所有异常
            System.err.println("数据库初始化过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            sqlSession.rollback(); // 出现异常，回滚事务
        } finally {
            // 4. 确保临时文件在操作结束后被删除
            if (userCsvTempFile != null && userCsvTempFile.exists()) {
                userCsvTempFile.delete();
            }
            if (studentCsvTempFile != null && studentCsvTempFile.exists()) {
                studentCsvTempFile.delete();
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
            if (courseCsvTempFile != null && courseCsvTempFile.exists()) {
                courseCsvTempFile.delete();
            }
            if (courseSelectionCsvTempFile != null && courseSelectionCsvTempFile.exists()) {
                courseSelectionCsvTempFile.delete();
            }
            if (classSessionCsvTempFile != null && classSessionCsvTempFile.exists()) {
                classSessionCsvTempFile.delete();
                }
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    /**
     * 从classpath的资源路径创建一个临时文件
     *
     * @param resourcePath 资源文件的路径 (例如 "db/tb_user.csv")
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