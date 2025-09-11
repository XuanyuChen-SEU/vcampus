package com.vcampus.database.utils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
public class MyBatisUtil {
    private static  SqlSessionFactory sqlSessionFactory;
    private static InputStream inputStream;
    private static String resource;

    public MyBatisUtil(){
        try {
            resource = "mybatis-config.xml";
            inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            // 初始化的严重错误，应用无法继续运行
            throw new RuntimeException("Could not initialize MyBatis SqlSessionFactory", e);
        }
    }
    /**
     * 获取 SqlSessionFactory 实例
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * 打开一个新的 SqlSession
     * @return SqlSession
     */
    public static SqlSession openSession() {
        return sqlSessionFactory.openSession();
    }


}
