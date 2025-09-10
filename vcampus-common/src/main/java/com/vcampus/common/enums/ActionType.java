package com.vcampus.common.enums;

/**
 * 网络通信动作类型枚举
 * 定义客户端与服务端之间的所有操作类型
 * 编写人：谌宣羽
 */
public enum ActionType {
    LOGIN("登录"),
    FORGET_PASSWORD("忘记密码"),
    LOGOUT("登出"),
    CHANGE_PASSWORD("修改密码"),
    REGISTER("注册"),
    GLOBAL_NOTIFICATION("全局通知"),
    SYSTEM_BROADCAST("系统广播"),
    EMERGENCY_NOTIFICATION("紧急通知"),
    //新增课程（学生端）相关枚举值
//    GET_COURSE_TABLE("获取课表"),    // 获取所有可选课程
//    GET_COURSE_DETAIL("获取课程详情"),           // 获取单个课程的详细信息
//    ENROLL_COURSE("选课"),                      // 学生选课
//    DROP_COURSE("退课"),                        // 学生退课
//    CHECK_COURSE_CONFLICT("检查课程冲突"),       // 检查所选课程是否与已选课程冲突
//
//    // 2. 课表功能
//    GET_STUDENT_TIMETABLE("获取学生课表"),       // 获取学生的完整课表
//    GET_STUDENT_ENROLLED_COURSES("获取学生已选课程"), // 获取学生已选课程列表（不包含课表格式）
//
//    // 课程详情相关
//    GET_COURSE_ENROLLMENT_STATUS("获取课程报名状态"), // 获取课程的报名状态（已选人数等）
//
    INFO_STUDENT("获取学生信息"),
    // === 选课相关操作 ===

    // 客户端 -> 服务端 (请求)
    GET_ALL_COURSES("获取所有课程"),    // 获取所有可选课程（拉取课表）
    SELECT_COURSE("选择课程"),      // 选择一门课的某个教学班
    DROP_COURSE("退选课程"),        // 退选一门课的某个教学班

    // 服务端 -> 客户端 (响应)
    GET_ALL_COURSES_RESPONSE("对获取课程请求的响应"),   // 对获取课程请求的响应
    SELECT_COURSE_RESPONSE("对选课请求的响应"),     // 对选课请求的响应
    DROP_COURSE_RESPONSE("对退课请求的响应");       // 对退课请求的响应







    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    /**
     * 根据描述获取枚举值
     * @param description 描述
     * @return 对应的枚举值
     */
    public static ActionType fromDescription(String description) {
        for (ActionType actionType : values()) {
            if (actionType.description.equals(description)) {
                return actionType;
            }
        }
        throw new IllegalArgumentException("无效的动作类型描述: " + description);
    }

    /**
     * 根据字符串获取枚举值
     * @param name 枚举名称
     * @return 对应的枚举值
     */
    public static ActionType fromName(String name) {
        try {
            return ActionType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的动作类型名称: " + name);
        }
    }

    public String getDescription() {
        return description;
    }
}
