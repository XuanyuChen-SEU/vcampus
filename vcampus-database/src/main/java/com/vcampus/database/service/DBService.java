package com.vcampus.database.service;

import com.vcampus.database.mapper.Mapper;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.mapper.UserMapper;
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

public class DBService {

    private static MyBatisUtil myBatisUtil;
    private static Mapper mapper;

    /**
     * 初始化整个数据库
     */
    public void initializeDatabase() {
        // 使用 try-with-resources 确保 sqlSession 总能被关闭
        // MyBatisUtil 实例的创建也放入 try 中
        File userCsvTempFile = null;
        File studentCsvTempFile = null;
        SqlSession sqlSession = null;

        try {
            MyBatisUtil myBatisUtil = new MyBatisUtil();
            sqlSession = myBatisUtil.openSession();
            Mapper mapper = sqlSession.getMapper(Mapper.class);

            String dbName = "vcampus_db";

            mapper.dropDatabase(dbName);
            System.out.println("成功删除数据库: " + dbName);
            mapper.createDatabase(dbName);
            System.out.println("成功创建数据库: " + dbName);

            mapper.createUserTable();
            mapper.createStudentTable();
            System.out.println("成功在数据库 " + dbName + " 中创建表结构。");
            System.out.println("准备从CSV文件加载数据...");

            String userCSVPath = "db/tb_user.csv";
            String studentCSVPath = "db/tb_student.csv";

            // ★ 修改点 1: 指定临时文件存放的目录
            // 在当前程序运行目录下创建一个名为 "temp_csv" 的子目录
            Path tempDirectory = Paths.get(System.getProperty("user.dir"), "temp_csv");
            Files.createDirectories(tempDirectory); // 如果目录不存在，则创建它

            // 将资源文件写入我们指定的临时目录中
            userCsvTempFile = createTempFileFromResource(userCSVPath, tempDirectory.toFile());
            studentCsvTempFile = createTempFileFromResource(studentCSVPath, tempDirectory.toFile());

            // getAbsolutePath() 在某些系统上可能包含'..'，改用 getCanonicalPath() 获取更规范的路径
            String userPath = userCsvTempFile.getCanonicalPath().replace('\\', '/');
            String studentPath = studentCsvTempFile.getCanonicalPath().replace('\\', '/');

            System.out.println("正在从临时文件加载: " + userPath);
            System.out.println("正在从临时文件加载: " + studentPath);

            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            userMapper.loadUsersFromCsv(userPath);
            studentMapper.loadStudentsFromCsv(studentPath);
            sqlSession.commit();

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
            // 确保临时文件和目录在操作结束后被删除
            if (userCsvTempFile != null) {
                userCsvTempFile.delete();
            }
            if (studentCsvTempFile != null) {
                studentCsvTempFile.delete();
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