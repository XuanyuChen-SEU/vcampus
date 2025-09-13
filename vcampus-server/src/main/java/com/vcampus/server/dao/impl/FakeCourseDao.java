//package com.vcampus.server.dao.impl;
//
//import com.vcampus.common.dao.ICourseDao;
//import com.vcampus.common.dto.ClassSession;
//import com.vcampus.common.dto.Course;
//import com.vcampus.common.dto.CourseSelection;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class FakeCourseDao implements ICourseDao {
//    private List<Course> courseTable = new ArrayList<>();
//    private List<CourseSelection> selectionTable = new ArrayList<>();
//
//    public FakeCourseDao() {}
//
//    public void setCourseTable(List<Course> courseTable) { this.courseTable = courseTable; }
//    public void setSelectionTable(List<CourseSelection> selectionTable) { this.selectionTable = selectionTable; }
//
//    @Override
//    public List<Course> getAllCourses() { return new ArrayList<>(this.courseTable); }
//
//    @Override
//    public List<CourseSelection> getSelectionsByStudentId(String studentId) {
//        return this.selectionTable.stream()
//                .filter(sel -> sel.getStudentId().equals(studentId))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean addCourseSelection(CourseSelection selection) {
////        this.selectionTable.add(selection);
////        this.courseTable.stream()
////                .flatMap(c -> c.getSessions().stream())
////                .filter(s -> s.getSessionId().equals(selection.getCourseId()))
////                .findFirst()
////                .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() + 1));
////        return true;
//        // 防止重复添加
//        if (isAlreadyEnrolled(selection.getStudentId(), selection.getSessionId())) {
//            return false;
//        }
//        this.selectionTable.add(selection);
//        // 找到对应的教学班，并将已选人数 +1
//        findSessionById(selection.getSessionId())
//                .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() + 1));
//        return true;
//    }
//
//    @Override
//    public boolean removeCourseSelection(String studentId, String sessionId) {
////        boolean removed = this.selectionTable.removeIf(sel -> sel.getStudentId().equals(studentId) && sel.getCourseId().equals(sessionId));
////        if (removed) {
////            this.courseTable.stream()
////                    .flatMap(c -> c.getSessions().stream())
////                    .filter(s -> s.getSessionId().equals(sessionId))
////                    .findFirst()
////                    .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() - 1));
////        }
////        return removed;
//        boolean removed = this.selectionTable.removeIf(sel -> sel.getStudentId().equals(studentId) && sel.getSessionId().equals(sessionId));
//        if (removed) {
//            // 如果成功移除，找到对应的教学班，并将已选人数 -1
//            findSessionById(sessionId)
//                    .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() - 1));
//        }
//        return removed;
//    }
//
//    @Override
//    public boolean isSessionFull(String sessionId) {
//        return this.courseTable.stream()
//                .flatMap(c -> c.getSessions().stream())
//                .filter(s -> s.getSessionId().equals(sessionId))
//                .anyMatch(s -> s.getEnrolledCount() >= s.getCapacity());
//    }
//
//    @Override
//    public boolean hasScheduleConflict(String studentId, String newSessionId) { return false; }
//
//    @Override
//    public boolean isAlreadyEnrolled(String studentId, String sessionId) {
//        return this.selectionTable.stream()
//                .anyMatch(sel -> sel.getStudentId().equals(studentId) && sel.getSessionId().equals(sessionId));
//    }
//
//    // 私有辅助方法，用于在课程表中查找教学班
//    private Optional<ClassSession> findSessionById(String sessionId) {
//        return this.courseTable.stream()
//                .flatMap(c -> c.getSessions().stream())
//                .filter(s -> s.getSessionId().equals(sessionId))
//                .findFirst();
//    }
//}


package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import com.vcampus.server.data.DataSource; // ⭐ 1. 引入全局数据源

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 一个模拟的 CourseDao 实现，完全基于全局唯一的 DataSource。
 *
 * 它本身不持有任何状态或数据，所有操作都直接代理到 DataSource 的静态数据表上，
 * 确保了在服务器生命周期内数据状态的一致性和持久性（在内存中）。
 */
public class FakeCourseDao implements ICourseDao {

    /**
     * 构造函数是空的。
     */
    public FakeCourseDao() {}


