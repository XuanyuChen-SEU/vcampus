//package com.vcampus.database;
//
//import com.vcampus.common.dto.ClassSession;
//import com.vcampus.database.mapper.ClassSessionMapper;
//import org.apache.ibatis.io.Resources;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.session.SqlSessionFactoryBuilder;
//import org.junit.Test;
//
//import java.io.InputStream;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class ClassSessionMapperTest {
//
//
//
//
//
//
//
//
//
//    /**
//     * 测试根据ID查询教学班。
//     * 前提：数据库中必须存在ID为 "SE_S01" 的教学班。
//     */
//    @Test
//    public void testSelectById() throws Exception {
//        String sessionIdToTest = "SE_S01"; // 假设这是您测试数据中的一个有效ID
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            ClassSessionMapper mapper = sqlSession.getMapper(ClassSessionMapper.class);
//            ClassSession session = mapper.selectById(sessionIdToTest);
//
//            assertNotNull("教学班不应为null", session);
//            assertEquals("教学班ID应匹配", sessionIdToTest, session.getSessionId());
//            System.out.println("查询成功: " + session.getTeacherName() + " - " + session.getScheduleInfo());
//        }
//    }
//
//    /**
//     * 测试根据学生ID查询其已选的所有教学班。
//     * 前提：数据库中，学生 "21320001" 至少选择了一门课。
//     */
//    @Test
//    public void testSelectSessionsByStudentId() throws Exception {
//        String studentIdToTest = "21320001"; // 假设这是有选课记录的学生ID
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            ClassSessionMapper mapper = sqlSession.getMapper(ClassSessionMapper.class);
//            List<ClassSession> sessions = mapper.selectSessionsByStudentId(studentIdToTest);
//
//            assertNotNull("教学班列表不应为null", sessions);
//            assertFalse("该生应有选课记录", sessions.isEmpty());
//            System.out.println("学生 " + studentIdToTest + " 选了 " + sessions.size() + " 个教学班。");
//        }
//    }
//
//    /**
//     * 测试增加和减少课程已选人数的功能。
//     * 前提：数据库中必须存在ID为 "ENG_S01" 的教学班。
//     */
//    @Test
//    public void testIncrementAndDecrementEnrolledCount() throws Exception {
//        String sessionIdToTest = "ENG_S01";
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            ClassSessionMapper mapper = sqlSession.getMapper(ClassSessionMapper.class);
//
//            // 1. 获取初始人数
//            ClassSession initialSession = mapper.selectById(sessionIdToTest);
//            assertNotNull(initialSession);
//            int initialCount = initialSession.getEnrolledCount();
//            System.out.println("初始人数: " + initialCount);
//
//            // 2. 增加人数并验证
//            int incrementResult = mapper.incrementEnrolledCount(sessionIdToTest);
//            sqlSession.commit();
//            assertEquals("应影响1行", 1, incrementResult);
//            ClassSession afterIncrement = mapper.selectById(sessionIdToTest);
//            assertEquals("人数应增加1", initialCount + 1, afterIncrement.getEnrolledCount());
//            System.out.println("增加后人数: " + afterIncrement.getEnrolledCount());
//
//            // 3. 减少人数并验证
//            int decrementResult = mapper.decrementEnrolledCount(sessionIdToTest);
//            sqlSession.commit();
//            assertEquals("应影响1行", 1, decrementResult);
//            ClassSession afterDecrement = mapper.selectById(sessionIdToTest);
//            assertEquals("人数应恢复初始值", initialCount, afterDecrement.getEnrolledCount());
//            System.out.println("减少后人数: " + afterDecrement.getEnrolledCount());
//        }
//    }
//}