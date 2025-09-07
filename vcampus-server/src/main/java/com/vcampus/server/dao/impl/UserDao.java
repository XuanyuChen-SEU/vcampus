package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.IUserDao;
import com.vcampus.common.dto.User;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.service.SqlFileExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
public class UserDao implements IUserDao {

    private static String resource;
    private static InputStream inputStream;
    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSession sqlSession;
    public static UserMapper usermapper;
    public static SqlFileExecutor sqlFileExecutor;
    String dbName ;
    private static String sqlFilePath = "db/tb_user.sql"; // SQL文件路径
    public UserDao() {
        UDInit();
    }


    @Override
    public void UDInit()
    {
        try {
        resource = "mybatis-config.xml";
        inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
        usermapper=sqlSession.getMapper(UserMapper.class);
        dbName = "vcampus_db";
        sqlFilePath = "db/tb_user.sql";

        usermapper.dropDatabase(dbName);
        System.out.println("删除数据库 " + dbName + " 成功");

        usermapper.createDatabase(dbName);
        System.out.println("创建数据库 " + dbName + " 成功");


        boolean sqlFileExists = checkSqlFileExists(sqlFilePath);
        if (sqlFileExists) {
            // 3.1 若存在 SQL 文件，执行文件
            sqlFileExecutor.executeSqlFile(sqlSession, sqlFilePath);
            System.out.println("已使用 SQL 文件初始化表和数据");
        } else {
            // 3.2 若不存在 SQL 文件，执行原生命令
            usermapper.createUserTable();
            System.out.println("在数据库 " + dbName + " 中创建 tb_user 表成功");

            usermapper.InsertTempData();
            System.out.println("固定用户数据（String类型7位数字）插入完成！");
            sqlSession.commit();
        }



        } catch (Exception e) {// 处理异常：可以打印日志、抛运行时异常等
            e.printStackTrace(); // 简单打印堆栈，也可替换为日志框架（如 log.error）
            // 若不想中断流程，也可抛运行时异常让上层感知
            throw new RuntimeException("MyBatis 配置文件加载失败", e);
        }

//        try {
//            resource = "mybatis-config.xml";
//            inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
//
//            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//            sqlSession = sqlSessionFactory.openSession();
//            usermapper=sqlSession.getMapper(UserMapper.class);
//            String dbName = "vcampus_db";
//
//            usermapper.dropDatabase(dbName);
//            System.out.println("删除数据库 " + dbName + " 成功");
//
//            usermapper.createDatabase(dbName);
//            System.out.println("创建数据库 " + dbName + " 成功");
//
//            usermapper.createUserTable();
//            System.out.println("在数据库 " + dbName + " 中创建 tb_user 表成功");
//
//            usermapper.InsertTempData();
//            System.out.println("固定用户数据（String类型7位数字）插入完成！");
//            sqlSession.commit();
//
//        } catch (IOException e) {// 处理异常：可以打印日志、抛运行时异常等
//            e.printStackTrace(); // 简单打印堆栈，也可替换为日志框架（如 log.error）
//            // 若不想中断流程，也可抛运行时异常让上层感知
//            throw new RuntimeException("MyBatis 配置文件加载失败", e);
//        }




    }
    @Override
    public User getUserById(String id)
    {
        return usermapper.selectById(id);
    }


    @Override
    public boolean updateUser(User user) {
        // 七位用户ID（使用String避免首位0丢失）
        String userId=user.getUserId();
        // 加密后的密码（传输为明文和存储为密文）
        String password=user.getPassword();
        User temp = new User();
        temp.setUserId(userId);
        temp.setPassword(password);
        int sign=usermapper.update(user);
        sqlSession.commit();
        return sign > 0;

    }

    @Override
    public boolean deleteUser(User user) {
        // 七位用户ID（使用String避免首位0丢失）
        String userId=user.getUserId();
        usermapper.deleteById(userId);
        sqlSession.commit();
        return true;
    }

    @Override
    public void UDClose()
    {
        if (sqlSession != null) {
            sqlSession.close();
        }
    }
    /**
     * 检查 SQL 文件是否存在于 resources 目录中
     */
    private boolean checkSqlFileExists(String filePath) {
        // 使用类加载器检查资源是否存在
        return getClass().getClassLoader().getResource(filePath) != null;
    }

}