    /**
     * 从 DataSource 中获取所有课程的深拷贝。
     * @return 课程列表
     */
    @Override
    public List<Course> getAllCourses() {
        // 返回数据源中课程表的深拷贝，防止业务逻辑意外修改 DataSource 中的原始数据
        return DataSource.MOCK_COURSE_TABLE.values().stream()
                .map(Course::new) // 使用 Course 的拷贝构造函数
                .collect(Collectors.toList());
    }

    /**
     * 根据学生ID，从 DataSource 中构建其所有选课记录。
     * @param studentId 学生ID
     * @return 该学生的选课记录列表
     */
    @Override
    public List<CourseSelection> getSelectionsByStudentId(String studentId) {
        List<String> sessionIds = DataSource.MOCK_SELECTION_TABLE.get(studentId);
        if (sessionIds == null) {
            return new ArrayList<>(); // 如果该生没有任何选课记录，返回一个空列表
        }
        // 根据 sessionId 列表，构建出 CourseSelection 对象列表
        return sessionIds.stream()
                .map(sessionId -> new CourseSelection(studentId, sessionId, "已选"))
                .collect(Collectors.toList());
    }

    /**
     * 向 DataSource 中添加一条新的选课记录，并更新对应教学班的已选人数。
     * @param selection 新的选课记录
     * @return 如果添加成功，返回 true
     */
    @Override
    public boolean addCourseSelection(CourseSelection selection) {
        // computeIfAbsent: 如果学生ID不存在，则为他创建一个新的空列表；否则返回现有列表
        List<String> selections = DataSource.MOCK_SELECTION_TABLE
                .computeIfAbsent(selection.getStudentId(), k -> new ArrayList<>());

        if (!selections.contains(selection.getSessionId())) {
            selections.add(selection.getSessionId());
            // 找到对应的教学班，并将已选人数 +1
            findSessionById(selection.getSessionId())
                    .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() + 1));
            return true;
        }
        return false; // 已选过，添加失败
    }

    /**
     * 从 DataSource 中移除一条选课记录，并更新对应教学班的已选人数。
     * @param studentId 学生ID
     * @param sessionId 教学班ID
     * @return 如果移除成功，返回 true
     */
    @Override
    public boolean removeCourseSelection(String studentId, String sessionId) {
        List<String> selections = DataSource.MOCK_SELECTION_TABLE.get(studentId);
        if (selections != null && selections.remove(sessionId)) {
            // 如果成功移除，找到对应的教学班，并将已选人数 -1
            findSessionById(sessionId)
                    .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() - 1));
            return true;
        }
        return false; // 移除失败（可能因为本来就没选）
    }

    /**
     * ⭐ [已补全] 检查指定教学班是否已满。
     * @param sessionId 教学班ID
     * @return 如果已满或找不到该班级，返回 true
     */
    @Override
    public boolean isSessionFull(String sessionId) {
        return findSessionById(sessionId)
                .map(session -> session.getEnrolledCount() >= session.getCapacity())
                .orElse(true); // 安全起见，如果找不到班级，也视为“满员”不可选
    }

    /**
     * ⭐ [已补全] 检查学生在选择新课程时是否有时间冲突（模拟实现）。
     * @param studentId 学生ID
     * @param newSessionId 新选择的教学班ID
     * @return 总是返回 false
     */
    @Override
    public boolean hasScheduleConflict(String studentId, String newSessionId) {
        System.out.println("DAO: [Mock] 正在检查时间冲突 (当前模拟为总是不冲突)...");
        return false;
    }

    /**
     * ⭐ [已补全] 检查学生是否已经选了某个教学班。
     * @param studentId 学生ID
     * @param sessionId 教学班ID
     * @return 如果已选，返回 true
     */
    @Override
    public boolean isAlreadyEnrolled(String studentId, String sessionId) {
        List<String> selections = DataSource.MOCK_SELECTION_TABLE.get(studentId);
        if (selections == null) {
            return false; // 该生没有任何选课记录
        }
        return selections.contains(sessionId);
    }

    /**
     * 私有辅助方法，用于在 DataSource 的课程表中通过 sessionId 查找教学班。
     * @param sessionId 教学班ID
     * @return 一个包含 ClassSession 的 Optional 对象
     */
    private Optional<ClassSession> findSessionById(String sessionId) {
        return DataSource.MOCK_COURSE_TABLE.values().stream()
                .flatMap(c -> c.getSessions().stream())
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();
    }
}