package com.vcampus.common.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码加密工具类
 * 使用jBCrypt进行密码加密和验证
 * 
 * @author AI Assistant
 */
public class PasswordUtil {
    
    /**
     * 默认的加密强度（cost factor）
     * 值越高，加密越安全但计算越慢
     * 推荐值：10-12
     */
    private static final int DEFAULT_ROUNDS = 10;
    
    /**
     * 加密密码
     * 
     * @param plainPassword 明文密码
     * @return 加密后的密码哈希值
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(DEFAULT_ROUNDS));
    }
    
    /**
     * 验证密码
     * 
     * @param plainPassword 明文密码
     * @param hashedPassword 加密后的密码哈希值
     * @return true表示密码正确，false表示密码错误
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            // 如果验证过程中出现异常，返回false
            return false;
        }
    }
    
    /**
     * 检查密码强度
     * 
     * @param password 密码
     * @return 密码强度等级：weak, medium, strong
     */
    public static String checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "weak";
        }
        
        int score = 0;
        
        // 长度检查
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // 包含小写字母
        if (password.matches(".*[a-z].*")) score++;
        
        // 包含大写字母
        if (password.matches(".*[A-Z].*")) score++;
        
        // 包含数字
        if (password.matches(".*[0-9].*")) score++;
        
        // 包含特殊字符
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        
        if (score <= 2) return "weak";
        if (score <= 4) return "medium";
        return "strong";
    }
    
    /**
     * 生成随机密码
     * 
     * @param length 密码长度
     * @return 随机生成的密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8;
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试密码加密和验证
        String password = "test123456";
        String hashed = hashPassword(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("加密后: " + hashed);
        System.out.println("验证结果: " + verifyPassword(password, hashed));
        System.out.println("密码强度: " + checkPasswordStrength(password));
        
        // 测试随机密码生成
        String randomPassword = generateRandomPassword(12);
        System.out.println("随机密码: " + randomPassword);
        System.out.println("随机密码强度: " + checkPasswordStrength(randomPassword));
    }
}
