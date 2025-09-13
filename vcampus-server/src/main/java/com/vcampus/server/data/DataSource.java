package com.vcampus.server.data; // 推荐为数据源创建一个新的 data 包

import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.common.enums.CourseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static com.vcampus.common.enums.CourseStatus.NOT_SELECTED;

/**
 * 模拟数据源 (内存数据库)
 * 使用静态 final 变量来存储所有模拟数据表，确保在服务器生命周期内全局唯一且状态持久。
 */
public class DataSource {

    // 使用 ConcurrentHashMap 保证多线程下的安全
    public static final Map<String, Course> MOCK_COURSE_TABLE = new ConcurrentHashMap<>();
    public static final Map<String, List<String>> MOCK_SELECTION_TABLE = new ConcurrentHashMap<>();

    // 使用静态代码块，在类被加载时仅执行一次，初始化所有数据
    static {
        System.out.println("DATASOURCE: 正在初始化全局静态模拟数据...");

        // 1. 创建课程数据
        List<ClassSession> englishSessions = List.of(
                new ClassSession("ENG_S01", "[01] 张老师", "1-16周 周二 3-4节", 60, 1, false)
        );
        MOCK_COURSE_TABLE.put("B17M0010", new Course("B17M0010", "大学英语II", "必修", "外国语学院", NOT_SELECTED, englishSessions));

        List<ClassSession> seSessions = List.of(
                new ClassSession("SE_S01", "[01] 刘老师", "1-16周 周一 5-6节", 50, 20, false)
        );
        MOCK_COURSE_TABLE.put("B08M4000", new Course("B08M4000", "软件工程", "限选", "计算机学院", NOT_SELECTED, seSessions));

        // 2. 为学生 '1234567' 创建初始选课记录
        MOCK_SELECTION_TABLE.put("1234567", new ArrayList<>(List.of("ENG_S01")));

        System.out.println("DATASOURCE: 模拟数据初始化完成。");
    }
}
