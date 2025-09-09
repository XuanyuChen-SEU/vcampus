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
    INFO_STUDENT("获取学生信息"), // <-- 保留这行，并确保后面是逗号

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
    SHOP_ADMIN_REVIEW_RETURN("管理员审核退货"); // <-- 确保最后一个是分号


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