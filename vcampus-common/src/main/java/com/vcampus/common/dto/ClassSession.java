package com.vcampus.common.dto;

import java.util.List;

public class ClassSession {
    private String sessionId;
    private String teacherName;
    //private String scheduleInfo;(其实就是把所有信息汇总即可）
    private int capacity;
    private int enrolledCount;
    private boolean isSelectedByStudent; // 学生是否已选此教学班

    // 默认构造方法（反序列化必需）
    public ClassSession() {}

    public ClassSession(String sessionId, String teacherName, String scheduleInfo, int capacity, int enrolledCount, boolean isSelectedByStudent) {
        this.sessionId = sessionId;
        this.teacherName = teacherName;
        //this.scheduleInfo = scheduleInfo;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
        this.isSelectedByStudent = isSelectedByStudent;
    }


    // --- 构造方法, Getters, Setters ---
    // (请确保为以上所有字段都提供了相应的get方法)
    // --- 构造方法, Getters, Setters ---
    public String getSessionId() { return sessionId; }
    public String getTeacherName() { return teacherName; }
    //public String getScheduleInfo() { return scheduleInfo; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }
    public boolean isSelectedByStudent() { return isSelectedByStudent; }

    //setter方法
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    //public void setScheduleInfo(String scheduleInfo) { this.scheduleInfo = scheduleInfo; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }
    public void setSelectedByStudent(boolean selectedByStudent) { isSelectedByStudent = selectedByStudent; }


}
