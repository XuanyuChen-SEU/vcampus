package com.vcampus.common.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class StudentLeaveApplication implements Serializable {

    private String applicationId;      // 申请编号（可由系统生成）
    private String userId;          // 学生user号
    private String studentName;        // 学生姓名
    private String currentStatus;      // 当前学籍状态（在读/休学/毕业）// 申请类型（休学申请/复学申请）
    private String reason;             // 申请原因// 申请提交日期
    private String status;
    private String type;
    private LocalDate createTime;

    public StudentLeaveApplication() {}

    public StudentLeaveApplication(String studentId, String studentName, String currentStatus,
                                   String applicationType, String reason, LocalDate applicationDate, String status) {
        this.userId = studentId;
        this.studentName = studentName;
        this.currentStatus = currentStatus;
        this.type = applicationType;
        this.reason = reason;
        this.createTime = applicationDate;
        this.status = status;
    }

    // ==== Getter & Setter ====
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getStudentId() { return userId; }
    public void setStudentId(String studentId) { this.userId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getCreateTime() { return createTime; }
    public void setCreateTime(LocalDate create_time) { this.createTime = create_time; }
}

