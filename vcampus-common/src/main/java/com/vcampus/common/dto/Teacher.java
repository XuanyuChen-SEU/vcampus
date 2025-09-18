package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 教师数据传输对象
 */
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;       // 工号
    private String name;            // 姓名
    private String gender;
    private String college;// 性别
    private String department;      // 所属院系
    private String title;           // 职称（讲师、副教授、教授）
    private String phone;           // 联系电话
    private String email;           // 邮箱
    private String office;          // 办公室地址

    // === 构造函数 ===
    public Teacher() {}

    public Teacher(String teacherId, String name,String college, String gender, String department,
                   String title, String phone, String email, String office) {
        this.userId = teacherId;
        this.name = name;
        this.gender = gender;
        this.college= college;
        this.department = department;
        this.title = title;
        this.phone = phone;
        this.email = email;
        this.office = office;
    }

    // === Getter & Setter ===
    public String getUserId() { return userId; }
    public void setUserId(String teacherId) { this.userId = teacherId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = office; }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", college='" + college + '\'' +
                ", department='" + department + '\'' +
                ", title='" + title + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", office='" + office + '\'' +
                '}';
    }
}

