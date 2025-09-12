package com.vcampus.database.service;

import com.vcampus.database.mapper.Mapper;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.mapper.PasswordResetApplicationMapper;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            
            
            System.out.println("成功在数据库 " + dbName + " 中创建表结构。");
            System.out.println("准备从CSV文件加载数据...");

            // 1. 定义资源路径
            String userCSVPath = "db/tb_user.csv";
            String studentCSVPath = "db/tb_student.csv";
            String passwordResetApplicationCSVPath = "db/tb_password_reset_application.csv";

            // 2. 将资源文件写入临时文件，并获取其路径
            userCsvTempFile = createTempFileFromResource(userCSVPath);
            studentCsvTempFile = createTempFileFromResource(studentCSVPath);
            passwordResetApplicationCsvTempFile = createTempFileFromResource(passwordResetApplicationCSVPath);


            String userPath = userCsvTempFile.getAbsolutePath();
            String studentPath = studentCsvTempFile.getAbsolutePath();
            String passwordResetApplicationPath = passwordResetApplicationCsvTempFile.getAbsolutePath();


            System.out.println("正在从临时文件加载: " + userPath);
            System.out.println("正在从临时文件加载: " + studentPath);
            System.out.println("正在从临时文件加载: " + passwordResetApplicationPath);

            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            PasswordResetApplicationMapper passwordResetApplicationMapper = sqlSession.getMapper(PasswordResetApplicationMapper.class);

            // 3. 调用Mapper方法执行批量加载，传入临时文件的路径
            userMapper.loadUsersFromCsv(userPath);
            studentMapper.loadStudentsFromCsv(studentPath);
            passwordResetApplicationMapper.loadPasswordResetApplicationsFromCsv(passwordResetApplicationPath);
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
    private File createTempFileFromResource(String resourcePath) throws IOException {
        // 使用 getResourceAsStream 读取JAR包内的文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("在 classpath 中找不到资源文件: " + resourcePath);
        }

        // 创建一个带前缀和后缀的临时文件，以防文件名冲突
        File tempFile = File.createTempFile("temp_db_data_", ".csv");

        // 使用 try-with-resources 确保流能被自动关闭
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            inputStream.close();
        }

        // 返回创建好的临时文件
        return tempFile;
    }
}