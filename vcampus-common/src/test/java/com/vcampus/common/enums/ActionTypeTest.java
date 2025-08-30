package com.vcampus.common.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ActionType枚举测试类
 * 测试枚举的各种功能
 */
public class ActionTypeTest {

    @Test
    public void testEnumValues() {
        // 测试枚举值
        assertEquals("登录", ActionType.LOGIN.getDescription());
        assertEquals("登出", ActionType.LOGOUT.getDescription());
    }

    @Test
    public void testFromDescription() {
        // 测试根据描述获取枚举值
        assertEquals(ActionType.LOGIN, ActionType.fromDescription("登录"));
        assertEquals(ActionType.LOGOUT, ActionType.fromDescription("登出"));
    }

    @Test
    public void testFromDescriptionInvalid() {
        // 测试无效描述
        assertThrows(IllegalArgumentException.class, () -> {
            ActionType.fromDescription("无效描述");
        });
    }

    @Test
    public void testFromName() {
        // 测试根据名称获取枚举值
        assertEquals(ActionType.LOGIN, ActionType.fromName("LOGIN"));
        assertEquals(ActionType.LOGOUT, ActionType.fromName("LOGOUT"));
        assertEquals(ActionType.LOGIN, ActionType.fromName("login")); // 测试大小写不敏感
    }

    @Test
    public void testFromNameInvalid() {
        // 测试无效名称
        assertThrows(IllegalArgumentException.class, () -> {
            ActionType.fromName("INVALID");
        });
    }
}
