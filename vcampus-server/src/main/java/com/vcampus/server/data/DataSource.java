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
    public static final Map<String, Course> MOCK_COURSE_TABLE = new ConcurrentHashMap<>();
    public static final Map<String, List<String>> MOCK_SELECTION_TABLE = new ConcurrentHashMap<>();

    static {
        System.out.println("DATASOURCE: 正在初始化全局静态模拟数据...");

        // ⭐ 核心修正1：使用 new ArrayList<>() 来创建【可修改】的列表
        List<ClassSession> englishSessions = new ArrayList<>(List.of(
                // 调用我们新添加的、包含 courseId 的构造方法
                new ClassSession("B17M0010", "ENG_S01", "[01] 张老师", "1-16周 周二 3-4节", 60, 1, false)
        ));
        // 调用新添加的“全参”构造方法
        Course englishCourse = new Course("B17M0010", "大学英语II", "必修", "外国语学院", CourseStatus.NOT_SELECTED, englishSessions, 2.0, "通识教育必修", "九龙湖");
        MOCK_COURSE_TABLE.put("B17M0010", englishCourse);

        // ⭐ 同样，为其他课程也创建可修改的列表
        List<ClassSession> seSessions = new ArrayList<>(List.of(
                new ClassSession("B08M4000", "SE_S01", "[01] 刘老师", "1-16周 周一 5-6节", 50, 20, false)
        ));
        Course seCourse = new Course("B08M4000", "软件工程", "限选", "计算机学院", CourseStatus.NOT_SELECTED, seSessions, 3.0, "专业核心课程", "九龙湖");
        MOCK_COURSE_TABLE.put("B08M4000", seCourse);

        List<ClassSession> networkSessions = new ArrayList<>(List.of(
                new ClassSession("B08M3000", "CS_S01", "[01] 王教授", "1-8周 周一 1-4节", 50, 50, false)
        ));
        Course networkCourse = new Course("B08M3000", "计算机网络", "必修", "计算机学院", CourseStatus.NOT_SELECTED, networkSessions, 3.0, "专业核心课程", "九龙湖");
        MOCK_COURSE_TABLE.put("B08M3000", networkCourse);

        // ⭐ 核心修正2：恢复学生的初始选课记录
        MOCK_SELECTION_TABLE.put("1234567", new ArrayList<>(List.of("ENG_S01")));

        System.out.println("DATASOURCE: 模拟数据初始化完成。");
    }
}
