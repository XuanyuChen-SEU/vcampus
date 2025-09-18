package com.vcampus.common.enums;

/**
 * 邮件状态枚举类（简化版）
 * 用于表示邮件的不同状态
 * 编写人：谌宣羽
 */
public enum EmailStatus {
    DRAFT(1, "草稿"),
    SENT(2, "已发送"),
    READ(3, "已读");

    private final int code;
    private final String description;

    EmailStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据数据库编码获取枚举实例
     * @param code 数据库存储的状态编码
     * @return 对应的邮件状态枚举
     * @throws IllegalArgumentException 当编码无效时抛出
     */
    public static EmailStatus fromCode(int code) {
        for (EmailStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的邮件状态编码: " + code);
    }

    /**
     * 根据描述获取枚举实例
     * @param description 状态描述
     * @return 对应的邮件状态枚举
     * @throws IllegalArgumentException 当描述无效时抛出
     */
    public static EmailStatus fromDescription(String description) {
        for (EmailStatus status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的邮件状态描述: " + description);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
