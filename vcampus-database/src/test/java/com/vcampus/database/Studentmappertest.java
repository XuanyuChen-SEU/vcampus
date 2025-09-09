package com.vcampus.database;

import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.service.SqlFileExecutor;
import com.vcampus.common.dto.Student;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Studentmappertest {

    // 原有测试方法保持不变...
    @Test
    public void testSelect() throws IOException {
        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<Student> students = studentMapper.selectAll();
        System.out.println(students);
        sqlSession.close();
    }

    @Test
    public void testSelectById() throws IOException {
        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        Student student = studentMapper.selectById("2345678"); // 假设学生ID为2023001
        System.out.println(student);
        sqlSession.close();
    }

    @Test
    public void testSelectByCondition() throws IOException {
        String studentId = "20210002";
        String name = "李四";

        Map<String, Object> map = new HashMap<>();
        map.put("studentId", studentId);
        map.put("name", name);

        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<Student> students = studentMapper.selectByCondition(map);
        System.out.println(students);
        sqlSession.close();
    }

    @Test
    public void testSelectBySingleCondition() throws IOException {
        String major = "计算机科学与技术";
        Student student = new Student();
        student.setMajor(major);

        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        List<Student> students = studentMapper.selectBySingleCondition(student);
        System.out.println(students);
        sqlSession.close();
    }

    @Test
    public void testAdd() throws IOException {
        String studentId = "2023002";
        String name = "李wu";
        Student student = new Student();
        student.setUserId(studentId);
        student.setName(name);

        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        studentMapper.add(student);
        sqlSession.commit();
        System.out.println("新增学生ID: " + student.getUserId());
        sqlSession.close();
    }

    @Test
    public void testUpdate() throws IOException {
        String newName = "李小四";
        String userId = "2023002";
        Student student = new Student();
        student.setUserId(userId);
        student.setName(newName);

        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        int count = studentMapper.update(student);
        System.out.println("影响行数: " + count);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testDeleteById() throws IOException {
        String userId = "2023002";

        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        studentMapper.deleteById(userId);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testDeleteByIds() throws IOException {
        String[] studentIds = {"3456789", "4567890"};

        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

        studentMapper.deleteByIds(studentIds);
        sqlSession.commit();
        sqlSession.close();
    }

    // 新增的数据库和表操作测试方法，模仿Usermappertest的testInit风格
    @Test
    public void testInit() throws Exception {
        // 加载MyBatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        String dbName = "vcampus_db"; // 与User测试保持一致的数据库名

        // 删除数据库
        studentMapper.dropDatabase(dbName);
        System.out.println("删除数据库 " + dbName + " 成功");

        // 创建数据库
        studentMapper.createDatabase(dbName);
        System.out.println("创建数据库 " + dbName + " 成功");

        // 创建学生表（如果有对应的SQL文件，可执行SQL文件）
        studentMapper.createStuTable(); // 此处方法名保持与UserMapper一致，实际应对应学生表
        System.out.println("在数据库 " + dbName + " 中创建学生表成功");

        // 如需执行学生表的SQL脚本，可添加此行（需确保存在对应SQL文件）
        SqlFileExecutor.executeSqlFile(sqlSession, "db/tb_student.sql");

        sqlSession.commit();
        sqlSession.close();
    }

    // 单独测试删除学生表的方法
    @Test
    public void testStuTable() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        String dbName = "vcampus_db";

        studentMapper.dropStuTable(dbName);
        System.out.println("删除数据库 " + dbName + " 中的学生表成功");

        sqlSession.commit();
        sqlSession.close();
    }
    @Test
    public void testLoadDataFromCsv() throws Exception {
        // 1. 获取CSV文件在项目中的URL
        String csvResourcePath = "db/tb_student.csv";
        URL resourceUrl = getClass().getClassLoader().getResource(csvResourcePath);
        if (resourceUrl == null) {
            throw new RuntimeException("在 resources 目录中找不到文件: " + csvResourcePath);
        }

        // 2. 将URL转换为绝对文件路径
        File csvFile = new File(resourceUrl.toURI());
        String absolutePath = csvFile.getAbsolutePath();
        System.out.println("正在从文件加载: " + absolutePath);

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        // 3. 调用Mapper方法执行批量加载
        // (这里的 userMapper 和 sqlSession 应该通过 @Before 方法初始化)
        studentMapper.loadStudentsFromCsv(absolutePath);
        sqlSession.commit(); // 提交事务

        System.out.println("CSV数据批量加载成功！");
    }
}