package com.vcampus.database;
import com.vcampus.database.mapper.UserMapper;
import com.vcampus.database.service.SqlFileExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import com.vcampus.common.dto.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usermappertest {
    @Test
    public void testSelect() throws IOException {
        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        List<User> users = userMapper.selectAll();
        System.out.println(users);
        sqlSession.close();
    }
    @Test
    public void testSelectById() throws IOException {
        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //List<User> users = userMapper.selectAll();

        User user=userMapper.selectById("1234567");
        System.out.println(user);
        sqlSession.close();
    }
    @Test
    public void testSelctByCondition() throws IOException {
//        int status = 1;
//        String brand_name = "华为";
//        String company_name = "华为";
        String userId="1234567";
        String password = "7654321";
        //userId = "%"+userId+"%";
        //password = "%"+password+"%";


        Map map = new HashMap<>();
        map.put("userId", userId);
        map.put("password", password);
//        map.put("status", status);
//        map.put("company_name", company_name);
//        map.put("brand_name", brand_name);


        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);


        //List<Brand> brands = brandMapper.selectByCondition(status, brand_name, company_name);
        //List<Brand> brands = brandMapper.selectByCondition(brand);
        List<User> users = userMapper.selectByCondition(map);
        System.out.println(users);
        //4.释放资源
        sqlSession.close();


    }
    @Test
    public void testSelectBySingleCondition() throws IOException {



        String userId="1234567";
        String password = "7654321";
        //userId = "%"+userId+"%";
        //password = "%"+password+"%";
        User user = new User();
        //user.setUserId(userId);
        user.setPassword(password);








        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        //List<User> users = userMapper.selectAll();
        List<User> users = userMapper.selectBySingleCondition(user);
        System.out.println(users);

        sqlSession.close();
    }

    @Test
    public void testAdd() throws IOException
    {
        String userId="8165577";
        String password ="8136557";
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.add(user);
        sqlSession.commit();
        String use = user.getUserId();
        System.out.println(use);

        sqlSession.close();
    }
    @Test
    public void testupdate() throws IOException
    {
        String userId="8165577";
        String password ="1234567";
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int count =userMapper.update(user);
        System.out.println(count);
        sqlSession.commit();
        sqlSession.close();
    }





    @Test
    public void testdeleteByuserId() throws IOException
    {
        String userId="8165577";

        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.deleteById(userId);
        sqlSession.commit();

        sqlSession.close();
    }
    @Test
    public void testDeleteByIds() throws IOException {
        String userIds[]={"1234567","2345678"};
        //1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        userMapper.deleteByIds(userIds);
        sqlSession.commit();
        //4.释放资源
        sqlSession.close();
    }
    @Test
    public void testInit() throws Exception {

//1.加载mubatis配置文件，获取SqlSessionFactory
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);//字符串传进来  返回字节输入流
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);//把流传进来就可以了

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        SqlFileExecutor sqlFileExecutor;
        String dbName = "vcampus_db";

        userMapper.dropDatabase(dbName);
        System.out.println("删除数据库 " + dbName + " 成功");

        userMapper.createDatabase(dbName);
        System.out.println("创建数据库 " + dbName + " 成功");

        SqlFileExecutor.executeSqlFile(sqlSession, "db/tb_user.sql");
//        userMapper.createUserTable();
//        System.out.println("在数据库 " + dbName + " 中创建 tb_user 表成功");
//
//
//        userMapper.InsertTempData();
//        System.out.println("固定用户数据（String类型7位数字）插入完成！");

        sqlSession.commit();
        sqlSession.close();
    }

}

