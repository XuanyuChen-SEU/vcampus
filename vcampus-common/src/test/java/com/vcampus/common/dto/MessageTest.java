package com.vcampus.common.dto;

import com.vcampus.common.enums.ActionType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Message类测试类
 * 测试消息对象的各种功能
 */
public class MessageTest {

    @Test
    public void testDefaultConstructor() {
        // 测试默认构造方法
        Message message = new Message();
        assertNull(message.getAction());
        assertNull(message.getData());
        assertFalse(message.isStatus());
        assertNull(message.getMessage());
    }

    @Test
    public void testFullConstructor() {
        // 测试完整构造方法
        String testData = "test data";
        Message message = new Message(ActionType.LOGIN, testData, true, "成功");
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertEquals(testData, message.getData());
        assertTrue(message.isStatus());
        assertEquals("成功", message.getMessage());
    }

    @Test
    public void testRequestConstructor() {
        // 测试请求构造方法
        String testData = "login data";
        Message message = new Message(ActionType.LOGIN, testData);
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertEquals(testData, message.getData());
        assertFalse(message.isStatus());
        assertEquals("", message.getMessage());
    }

    @Test
    public void testResponseConstructor() {
        // 测试响应构造方法
        Message message = new Message(ActionType.LOGIN, true, "登录成功");
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertNull(message.getData());
        assertTrue(message.isStatus());
        assertEquals("登录成功", message.getMessage());
    }

    @Test
    public void testIsSuccess() {
        // 测试成功状态判断
        Message successMessage = new Message(ActionType.LOGIN, null, true, "成功");
        Message failureMessage = new Message(ActionType.LOGIN, null, false, "失败");
        
        assertTrue(successMessage.isSuccess());
        assertFalse(failureMessage.isSuccess());
    }

    @Test
    public void testIsFailure() {
        // 测试失败状态判断
        Message successMessage = new Message(ActionType.LOGIN, null, true, "成功");
        Message failureMessage = new Message(ActionType.LOGIN, null, false, "失败");
        
        assertFalse(successMessage.isFailure());
        assertTrue(failureMessage.isFailure());
    }

    @Test
    public void testSuccessStaticMethod() {
        // 测试成功静态方法
        String testData = "user data";
        Message message = Message.success(ActionType.LOGIN, testData, "登录成功");
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertEquals(testData, message.getData());
        assertTrue(message.isStatus());
        assertEquals("登录成功", message.getMessage());
    }

    @Test
    public void testSuccessStaticMethodNoData() {
        // 测试成功静态方法（无数据）
        Message message = Message.success(ActionType.LOGIN, "登录成功");
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertNull(message.getData());
        assertTrue(message.isStatus());
        assertEquals("登录成功", message.getMessage());
    }

    @Test
    public void testFailureStaticMethod() {
        // 测试失败静态方法
        Message message = Message.failure(ActionType.LOGIN, "用户名或密码错误");
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertNull(message.getData());
        assertFalse(message.isStatus());
        assertEquals("用户名或密码错误", message.getMessage());
    }

    @Test
    public void testFailureStaticMethodWithData() {
        // 测试失败静态方法（带数据）
        String testData = "error data";
        Message message = Message.failure(ActionType.LOGIN, testData, "登录失败");
        
        assertEquals(ActionType.LOGIN, message.getAction());
        assertEquals(testData, message.getData());
        assertFalse(message.isStatus());
        assertEquals("登录失败", message.getMessage());
    }

    @Test
    public void testSetterMethods() {
        // 测试setter方法
        Message message = new Message();
        
        message.setAction(ActionType.LOGOUT);
        message.setData("logout data");
        message.setStatus(true);
        message.setMessage("登出成功");
        
        assertEquals(ActionType.LOGOUT, message.getAction());
        assertEquals("logout data", message.getData());
        assertTrue(message.isStatus());
        assertEquals("登出成功", message.getMessage());
    }

    @Test
    public void testToString() {
        // 测试toString方法
        Message message = new Message(ActionType.LOGIN, "test data", true, "成功");
        String result = message.toString();
        
        assertTrue(result.contains("LOGIN"));
        assertTrue(result.contains("test data"));
        assertTrue(result.contains("true"));
        assertTrue(result.contains("成功"));
    }

    @Test
    public void testSerializable() {
        // 测试序列化支持
        Message message = new Message(ActionType.LOGIN, "test", true, "success");
        
        // 检查是否实现了Serializable接口
        assertTrue(message instanceof java.io.Serializable);
    }
}
