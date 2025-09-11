package com.vcampus.database;

import com.vcampus.database.mapper.Mapper;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.service.SqlFileExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class Mappertest {





    @Test
    public void testLoadDataFromCsv() throws Exception {

        // 1. 获取CSV文件在项目中的URL
        String UserCSVPath = "db/tb_user.csv";
        String StudentCSVPath = "db/tb_student.csv";

        URL userUrl = getClass().getClassLoader().getResource(UserCSVPath);
        URL studentUrl = getClass().getClassLoader().getResource(StudentCSVPath);
        if (userUrl == null || studentUrl == null) {
            throw new RuntimeException("在 resources 目录中找不到文件: " );
        }

        // 2. 将URL转换为绝对文件路径
        File UsercsvFile = new File(userUrl.toURI());
        File StudentcsvFile = new File(studentUrl.toURI());



        String userPath = UsercsvFile.getAbsolutePath();
        String studentPath = StudentcsvFile.getAbsolutePath();
        System.out.println("正在从文件加载: ");

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        // 3. 调用Mapper方法执行批量加载
        // (这里的 userMapper 和 sqlSession 应该通过 @Before 方法初始化)
        userMapper.loadUsersFromCsv(userPath);
        studentMapper.loadStudentsFromCsv(studentPath);
        sqlSession.commit(); // 提交事务

        System.out.println("CSV数据批量加载成功！");
    }




    @Test
    public void testInit() throws Exception {
        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        Mapper mapper = sqlSession.getMapper(Mapper.class);
        String dbName = "vcampus_db"; // 与User测试保持一致的数据库名

        // 删除数据库
        mapper.dropDatabase(dbName);
        System.out.println("删除数据库 " + dbName + " 成功");

        // 创建数据库
        mapper.createDatabase(dbName);
        System.out.println("创建数据库 " + dbName + " 成功");

        // 创建学生表（如果有对应的SQL文件，可执行SQL文件）
        mapper.createUserTable();
        mapper.createStudentTable();
        System.out.println("在数据库 " + dbName + " 中创建各表成功");

        // 如需执行学生表的SQL脚本，可添加此行（需确保存在对应SQL文件）
        SqlFileExecutor.executeSqlFile(sqlSession, "db/tb_student.sql");
        SqlFileExecutor.executeSqlFile(sqlSession, "db/tb_user.sql");
        sqlSession.commit();
        sqlSession.close();
    }
}
