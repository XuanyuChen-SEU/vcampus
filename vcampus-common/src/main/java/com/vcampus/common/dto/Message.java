package com.vcampus.common.dto;

import java.io.Serializable;

public class Message implements Serializable {
    private String action;   // 动作指令，例如 LOGIN, QUERY_COURSE
    private Object data;     // 携带的数据
    private String status;   // 状态码，例如 200, 400

    public Message() {}
    public Message(String action, Object data, String status) {
        this.action = action;
        this.data = data;
        this.status = status;
    }

    // getter & setter
}
