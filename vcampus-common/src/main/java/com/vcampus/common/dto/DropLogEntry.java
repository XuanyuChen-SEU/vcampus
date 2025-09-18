package com.vcampus.common.dto;

import java.io.Serializable;

public class DropLogEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    // 为了方便 TableView 绑定，我们使用 JavaFX 的 Property 类型
    // 但为了保持 DTO 的通用性，我们也可以先用普通类型，再在 Controller 中转换
    // 这里我们先用普通类型，更简单
    private String courseIdAndName;
    private String teacherName;
    private String courseType; // 课程性质
    private double credits;
    //private String dropTime; // 用 String 类型来显示格式化后的时间
    private String droppedBy;
    private String dropType;
    private String priority;

    // --- Getters and Setters for all fields ---
    // (请为以上所有字段添加 public 的 getter 和 setter 方法)

    // 示例 Getter & Setter
    public String getCourseIdAndName() { return courseIdAndName; }
    public void setCourseIdAndName(String courseIdAndName) { this.courseIdAndName = courseIdAndName; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public double getCredits() { return credits; }
    public void setCredits(double credits) { this.credits = credits; }

//    public String getTime() { return time; }
//    public void setTime(String time) { this.time = time; }

    public String getDroppedBy() { return droppedBy; }
    public void setDroppedBy(String droppedBy) { this.droppedBy = droppedBy; }

    public String getDropType() { return dropType; }
    public void setDropType(String dropType) { this.dropType = dropType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}