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
    INFO_STUDENT("获取学生信息"),
    UPDATE_STUDENT("修改学生信息"),

    // === 用户管理相关操作 ===
    CREATE_USER("创建用户"),                   // 创建新用户
    DELETE_USER("删除用户"),                   // 删除用户
    RESET_USER_PASSWORD("重置用户密码"),        // 重置用户密码
    SEARCH_USERS("搜索用户"),                  // 搜索用户
    GET_FORGET_PASSWORD_TABLE("获取忘记密码申请"),                  // 获取忘记密码申请  
    APPROVE_FORGET_PASSWORD_APPLICATION("批准忘记密码申请"),        // 批准忘记密码申请
    REJECT_FORGET_PASSWORD_APPLICATION("拒绝忘记密码申请"),         // 拒绝忘记密码申请  


    // --- 商店模块 - 用户端 ---
    // 商品浏览与搜索
    SHOP_GET_ALL_PRODUCTS("获取所有商品"),
    SHOP_SEARCH_PRODUCTS("搜索商品"),
    SHOP_GET_PRODUCT_DETAIL("获取商品详情"),
    // 订单管理
    SHOP_CREATE_ORDER("创建订单"),
    SHOP_GET_MY_ORDERS("获取我的订单"),
    SHOP_CANCEL_ORDER("取消订单"),
    // 收藏夹管理
    SHOP_ADD_FAVORITE("添加收藏"),
    SHOP_GET_MY_FAVORITES("获取我的收藏"),
    SHOP_REMOVE_FAVORITE("取消收藏"),
    // 售后服务
    SHOP_REQUEST_RETURN("申请退货"),


    // --- 商店模块 - 管理员端 ---
    SHOP_ADMIN_ADD_PRODUCT("管理员添加商品"),
    SHOP_ADMIN_UPDATE_PRODUCT("管理员更新商品"),
    SHOP_ADMIN_DELETE_PRODUCT("管理员删除商品"),
    SHOP_ADMIN_GET_ALL_RETURNS("管理员获取所有退货申请"),
    SHOP_ADMIN_REVIEW_RETURN("管理员审核退货"),// <-- 确保最后一个是分号
    // === 选课相关操作 ===

    // 客户端 -> 服务端 (请求)
    GET_ALL_COURSES("获取所有课程"),    // 获取所有可选课程（拉取课表）
    SELECT_COURSE("选择课程"),      // 选择一门课的某个教学班
    DROP_COURSE("退选课程"),        // 退选一门课的某个教学班

    // 服务端 -> 客户端 (响应)
    GET_ALL_COURSES_RESPONSE("对获取课程请求的响应"),   // 对获取课程请求的响应
    SELECT_COURSE_RESPONSE("对选课请求的响应"),     // 对选课请求的响应
    DROP_COURSE_RESPONSE("对退课请求的响应"),      // 对退课请求的响应

    // ⭐ 新增：获取我的课表
    GET_MY_COURSES("获取我的课表"),
    GET_MY_COURSES_RESPONSE("对获取我的课表请求的响应");


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