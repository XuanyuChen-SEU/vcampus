package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 用于“我的课表”界面显示的数据传输对象 (DTO)。
 * 它是一个“扁平化”的数据结构，整合了课程和教学班的关键信息，方便在 TableView 中直接展示。
 */
public class MyCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseIdAndName; // 课程号/课程名
    private String teacherName;
    private String scheduleInfo;
    private String credits;//学分
    private String courseType;//课程性质
    private String category;//校公选类别
    private String campus;//校区
    private String conflictStatus;//是否冲突
    private String sessionId; // 隐藏字段，用于退课操作

    // --- Getters and Setters ---
    // (请为以上所有字段添加 public 的 getter 和 setter 方法)

    // 示例 Getter & Setter
    public String getCourseIdAndName() { return courseIdAndName; }
    public void setCourseIdAndName(String courseIdAndName) { this.courseIdAndName = courseIdAndName; }

    //获取老师名字
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    // ... 其他字段的 Getters and Setters ...

    //获取特色班ID
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    //获取时间段
    public String getScheduleInfo() { return scheduleInfo; }
    public void setScheduleInfo(String scheduleInfo) { this.scheduleInfo = scheduleInfo; }

    //获取学分
    public String getCredits() { return credits; }
    public void setCredits(String credits) { this.credits = credits; }

    //获取校公选类型
    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    //获取课程类型
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    //获取校区
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }

    //获取冲突状态
    public String getConflictStatus() { return conflictStatus; }
    //设置冲突状态
    public void setConflictStatus(String conflictStatus) { this.conflictStatus = conflictStatus; }
}
