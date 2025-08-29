package com.vcampus.common.dto;

import java.io.Serializable;

public class User implements Serializable {
    private int id;          // 用户ID
    private String username; // 一卡通号
    private String password; // 加密后的密码
    private String role;     // 用户角色（学生/教师/管理员）

    // getter & setter
}
