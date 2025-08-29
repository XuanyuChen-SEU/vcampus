package com.vcampus.common.util;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptUtil {
    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
