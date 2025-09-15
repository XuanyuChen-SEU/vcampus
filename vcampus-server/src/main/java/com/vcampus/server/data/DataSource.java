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
        System.out.println("DATASOURCE: 正在初始化【信息增强版】模拟数据...");

        // ⭐ 1. 为课程添加完整信息
        List<ClassSession> englishSessions = List.of(
                new ClassSession("ENG_S01", "陈玉玲,任丹丽", "1-11周 星期四 11-13节 教七-203", 20, 1, false)
        );
        Course englishCourse = new Course("B00RW025[01]", "大学英语II", "必修", "外国语学院", NOT_SELECTED, englishSessions,2.0,"","九龙湖");
//        englishCourse.setCredits(2.0);
//        englishCourse.setCategory("人文社科与智慧（原文艺社科类）");
//        englishCourse.setCampus("九龙湖");
        MOCK_COURSE_TABLE.put("B00RW025[01]", englishCourse);

        List<ClassSession> seSessions = List.of(
                new ClassSession("SE_S01", "任国森", "1-8周 星期二 8-9节 教三-204", 20, 1, false)
        );
        Course seCourse = new Course("BJSL0120[04]", "计算机组成原理", "必修", "软件学院", NOT_SELECTED, seSessions,4.0,"","九龙湖");
//        seCourse.setCredits(1.0);
//        seCourse.setCategory("通识教育核心课程");
//        seCourse.setCampus("九龙湖");
        MOCK_COURSE_TABLE.put("BJSL0120[04]", seCourse);

        // ⭐ 2. 为学生 '1234567' 创建初始选课记录
        MOCK_SELECTION_TABLE.put("1234567", new ArrayList<>(List.of("ENG_S01")));

        System.out.println("DATASOURCE: 模拟数据初始化完成。");
    }
}
