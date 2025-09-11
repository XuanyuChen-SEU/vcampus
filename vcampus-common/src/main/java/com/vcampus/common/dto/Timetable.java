package com.vcampus.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 课表数据传输对象
 * 用于封装学生的完整课表信息
 */
public class Timetable implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentId;        // 学生ID
    private List<Course> courses;    // 已选课程列表
    //private int totalCredits;        // 总学分（先考虑无限选课情况，无限制选课）

    // 默认构造方法
    public Timetable() {}

    // 带参构造方法
    public Timetable(String studentId) {
        this.studentId = studentId;
    }

    // Getter & Setter
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }


    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
        // 自动计算总学分
        //calculateTotalCredits();
    }

    //public int getTotalCredits() { return totalCredits; }

    // 计算总学分
//    private void calculateTotalCredits() {
//        if (courses == null || courses.isEmpty()) {
//            this.totalCredits = 0;
//            return;
//        }
//        double creditsSum = 0;
//        for (Course course : courses) {
//            creditsSum += course.getCredits();
//        }
//        this.totalCredits = (int) Math.round(creditsSum);
//    }

    // 添加课程到课表
    public void addCourse(Course course) {
        if (courses != null) {
            courses.add(course);
            //calculateTotalCredits();
        }
    }

    // 从课表中移除课程
    public void removeCourse(String courseId) {
        if (courses != null) {
            courses.removeIf(course -> course.getCourseId().equals(courseId));
            //calculateTotalCredits();
        }
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "studentId='" + studentId + '\'' +
                ", courseCount=" + (courses != null ? courses.size() : 0) +
                //", totalCredits=" + totalCredits +
                '}';
    }
}
