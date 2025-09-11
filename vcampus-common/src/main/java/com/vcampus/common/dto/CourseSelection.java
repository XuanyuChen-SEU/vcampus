package com.vcampus.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 选课记录数据传输对象（DTO）
 * 用于客户端与服务端之间的选课信息传递
 */
public class CourseSelection implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;      // 学生ID
    private String courseId;       // 课程ID
    //private Date enrollTime;       // 选课时间(目前先不考虑时间⌚️）
    private String status;         // 状态（选修中、退选中等）

    // 默认构造方法
    public CourseSelection() {}

    // 带参构造方法
    public CourseSelection(String studentId, String courseId, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        //this.enrollTime = new Date();
        this.status = status;
    }

    // Getter & Setter
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

//    public Date getEnrollTime() { return enrollTime; }
//    public void setEnrollTime(Date enrollTime) { this.enrollTime = enrollTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    @Override
    public String toString() {
        return "CourseSelection{" +
                "studentId='" + studentId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
