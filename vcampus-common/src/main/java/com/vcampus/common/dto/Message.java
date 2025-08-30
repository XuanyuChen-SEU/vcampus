package com.vcampus.common.dto;

import java.io.Serializable;

import com.vcampus.common.enums.ActionType;

/**
 * 网络通信消息对象
 * 用于客户端与服务端之间的消息传递
 * 编写人：谌宣羽
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private ActionType action;    // 操作类型
    private Object data;          // 传递的数据
    private boolean status;       // 操作状态（true=成功，false=失败）
    private String message;       // 状态描述

    // 默认构造方法（反序列化必需）
    public Message() {}

    public Message(ActionType action, Object data, boolean status, String message) {
        this.action = action;
        this.data = data;
        this.status = status;
        this.message = message;
    }

    // 简化构造方法(用于请求)
    public Message(ActionType action, Object data) {
        this(action, data, false, "");
    }

    // 简化构造方法(用于响应)
    public Message(ActionType action, boolean status, String message) {
        this(action, null, status, message);
    }

    // Getter & Setter
    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // 判断操作是否成功
    public boolean isSuccess() {
        return status;
    }

    // 判断操作是否失败
    public boolean isFailure() {
        return !status;
    }

    // 创建成功消息的静态方法
    public static Message success(ActionType action, Object data, String message) {
        return new Message(action, data, true, message);
    }

    // 创建成功消息的静态方法(无数据)
    public static Message success(ActionType action, String message) {
        return new Message(action, null, true, message);
    }

    // 创建失败消息的静态方法
    public static Message failure(ActionType action, String message) {
        return new Message(action, null, false, message);
    }

    // 创建失败消息的静态方法(带数据)
    public static Message failure(ActionType action, Object data, String message) {
        return new Message(action, data, false, message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "action=" + action +
                ", data=" + data +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
