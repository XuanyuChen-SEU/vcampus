package com.vcampus.common.dto;

import java.io.Serializable;
import java.util.List;

public class ClassSession implements Serializable {
    private String sessionId;
    private String teacherName;
    private String scheduleInfo;//时间表
    private int capacity;
    private int enrolledCount;
    private boolean isSelectedByStudent; // 学生是否已选此教学班

    // 默认构造方法（反序列化必需）
    public ClassSession() {}

    public ClassSession(String sessionId, String teacherName, String scheduleInfo, int capacity, int enrolledCount, boolean isSelectedByStudent) {
        this.sessionId = sessionId;
        this.teacherName = teacherName;
        this.scheduleInfo = scheduleInfo;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
        this.isSelectedByStudent = isSelectedByStudent;
    }


    //深度拷贝
    // 在 ClassSession.java (common DTO) 中添加
    public ClassSession(ClassSession other) {
        this.sessionId = other.sessionId;
        this.teacherName = other.teacherName;
        this.scheduleInfo = other.scheduleInfo; //拷贝一下时间
        this.capacity = other.capacity;
        this.enrolledCount = other.enrolledCount;
        this.isSelectedByStudent = other.isSelectedByStudent;
    }


    // --- 构造方法, Getters, Setters ---
    // (请确保为以上所有字段都提供了相应的get方法)
    // --- 构造方法, Getters, Setters ---
    public String getSessionId() { return sessionId; }
    public String getTeacherName() { return teacherName; }
    public String getScheduleInfo() { return scheduleInfo; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }
    public boolean isSelectedByStudent() { return isSelectedByStudent; }

    //setter方法
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public void setScheduleInfo(String scheduleInfo) { this.scheduleInfo = scheduleInfo; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }
    public void setSelectedByStudent(boolean selectedByStudent) { isSelectedByStudent = selectedByStudent; }


}
