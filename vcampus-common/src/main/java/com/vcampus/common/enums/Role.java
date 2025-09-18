package com.vcampus.common.enums;

/**
 * 角色枚举类
 * 用于表示用户的不同角色（学生、教师、管理员）
 * 包含角色描述，便于日志输出和界面展示
 * 编写人：谌宣羽
 *
 */
public enum Role {
    STUDENT(1, "学生"),
    TEACHER(2, "教师"),
    USER_ADMIN(3, "用户管理员"),
    STUDENT_ADMIN(4, "学籍管理员"),
    COURSE_ADMIN(5, "教务管理员"),
    LIBRARY_ADMIN(6, "图书馆管理员"),
    SHOP_ADMIN(7, "商店管理员"),
    EMAIL_ADMIN(8, "邮件管理员");


    private final int code;  // 数据库存储的编码（如 "student"）
    private final String desc;  // 显示用描述

    Role(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据数据库编码获取枚举实例
     * @param code 数据库存储的角色编码
     * @return 对应的角色枚举
     * @throws IllegalArgumentException 当编码无效时抛出
     */
    public static Role fromCode(int code) {
        for (Role role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("无效的角色编码: " + code);
    }

    /**
     * 通过用户ID获取角色（解析用户ID的第一位）
     * @param userId 用户ID（格式：7位字符串，第一位为角色编码）
     * @return 对应的角色枚举
     * @throws IllegalArgumentException 当用户ID为空、长度不符或第一位无效时抛出
     */
    public static Role fromUserId(String userId) {
        // 提取第一位
        String roleCode = userId.substring(0, 1);
        // 匹配对应的角色
        for (Role role : values()) {
            // 将转换为整数进行比较
            if (role.code == Integer.parseInt(roleCode)) {
                return role;
            }
        }
        throw new IllegalArgumentException("用户ID第一位无效，无法解析角色: " + userId);
    }

    // Getter
    public int getCode() { return code; }
    public String getDesc() { return desc; }
}