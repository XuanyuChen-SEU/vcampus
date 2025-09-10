package com.vcampus.server.dao.impl;
import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.enums.CourseStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDao implements ICourseDao {

    // 模拟数据库中的课程表和选课表
    private static final List<Course> mockCourseTable = new ArrayList<>();
    private static final List<CourseSelection> mockSelectionTable = new ArrayList<>();

    // 静态代码块，用于初始化模拟数据
//    static {
//        // 初始化课程
//        List<ClassSession> sessions1 = List.of(
//                new ClassSession("S01", "[01] 宋安娜", "...", 31, 15, false),
//                new ClassSession("S02", "[02] 王老师", "...", 31, 31, false)
//        );
//        mockCourseTable.add(new Course("B17M0010", "大学英语II", "必修", "外国语学院", null, sessions1));
//
//        List<ClassSession> sessions2 = List.of(
//                new ClassSession("S04", "[01] 赵教授", "...", 40, 39, false)
//        );
//        mockCourseTable.add(new Course("B07M1010", "数学分析", "必修", "理学院", null, sessions2));
//
//        // 初始化选课记录
//        mockSelectionTable.add(new CourseSelection("student123", "S04", "已选"));
//    }


    @Override
    public List<Course> getAllCourses() {
        System.out.println("DAO: [DB] 查询所有课程...");
        return new ArrayList<>(mockCourseTable); // 返回副本以防外部修改
    }

    @Override
    public List<CourseSelection> getSelectionsByStudentId(String studentId) {
        System.out.println("DAO: [DB] 查询学生 " + studentId + " 的选课记录...");
        return mockSelectionTable.stream()
                .filter(sel -> sel.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSessionFull(String sessionId) {
        // 模拟逻辑
        return sessionId.equals("S02");
    }

    @Override
    public boolean hasScheduleConflict(String studentId, String newSessionId) {
        // TODO: 实现真实的课程冲突检测逻辑
        return false;
    }

    @Override
    public boolean addCourseSelection(CourseSelection selection) {
        System.out.println("DAO: [DB] 插入选课记录: " + selection);
        mockSelectionTable.add(selection);
        return true;
    }

    @Override
    public boolean removeCourseSelection(String studentId, String sessionId) {
        System.out.println("DAO: [DB] 删除学生 " + studentId + " 的选课记录 " + sessionId);
        return mockSelectionTable.removeIf(sel -> sel.getStudentId().equals(studentId) && sel.getCourseId().equals(sessionId));
    }



}
