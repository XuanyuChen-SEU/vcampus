package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ICourseDao;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.CourseSelection;
import java.util.ArrayList;
import java.util.List;
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
        this.selectionTable.add(selection);
        this.courseTable.stream()
                .flatMap(c -> c.getSessions().stream())
                .filter(s -> s.getSessionId().equals(selection.getCourseId()))
                .findFirst()
                .ifPresent(s -> s.setEnrolledCount(s.getEnrolledCount() + 1));
        return true;
    }

    @Override
    public boolean removeCourseSelection(String studentId, String sessionId) {
        boolean removed = this.selectionTable.removeIf(sel -> sel.getStudentId().equals(studentId) && sel.getCourseId().equals(sessionId));
        if (removed) {
            this.courseTable.stream()
                    .flatMap(c -> c.getSessions().stream())
                    .filter(s -> s.getSessionId().equals(sessionId))
                    .findFirst()
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
}