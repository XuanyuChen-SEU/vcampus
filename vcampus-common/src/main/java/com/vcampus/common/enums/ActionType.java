package com.vcampus.common.enums;

/**
 * 网络通信动作类型枚举
 * 定义客户端与服务端之间的所有操作类型
 * 编写人：谌宣羽
 */
public enum ActionType {
    LOGIN("登录"),
    LOGOUT("登出"),
    REGISTER("注册"),
    GLOBAL_NOTIFICATION("全局通知"),
    SYSTEM_BROADCAST("系统广播"),
    EMERGENCY_NOTIFICATION("紧急通知");

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
