package com.vcampus.common.dto;

import com.vcampus.common.enums.CourseStatus;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程数据传输对象（DTO）
 * 用于客户端与服务端之间的课程信息传递
 */





public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseId;
    private String courseName;
    private String courseType; // e.g., "必修"
    private String department; // e.g., "外国语学院"
    //计算coursesession数量
    private int sessionnum;


    private CourseStatus status; // 选课状态
    private List<ClassSession> sessions;

    // 默认构造方法（反序列化必需）
    public Course() {}

    // 带参构造方法
    public Course(String courseId, String courseName, String courseType, String department, CourseStatus status, List<ClassSession> sessions) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseType = courseType;
        this.department = department;
        this.status = status;
        this.sessions = sessions;
        this.sessionnum = sessions.size() ;
    }

    //进行一项深拷贝
    // 在 Course.java (common DTO) 中添加
    public Course(Course other) {
        this.courseId = other.courseId;
        this.courseName = other.courseName;
        this.courseType = other.courseType;
        this.department = other.department;
        this.status = other.status;
        this.sessionnum = other.sessionnum;
        // 对 sessions 列表也进行深拷贝
        if (other.sessions != null) {
            this.sessions = other.sessions.stream().map(ClassSession::new).collect(Collectors.toList());
        }
    }







    // Getter & Setter方法
    // --- 构造方法, Getters, Setters ---
    // (请确保为以上所有字段都提供了相应的get方法)
    public String getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getCourseType() { return courseType; }
    public String getDepartment() { return department; }
    public CourseStatus getStatus() { return status; }
    public List<ClassSession> getSessions() { return sessions; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setCourseType(String courseType) { this.courseType = courseType; }
    public void setDepartment(String department) { this.department = department; }
    public void setStatus(CourseStatus status) { this.status = status; }
    public void setSessions(List<ClassSession> sessions) { this.sessions = sessions; }
    public int getSessionnum() { return sessionnum; }
    public void setSessionnum(int sessionnum) { this.sessionnum = sessions.size(); }


    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseType='" + courseType + '\'' +
                ", department='" + department + '\'' +
                ", status=" + status +
                ", sessions=" + sessions +
                '}';
    }


}