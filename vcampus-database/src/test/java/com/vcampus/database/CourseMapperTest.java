//package com.vcampus.database;
//
//import com.vcampus.common.dto.Course;
//import com.vcampus.database.mapper.CourseMapper;
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
//public class CourseMapperTest {
//    private SqlSessionFactory sqlSessionFactory;
//    private SqlSession sqlSession;
//    private CourseMapper courseMapper;
//    /**
//     * 测试查询所有课程及其包含的教学班。
//     * 前提：数据库中必须已存在课程和教学班数据。
//     */
//    @Test
//    public void testSelectAllCoursesWithSessions() throws Exception {
//        // 1. 加载MyBatis配置文件，获取SqlSessionFactory
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
//        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
//
//        // 2. 获取SqlSession和Mapper接口
//        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
//            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
//
//            // 3. 调用方法进行测试
//            List<Course> courses = courseMapper.selectAllCoursesWithSessions();
//
//            // 4. 验证和输出结果
//            assertNotNull("课程列表不应为null", courses);
//            assertFalse("课程列表不应为空", courses.isEmpty());
//
//            System.out.println("查询到的课程总数: " + courses.size());
//            for (Course course : courses) {
//                System.out.println("课程: " + course.getCourseName() + " (" + course.getCourseId() + ")");
//                assertNotNull(course.getSessions()); // 验证内嵌的教学班列表不为null
//                System.out.println("  - 包含教学班数量: " + course.getSessions().size());
//            }
//        }
//    }
//}