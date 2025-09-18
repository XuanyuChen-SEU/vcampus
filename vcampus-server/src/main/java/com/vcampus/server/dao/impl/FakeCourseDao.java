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
import com.vcampus.common.dto.DropLogEntry;
import com.vcampus.server.data.DataSource; // ⭐ 1. 引入全局数据源

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Override
    public boolean addDropLogEntry(DropLogEntry entry) {
        System.out.println("FakeDAO: [State Changed] 正在写入退课日志...");
        DataSource.MOCK_DROP_LOG_TABLE.add(entry);
        return true;
    }

    @Override
    public List<DropLogEntry> getDropLogsByStudentId(String studentId) {
        System.out.println("FakeDAO: 正在获取学生" + studentId + "的退课日志...");
        return DataSource.MOCK_DROP_LOG_TABLE.stream()
                .filter(entry -> entry.getDroppedBy().equals(studentId))
                .collect(Collectors.toList());
    }

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

    // 根据教学班ID查找父课程信息
    @Override
    public Course findCourseBySessionId(String sessionId) {
        return DataSource.MOCK_COURSE_TABLE.values().stream()
                .filter(course -> course.getSessions().stream()
                        .anyMatch(s -> s.getSessionId().equals(sessionId)))
                .findFirst().orElse(null);
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
        if (sessionId == null) return Optional.empty();

        return DataSource.MOCK_COURSE_TABLE.values().stream()
                .filter(Objects::nonNull) // 过滤掉 null 的 Course 对象
                .filter(course -> course.getSessions() != null) // 过滤掉 sessions 列表为 null 的 Course
                .flatMap(course -> course.getSessions().stream())
                .filter(Objects::nonNull) // 过滤掉 null 的 ClassSession 对象
                .filter(session -> sessionId.equals(session.getSessionId()))
                .findFirst();
    }

    // --- ⭐ 新增：管理员功能实现 ---

    /**
     * ⭐ 新增：在内存中添加一个新的课程
     * @param course 要添加的课程 DTO
     * @return 如果成功添加，返回 true
     */
    @Override
    public boolean addCourse(Course course) {
        if (course == null || course.getCourseId() == null) {
            return false;
        }
        // computeIfAbsent: 如果Key不存在，则添加；如果已存在，则不操作并返回现有值
        // 这样可以保证课程ID的唯一性
        Course existing = DataSource.MOCK_COURSE_TABLE.putIfAbsent(course.getCourseId(), course);

        if(existing == null) {
            System.out.println("DAO: [State Changed] 新课程 " + course.getCourseId() + " 已被添加。");
            return true; // 如果之前没有值(返回null)，说明添加成功
        } else {
            System.err.println("DAO 错误：尝试添加已存在的课程ID " + course.getCourseId());
            return false; // 如果之前有值，说明ID冲突，添加失败
        }
    }

    /**
     * ⭐ 新增：在内存中更新一个课程的信息
     * @param course 包含了最新信息的课程 DTO
     * @return 如果成功更新，返回 true
     */
    @Override
    public boolean updateCourse(Course course) {
        if (course == null || course.getCourseId() == null) {
            return false;
        }

        // 检查课程ID是否存在
        if (DataSource.MOCK_COURSE_TABLE.containsKey(course.getCourseId())) {
            // 如果存在，用新的课程对象替换旧的
            DataSource.MOCK_COURSE_TABLE.put(course.getCourseId(), course);
            System.out.println("DAO: [State Changed] 课程 " + course.getCourseId() + " 已被更新。");
            return true;
        } else {
            System.err.println("DAO 错误：尝试更新不存在的课程ID " + course.getCourseId());
            return false; // 课程不存在，更新失败
        }
    }

    @Override
    public boolean deleteCourse(String courseId) {
        // 同时需要删除与该课程相关的所有教学班和选课记录
        Course removedCourse = DataSource.MOCK_COURSE_TABLE.remove(courseId);
        if (removedCourse != null) {
            List<String> sessionsToDelete = removedCourse.getSessions().stream()
                    .map(ClassSession::getSessionId).collect(Collectors.toList());
            DataSource.MOCK_SELECTION_TABLE.values()
                    .forEach(selectionList -> selectionList.removeAll(sessionsToDelete));
            return true;
        }
        return false;
    }

    @Override
    public boolean addSession(ClassSession session) {
        // 1. 检查 session 和它的关键ID是否为空
        if (session == null || session.getCourseId() == null || session.getSessionId() == null) {
            return false;
        }

        // 2. 根据 courseId 找到它应该属于的父课程
        Course parentCourse = DataSource.MOCK_COURSE_TABLE.get(session.getCourseId());
        if (parentCourse == null) {
            System.err.println("DAO错误：尝试为不存在的课程 " + session.getCourseId() + " 添加教学班");
            return false; // 找不到父课程，添加失败
        }

        // 3. 检查新的教学班ID是否在整个系统中已存在
        if (findSessionById(session.getSessionId()).isPresent()) {
            System.err.println("DAO错误：教学班ID " + session.getSessionId() + " 已存在，无法添加");
            return false;
        }

        // 4. 将新的教学班添加到父课程的 sessions 列表中
        parentCourse.getSessions().add(session);
        System.out.println("DAO: [State Changed] 新教学班 " + session.getSessionId() + " 已添加到课程 " + parentCourse.getCourseId());
        return true;
    }

    @Override
    public boolean updateSession(ClassSession updatedSession) {
        if(updatedSession == null || updatedSession.getSessionId() == null) return false;

        // 调用我们下面更健壮的 findSessionById 方法
        Optional<ClassSession> sessionOpt = findSessionById(updatedSession.getSessionId());

        if (sessionOpt.isPresent()) {
            ClassSession originalSession = sessionOpt.get();
            originalSession.setTeacherName(updatedSession.getTeacherName());
            originalSession.setScheduleInfo(updatedSession.getScheduleInfo());
            originalSession.setCapacity(updatedSession.getCapacity());
            System.out.println("FakeDAO: [State Changed] 教学班 " + originalSession.getSessionId() + " 的信息已更新。");
            return true;
        }
        return false;
    }

    /**
     * ⭐ 新增：在内存中删除一个教学班，并级联删除所有相关的学生选课记录
     * @param sessionId 要删除的教学班 ID
     * @return 如果成功删除，返回 true
     */
    @Override
    public boolean deleteSession(String sessionId) {
        boolean wasRemoved = false;
        // 1. 遍历所有课程，找到并移除对应的教学班
        for (Course course : DataSource.MOCK_COURSE_TABLE.values()) {
            // 使用 removeIf 方法，如果成功删除则返回 true
            wasRemoved = course.getSessions().removeIf(s -> s.getSessionId().equals(sessionId));
            if (wasRemoved) {
                System.out.println("DAO: [State Changed] 教学班 " + sessionId + " 已从课程 " + course.getCourseId() + " 中移除。");
                break; // 教学班ID是唯一的，找到后即可退出循环
            }
        }

        // 2. 如果成功从课程中移除了教学班，则执行级联删除
        if (wasRemoved) {
            // 遍历所有学生的选课记录
            System.out.println("DAO: [State Changed] 正在级联删除教学班 " + sessionId + " 的所有选课记录...");
            DataSource.MOCK_SELECTION_TABLE.values().forEach(
                    // 从每个学生的选课列表 (List<String>) 中，移除这个 sessionId
                    selectionList -> selectionList.remove(sessionId)
            );
        }

        return wasRemoved;
    }

//    @Override
//    public boolean updateSessionCapacity(String sessionId, int newCapacity) {
//        Optional<ClassSession> sessionOpt = findSessionById(sessionId);
//        if (sessionOpt.isPresent()) {
//            sessionOpt.get().setCapacity(newCapacity);
//            return true;
//        }
//        return false;
//    }

}