package com.vcampus.common.dto;

import java.io.Serializable;

/**
 * 课程时间段数据传输对象
 * 用于表示课程的具体上课时间
 */
public class TimeSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dayOfWeek;     // 星期几（1-7，对应周一到周日）
    private int startSection;  // 开始节次
    private int endSection;    // 结束节次
    private String startTime;  // 开始时间（如 "08:00"）
    private String endTime;    // 结束时间（如 "09:40"）

    // 构造方法、Getter和Setter
    public TimeSlot() {}

    public TimeSlot(int dayOfWeek, int startSection, int endSection) {
        this.dayOfWeek = dayOfWeek;
        this.startSection = startSection;
        this.endSection = endSection;
    }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getStartSection() { return startSection; }
    public void setStartSection(int startSection) { this.startSection = startSection; }

    public int getEndSection() { return endSection; }
    public void setEndSection(int endSection) { this.endSection = endSection; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    // 获取时间段描述
    public String getTimeDescription() {
        String[] weekdays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return weekdays[dayOfWeek] + " " + startSection + "-" + endSection + "节" +
                (startTime != null ? " (" + startTime + "-" + endTime + ")" : "");
    }

    // 判断是否与另一个时间段冲突
    public boolean conflictsWith(TimeSlot other) {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false;
        }
        return !(this.endSection < other.startSection || other.endSection < this.startSection);
    }

    @Override
    public String toString() {
        return getTimeDescription();
    }
}