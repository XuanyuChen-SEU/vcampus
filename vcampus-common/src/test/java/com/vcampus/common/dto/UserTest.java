package com.vcampus.common.dto;

import com.vcampus.common.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class UserTest {

    // 测试合法参数的构造方法
    @Test
    public void testValidConstructor() {
        String validUserId = "1234567";
        String password = "encryptedPass";
        String validPhone = "13800138000";
        Role role = Role.STUDENT;

        User user = new User(validUserId, password, validPhone, role);

        // 验证参数正确设置
        assertEquals(validUserId, user.getUserId());
        assertEquals(password, user.getPassword());
        assertEquals(validPhone, user.getPhone());
        assertEquals(role, user.getRole());
    }

    // 测试用户ID不合法的构造方法（参数化测试）
    @ParameterizedTest
    @ValueSource(strings = {
            "123456",       // 6位
            "12345678",     // 8位
            "123abc7",      // 包含字母
            "123 4567"      // 包含空格
    })
    public void testInvalidUserIdInConstructor(String invalidUserId) {
        // 执行构造方法并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            new User(invalidUserId, "pass", "13800138000", Role.TEACHER);
        });
    }

    // 测试手机号不合法的构造方法（参数化测试）
    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",   // 10位
            "123456789012", // 12位
            "1380013800a",  // 包含字母
            "138 00138000"  // 包含空格
    })
    public void testInvalidPhoneInConstructor(String invalidPhone) {
        // 执行构造方法并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            new User("1234567", "pass", invalidPhone, Role.ADMIN);
        });
    }

    // 测试setUserId方法（合法和非法参数）
    @Test
    public void testSetUserId() {
        User user = new User();

        // 测试合法ID
        String validId = "7654321";
        user.setUserId(validId);
        assertEquals(validId, user.getUserId());

        // 测试非法ID
        assertThrows(IllegalArgumentException.class, () -> user.setUserId("123"));
        assertThrows(IllegalArgumentException.class, () -> user.setUserId("abc1234"));
    }

    // 测试setPhone方法（合法和非法参数）
    @Test
    public void testSetPhone() {
        User user = new User();
        user.setUserId("1234567"); // 先设置合法的用户ID（避免构造方法影响）

        // 测试合法手机号
        String validPhone = "13912345678";
        user.setPhone(validPhone);
        assertEquals(validPhone, user.getPhone());

        // 测试非法手机号
        assertThrows(IllegalArgumentException.class, () -> user.setPhone("123"));
        assertThrows(IllegalArgumentException.class, () -> user.setPhone("1380013800a"));
    }

    // 测试密码设置（不校验格式，直接存储）
    @Test
    public void testSetPassword() {
        User user = new User();
        String password = "encrypted123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());

        // 测试空密码（允许，业务层可额外限制）
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    // 测试角色设置
    @Test
    public void testSetRole() {
        User user = new User();
        Role role = Role.TEACHER;
        user.setRole(role);
        assertEquals(role, user.getRole());

        // 测试空角色（允许，根据业务场景判断是否合理）
        user.setRole(null);
        assertNull(user.getRole());
    }

    // 测试序列化与反序列化（确保网络传输正常）
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        // 创建原始对象
        User original = new User("1000001", "hashPass", "13800138000", Role.STUDENT);

        // 序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(original);

        // 反序列化
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        User deserialized = (User) ois.readObject();

        // 验证反序列化后数据一致
        assertEquals(original.getUserId(), deserialized.getUserId());
        assertEquals(original.getPassword(), deserialized.getPassword());
        assertEquals(original.getPhone(), deserialized.getPhone());
        assertEquals(original.getRole(), deserialized.getRole());
    }
}
