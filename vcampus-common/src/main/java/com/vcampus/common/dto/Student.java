package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 学生学籍信息 DTO
 * 与 User 账号体系通过 userId 关联
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    // 基本信息
    private String userId;
    private String studentId;
    private String cardId;
    private String name;
    private String gender;
    private String college;
    private String major;
    private int grade;
    private String birth_date;
    private String native_place;
    private String politics_status;
    private String student_status;

    // 新增联系方式
    private String phone;           // 手机号
    private String email;           // 电子邮箱
    private String dormAddress;     // 宿舍地址

    // 父亲信息
    private String fatherName;
    private String fatherPhone;
    private String fatherPoliticsStatus;
    private String fatherWorkUnit;

    // 母亲信息
    private String motherName;
    private String motherPhone;
    private String motherPoliticsStatus;
    private String motherWorkUnit;

    // 默认构造
    public Student() {}

    public Student(String userId, String studentId, String cardId, String name, String gender,
                   String college, String major, int grade, String birth_date,
                   String native_place, String politics_status, String student_status,
                   String phone, String email, String dormAddress,
                   String fatherName, String fatherPhone, String fatherPoliticsStatus, String fatherWorkUnit,
                   String motherName, String motherPhone, String motherPoliticsStatus, String motherWorkUnit) {
        this.userId = userId;
        this.studentId = studentId;
        this.cardId = cardId;
        this.name = name;
        this.gender = gender;
        this.college = college;
        this.major = major;
        this.grade = grade;
        this.birth_date = birth_date;
        this.native_place = native_place;
        this.politics_status = politics_status;
        this.student_status = student_status;
        this.phone = phone;
        this.email = email;
        this.dormAddress = dormAddress;
        this.fatherName = fatherName;
        this.fatherPhone = fatherPhone;
        this.fatherPoliticsStatus = fatherPoliticsStatus;
        this.fatherWorkUnit = fatherWorkUnit;
        this.motherName = motherName;
        this.motherPhone = motherPhone;
        this.motherPoliticsStatus = motherPoliticsStatus;
        this.motherWorkUnit = motherWorkUnit;
    }

    // Getter & Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) {
        if (userId != null && userId.matches("\\d{7}")) this.userId = userId;
        else throw new IllegalArgumentException("用户ID必须为7位纯数字字符串");
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) {
        if (studentId != null && studentId.matches("\\d{8}")) this.studentId = studentId;
        else throw new IllegalArgumentException("学号必须为8位纯数字字符串");
    }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) {
        if (cardId != null && cardId.matches("\\d{9}")) this.cardId = cardId;
        else throw new IllegalArgumentException("一卡通号必须为9位纯数字字符串");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getGrade() { return grade; }
    public void setGrade(int grade) { this.grade = grade; }

    public String getBirth_date() { return birth_date; }
    public void setBirth_date(String birth_date) { this.birth_date = birth_date; }

    public String getNative_place() { return native_place; }
    public void setNative_place(String native_place) { this.native_place = native_place; }

    public String getPolitics_status() { return politics_status; }
    public void setPolitics_status(String politics_status) { this.politics_status = politics_status; }

    public String getStudent_status() { return student_status; }
    public void setStudent_status(String student_status) { this.student_status = student_status; }

    // 联系方式
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDormAddress() { return dormAddress; }
    public void setDormAddress(String dormAddress) { this.dormAddress = dormAddress; }

    // 父亲信息
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getFatherPhone() { return fatherPhone; }
    public void setFatherPhone(String fatherPhone) { this.fatherPhone = fatherPhone; }

    public String getFatherPoliticsStatus() { return fatherPoliticsStatus; }
    public void setFatherPoliticsStatus(String fatherPoliticsStatus) { this.fatherPoliticsStatus = fatherPoliticsStatus; }

    public String getFatherWorkUnit() { return fatherWorkUnit; }
    public void setFatherWorkUnit(String fatherWorkUnit) { this.fatherWorkUnit = fatherWorkUnit; }

    // 母亲信息
    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getMotherPhone() { return motherPhone; }
    public void setMotherPhone(String motherPhone) { this.motherPhone = motherPhone; }

    public String getMotherPoliticsStatus() { return motherPoliticsStatus; }
    public void setMotherPoliticsStatus(String motherPoliticsStatus) { this.motherPoliticsStatus = motherPoliticsStatus; }

    public String getMotherWorkUnit() { return motherWorkUnit; }
    public void setMotherWorkUnit(String motherWorkUnit) { this.motherWorkUnit = motherWorkUnit; }
}

