//package com.vcampus.database;
//
//import com.vcampus.common.dto.CourseSelection;
//import com.vcampus.database.mapper.CourseSelectionMapper;
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
//public class CourseSelectionMapperTest {
//
//    /**
//     * 测试根据学生ID查询选课记录。
//     * 前提：数据库中，学生 "21320002" 必须有选课记录。
//     */
//    @Test
//    public void testSelectByStudentId() throws Exception {
//        String studentIdToTest = "21320002";
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            CourseSelectionMapper mapper = sqlSession.getMapper(CourseSelectionMapper.class);
//            List<CourseSelection> selections = mapper.selectByStudentId(studentIdToTest);
//
//            assertNotNull(selections);
//            assertFalse(selections.isEmpty());
//            System.out.println("学生 " + studentIdToTest + " 的选课记录数: " + selections.size());
//        }
//    }
//
//    /**
//     * 测试插入和删除选课记录的原子操作。
//     * 前提：学生 "21320001" 未选择 "SE_S01" 教学班。
//     */
//    @Test
//    public void testInsertAndDelete() throws Exception {
//        String studentId = "21320001";
//        String sessionId = "SE_S01";
//        CourseSelection newSelection = new CourseSelection(studentId, sessionId, "已选");
//
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            CourseSelectionMapper mapper = sqlSession.getMapper(CourseSelectionMapper.class);
//
//            // 1. 确认初始状态为未选
//            CourseSelection beforeInsert = mapper.selectByStudentAndSession(studentId, sessionId);
//            assertNull("测试开始前，该生不应已选择此课", beforeInsert);
//
//            // 2. 插入选课记录
//            int insertResult = mapper.insert(newSelection);
//            sqlSession.commit();
//            assertEquals("应插入1行", 1, insertResult);
//            System.out.println("成功为学生 " + studentId + " 选上课程 " + sessionId);
//
//            // 3. 验证插入成功
//            CourseSelection afterInsert = mapper.selectByStudentAndSession(studentId, sessionId);
//            assertNotNull("插入后应能查到记录", afterInsert);
//            assertEquals("状态应为 '已选'", "已选", afterInsert.getStatus());
//
//            // 4. 删除选课记录
//            int deleteResult = mapper.delete(studentId, sessionId);
//            sqlSession.commit();
//            assertEquals("应删除1行", 1, deleteResult);
//            System.out.println("成功为学生 " + studentId + " 退选课程 " + sessionId);
//
//            // 5. 验证删除成功
//            CourseSelection afterDelete = mapper.selectByStudentAndSession(studentId, sessionId);
//            assertNull("删除后应查不到记录", afterDelete);
//        }
//    }
//}