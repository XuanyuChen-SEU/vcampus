package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FakeCourseDao implements ICourseDao {
    private List<Course> courseTable = new ArrayList<>();
    private List<CourseSelection> selectionTable = new ArrayList<>();

    public FakeCourseDao() {}

    public void setCourseTable(List<Course> courseTable) { this.courseTable = courseTable; }
    public void setSelectionTable(List<CourseSelection> selectionTable) { this.selectionTable = selectionTable; }

    @Override
    public List<Course> getAllCourses() { return new ArrayList<>(this.courseTable); }

    @Override
    public List<CourseSelection> getSelectionsByStudentId(String studentId) {
        return this.selectionTable.stream()
                .filter(sel -> sel.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addCourseSelection(CourseSelection selection) {
//        this.selectionTable.add(selection);
//        this.courseTable.stream()
//                .flatMap(c -> c.getSessions().stream())
//                .filter(s -> s.getSessionId().equals(selection.getCourseId()))
//                .findFirst()
//                .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() + 1));
//        return true;
        // 防止重复添加
        if (isAlreadyEnrolled(selection.getStudentId(), selection.getSessionId())) {
            return false;
        }
        this.selectionTable.add(selection);
        // 找到对应的教学班，并将已选人数 +1
        findSessionById(selection.getSessionId())
                .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() + 1));
        return true;
    }

    @Override
    public boolean removeCourseSelection(String studentId, String sessionId) {
//        boolean removed = this.selectionTable.removeIf(sel -> sel.getStudentId().equals(studentId) && sel.getCourseId().equals(sessionId));
//        if (removed) {
//            this.courseTable.stream()
//                    .flatMap(c -> c.getSessions().stream())
//                    .filter(s -> s.getSessionId().equals(sessionId))
//                    .findFirst()
//                    .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() - 1));
//        }
//        return removed;
        boolean removed = this.selectionTable.removeIf(sel -> sel.getStudentId().equals(studentId) && sel.getSessionId().equals(sessionId));
        if (removed) {
            // 如果成功移除，找到对应的教学班，并将已选人数 -1
            findSessionById(sessionId)
                    .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() - 1));
        }
        return removed;
    }

    @Override
    public boolean isSessionFull(String sessionId) {
        return this.courseTable.stream()
                .flatMap(c -> c.getSessions().stream())
                .filter(s -> s.getSessionId().equals(sessionId))
                .anyMatch(s -> s.getEnrolledCount() >= s.getCapacity());
    }

    @Override
    public boolean hasScheduleConflict(String studentId, String newSessionId) { return false; }

    @Override
    public boolean isAlreadyEnrolled(String studentId, String sessionId) {
        return this.selectionTable.stream()
                .anyMatch(sel -> sel.getStudentId().equals(studentId) && sel.getSessionId().equals(sessionId));
    }

    // 私有辅助方法，用于在课程表中查找教学班
    private Optional<ClassSession> findSessionById(String sessionId) {
        return this.courseTable.stream()
                .flatMap(c -> c.getSessions().stream())
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();
    }
}