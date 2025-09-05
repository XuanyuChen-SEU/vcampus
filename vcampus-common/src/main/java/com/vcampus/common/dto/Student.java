package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 学生学籍信息 DTO
 * 与 User 账号体系通过 userId 关联
 * 编写人：周蔚钺
 *
 * 字段说明：
 *   - userId: 与 User 对象关联的 7 位用户ID
 *   - studentId: 学号，8 位数字
 *   - cardId: 一卡通号，9 位数字
 *   - name: 姓名
 *   - gender: 性别
 *   - college: 学院
 *   - major: 专业
 *   - grade: 年级（如 2023）
 */
public class Student implements Serializable {

    private String userId;     // 7位用户ID（关联 User.userId）
    private String studentId;  // 学号（8位数字）
    private String cardId;     // 一卡通号（9位数字）

    private String name;       // 姓名
    private String gender;     // 性别
    private String college;    // 学院
    private String major;      // 专业
    private int grade;         // 年级（如 2023）

    // 默认构造方法（反序列化必需）
    public Student() {}

    public Student(String userId, String studentId, String cardId,
                   String name, String gender, String college,
                   String major, int grade) {
        setUserId(userId);
        setStudentId(studentId);
        setCardId(cardId);
        this.name = name;
        this.gender = gender;
        this.college = college;
        this.major = major;
        this.grade = grade;
    }

    // Getter & Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId != null && userId.matches("\\d{7}")) {
            this.userId = userId;
        } else {
            throw new IllegalArgumentException("用户ID必须为7位纯数字字符串");
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId != null && studentId.matches("\\d{8}")) {
            this.studentId = studentId;
        } else {
            throw new IllegalArgumentException("学号必须为8位纯数字字符串");
        }
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        if (cardId != null && cardId.matches("\\d{9}")) {
            this.cardId = cardId;
        } else {
            throw new IllegalArgumentException("一卡通号必须为9位纯数字字符串");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
