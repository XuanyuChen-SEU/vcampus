package com.vcampus.common.util;

import com.vcampus.common.util.EncryptUtil;

public class Test {
    public static void main(String[] args) {
        String password = "teacher123";
        String encryptedPassword = EncryptUtil.hashPassword(password);
        System.out.println(encryptedPassword);
    }
}
