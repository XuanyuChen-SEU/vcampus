package com.vcampus.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 学籍变更申请 DTO
 * 用于学生提交修改学籍关键信息时，客户端与服务端的数据传输
 *
 * 编写人：周蔚钺
 *
 * 字段说明：
 *   - applicationId: 申请唯一ID（UUID生成）
 *   - userId: 提交申请的用户ID（7位数字，关联 User）
 *   - studentId: 学号（8位数字，关联 Student）
 *   - applicantName: 申请人姓名
 *   - fieldName: 申请修改的字段名（如“学院”“专业”）
 *   - changeType: 变更类型（如“学院变更”“专业变更”“学号修改”）
 *   - beforeValue: 修改前的值
 *   - afterValue: 修改后的值
 *   - status: 申请状态（pending/approved/rejected）
 *   - reviewerName: 审核人姓名（管理员填写）
 *   - remark: 审核意见
 *   - submitTime: 学生提交申请的时间
 *   - reviewTime: 管理员审核申请的时间
 */
public class StudentChangeApplication implements Serializable {

    private String applicationId;   // 唯一申请ID
    private String userId;          // 7位用户ID
    private String studentId;       // 8位学号
    private String applicantName;   // 申请人姓名
    private String fieldName;       // 修改字段名
    private String changeType;      // 变更类型
    private String beforeValue;     // 修改前的值
    private String afterValue;      // 修改后的值
    private String status;          // 状态：pending/approved/rejected
    private String reviewerName;    // 审核人姓名
    private String remark;          // 管理员审核意见
    private LocalDateTime submitTime; // 提交时间
    private LocalDateTime reviewTime; // 审核时间

    // 默认构造函数（反序列化需要）
    public StudentChangeApplication() {
        this.applicationId = UUID.randomUUID().toString();
        this.status = "pending";
        this.submitTime = LocalDateTime.now();
    }

    // 构造函数（学生提交申请时使用）
    public StudentChangeApplication(String userId, String studentId, String applicantName,
                                    String fieldName, String changeType,
                                    String beforeValue, String afterValue) {
        this();
        this.userId = userId;
        this.studentId = studentId;
        this.applicantName = applicantName;
        this.fieldName = fieldName;
        this.changeType = changeType;
        this.beforeValue = beforeValue;
        this.afterValue = afterValue;
    }

    // Getter & Setter
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

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

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getBeforeValue() {
        return beforeValue;
    }

    public void setBeforeValue(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    public String getAfterValue() {
        return afterValue;
    }

    public void setAfterValue(String afterValue) {
        this.afterValue = afterValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status.equals("pending") || status.equals("approved") || status.equals("rejected")) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("申请状态必须是 pending/approved/rejected 之一");
        }
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }
}
