package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.dto.TimeSlot; // 确保这个DTO存在
import com.vcampus.database.mapper.ClassSessionMapper;
import com.vcampus.database.mapper.CourseMapper;
import com.vcampus.database.mapper.CourseSelectionMapper;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * CourseDao 接口的真实数据库实现。
 * 使用 MyBatis 与数据库进行交互。
 */
public class CourseDao/* implements ICourseDao */{
/*
    @Override
    public List<Course> getAllCourses() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            return mapper.selectAllCoursesWithSessions();
        }
    }

    @Override
    public List<CourseSelection> getSelectionsByStudentId(String studentId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            CourseSelectionMapper mapper = sqlSession.getMapper(CourseSelectionMapper.class);
            return mapper.selectByStudentId(studentId);
        }
    }

    @Override
    public boolean isSessionFull(String sessionId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ClassSessionMapper mapper = sqlSession.getMapper(ClassSessionMapper.class);
            ClassSession session = mapper.selectById(sessionId);
            return session != null && session.getEnrolledCount() >= session.getCapacity();
        }
    }

    @Override
    public boolean hasScheduleConflict(String studentId, String newSessionId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ClassSessionMapper mapper = sqlSession.getMapper(ClassSessionMapper.class);

            // 1. 获取新课程的时间信息
            ClassSession newSession = mapper.selectById(newSessionId);
            if (newSession == null || newSession.getScheduleInfo() == null) return false;
            TimeSlot newTimeSlot = parseScheduleInfo(newSession.getScheduleInfo());
            if (newTimeSlot == null) return false;

            // 2. 获取该生所有已选课程的时间信息
            List<ClassSession> enrolledSessions = mapper.selectSessionsByStudentId(studentId);
            List<TimeSlot> enrolledTimeSlots = enrolledSessions.stream()
                    .map(session -> parseScheduleInfo(session.getScheduleInfo()))
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            // 3. 检查冲突
            for (TimeSlot enrolledTimeSlot : enrolledTimeSlots) {
                if (newTimeSlot.conflictsWith(enrolledTimeSlot)) {
                    return true; // 发现冲突
                }
            }
            return false; // 无冲突
        }
    }

    // 一个简化的 scheduleInfo 解析器，您可能需要根据实际格式进行调整
    private TimeSlot parseScheduleInfo(String scheduleInfo) {
        // 假设格式为 "x-x周 周x x-x节"
        Pattern pattern = Pattern.compile("周(一|二|三|四|五|六|日)\\s*(\\d+)-(\\d+)节");
        Matcher matcher = pattern.matcher(scheduleInfo);
        if (matcher.find()) {
            String dayOfWeekStr = matcher.group(1);
            int startSection = Integer.parseInt(matcher.group(2));
            int endSection = Integer.parseInt(matcher.group(3));

            int dayOfWeek = 0;
            switch(dayOfWeekStr) {
                case "一": dayOfWeek = 1; break;
                case "二": dayOfWeek = 2; break;
                case "三": dayOfWeek = 3; break;
                case "四": dayOfWeek = 4; break;
                case "五": dayOfWeek = 5; break;
                case "六": dayOfWeek = 6; break;
                case "日": dayOfWeek = 7; break;
            }
            return new TimeSlot(dayOfWeek, startSection, endSection);
        }
        return null;
    }


    @Override
    public boolean addCourseSelection(CourseSelection selection) {
        SqlSession sqlSession = MyBatisUtil.openSession(); // 手动管理事务
        try {
            CourseSelectionMapper selectionMapper = sqlSession.getMapper(CourseSelectionMapper.class);
            ClassSessionMapper sessionMapper = sqlSession.getMapper(ClassSessionMapper.class);
            selectionMapper.insert(selection);
            sessionMapper.incrementEnrolledCount(selection.getSessionId());
            sqlSession.commit();
            return true;
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
            return false;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public boolean removeCourseSelection(String studentId, String sessionId) {
        SqlSession sqlSession = MyBatisUtil.openSession(); // 手动管理事务
        try {
            CourseSelectionMapper selectionMapper = sqlSession.getMapper(CourseSelectionMapper.class);
            ClassSessionMapper sessionMapper = sqlSession.getMapper(ClassSessionMapper.class);

            int affectedRows = selectionMapper.delete(studentId, sessionId);
            if (affectedRows == 0) {
                sqlSession.rollback(); // 如果没有删除任何记录，说明未选此课
                return false;
            }
            sessionMapper.decrementEnrolledCount(sessionId);
            sqlSession.commit();
            return true;
        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
            return false;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public boolean isAlreadyEnrolled(String studentId, String sessionId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            CourseSelectionMapper mapper = sqlSession.getMapper(CourseSelectionMapper.class);
            return mapper.selectByStudentAndSession(studentId, sessionId) != null;
        }
    }

   */
}