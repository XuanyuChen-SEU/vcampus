package com.vcampus.common.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * jbcrypt的工具类
 * 用于密码加密与校验
 * 编写人：谌宣羽
 */
public class EncryptUtil {
    // 对明文密码进行加密
    public static String hashPassword(String plain) {
        // 新增：主动校验null，确保抛出异常
        if (plain == null) {
            throw new IllegalArgumentException("明文密码不能为null");
        }
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    // 验证明文密码与加密后的密码是否匹配
    public static boolean checkPassword(String plain, String hashed) {
        // 新增：主动校验null，确保抛出异常
        if (plain == null || hashed == null) {
            throw new IllegalArgumentException("明文或密文不能为null");
        }
        return BCrypt.checkpw(plain, hashed);
    }
}
