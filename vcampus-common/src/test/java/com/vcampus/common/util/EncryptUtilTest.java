package com.vcampus.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptUtilTest {

    // 测试正常密码加密
    @Test
    public void testHashPassword() {
        String plainPassword = "TestPass123!";

        // 加密密码
        String hashedPassword = EncryptUtil.hashPassword(plainPassword);

        // 验证加密结果不为空且与明文不同
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
    }

    // 测试不同明文加密后结果不同（盐值随机）
    @Test
    public void testHashDifferentForSamePlaintext() {
        String plainPassword = "SamePass456!";

        // 对同一明文加密两次
        String hash1 = EncryptUtil.hashPassword(plainPassword);
        String hash2 = EncryptUtil.hashPassword(plainPassword);

        // 验证两次加密结果不同（因为盐值随机）
        assertNotEquals(hash1, hash2);
    }

    // 测试密码校验：正确密码匹配
    @ParameterizedTest
    @ValueSource(strings = {
            "123456",          // 纯数字
            "abcdef",          // 纯字母
            "Abc123!",         // 混合字符
            ""                 // 空密码（业务层可限制，工具类允许）
    })
    public void testCheckPasswordMatch(String plainPassword) {
        String hashed = EncryptUtil.hashPassword(plainPassword);

        // 验证正确密码能匹配
        assertTrue(EncryptUtil.checkPassword(plainPassword, hashed));
    }

    // 测试密码校验：错误密码不匹配
    @Test
    public void testCheckPasswordMismatch() {
        String plain = "CorrectPass";
        String hashed = EncryptUtil.hashPassword(plain);

        // 验证错误密码不匹配
        assertFalse(EncryptUtil.checkPassword("WrongPass", hashed));
        assertFalse(EncryptUtil.checkPassword(plain + "extra", hashed));
        assertFalse(EncryptUtil.checkPassword("", hashed)); // 空密码不匹配非空原密码
    }

    // 测试异常场景：加密时明文为null
    @Test
    public void testHashPasswordWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.hashPassword(null);
        });
    }

    // 测试异常场景：校验时参数为null
    @Test
    public void testCheckPasswordWithNull() {
        String hashed = EncryptUtil.hashPassword("test");

        // 明文为null时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.checkPassword(null, hashed);
        });

        // 密文为null时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.checkPassword("test", null);
        });
    }
}
