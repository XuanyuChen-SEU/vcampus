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
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    // 验证明文密码与加密后的密码是否匹配
    public static boolean checkPassword(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}