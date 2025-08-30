package com.vcampus.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vcampus.common.dao.UserDAO;
import com.vcampus.common.dto.User;
import com.vcampus.common.util.EncryptUtil;
import com.vcampus.server.dao.impl.UserDAOImpl;

/**
 * 用户业务逻辑服务类
 * 处理用户相关的业务逻辑，包括登录验证、密码管理等
 * 编写人：谌宣羽
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAOImpl();
    }
    
    /**
     * 用户登录验证
     * @param userId 用户ID
     * @param password 明文密码
     * @return 登录结果，成功返回用户对象，失败返回null
     */
    public User login(String userId, String password) {
        logger.info("用户登录验证: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("用户ID为空");
                return null;
            }
            
            if (password == null || password.trim().isEmpty()) {
                logger.warn("密码为空");
                return null;
            }
            
            // 验证用户ID格式（7位数字）
            if (!userId.matches("\\d{7}")) {
                logger.warn("用户ID格式错误: {}", userId);
                return null;
            }
            
            // 根据用户ID查找用户
            User user = userDAO.findById(userId);
            if (user == null) {
                logger.warn("用户不存在: {}", userId);
                return null;
            }
            
            // 验证密码
            if (!EncryptUtil.checkPassword(password, user.getPassword())) {
                logger.warn("密码错误: {}", userId);
                return null;
            }
            
            logger.info("用户登录成功: {}, 角色: {}", userId, user.getRole().getDesc());
            return user;
            
        } catch (Exception e) {
            logger.error("登录验证异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 如果存在返回true，否则返回false
     */
    public boolean userExists(String userId) {
        logger.info("检查用户是否存在: {}", userId);
        
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return false;
            }
            
            return userDAO.exists(userId);
            
        } catch (Exception e) {
            logger.error("检查用户存在性异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户对象，如果不存在返回null
     */
    public User getUserById(String userId) {
        logger.info("获取用户信息: {}", userId);
        
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return null;
            }
            
            return userDAO.findById(userId);
            
        } catch (Exception e) {
            logger.error("获取用户信息异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     * @return 更新是否成功
     */
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        logger.info("更新用户密码: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || oldPassword == null || newPassword == null) {
                logger.warn("参数为空");
                return false;
            }
            
            // 验证旧密码
            User user = login(userId, oldPassword);
            if (user == null) {
                logger.warn("旧密码验证失败");
                return false;
            }
            
            // 加密新密码
            String hashedNewPassword = EncryptUtil.hashPassword(newPassword);
            
            // 更新密码
            int result = userDAO.updatePassword(userId, hashedNewPassword);
            
            if (result > 0) {
                logger.info("密码更新成功: {}", userId);
                return true;
            } else {
                logger.warn("密码更新失败: {}", userId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新密码异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 更新用户手机号
     * @param userId 用户ID
     * @param newPhone 新手机号
     * @return 更新是否成功
     */
    public boolean updatePhone(String userId, String newPhone) {
        logger.info("更新用户手机号: {}", userId);
        
        try {
            // 参数验证
            if (userId == null || newPhone == null) {
                logger.warn("参数为空");
                return false;
            }
            
            // 验证手机号格式
            if (!newPhone.matches("\\d{11}")) {
                logger.warn("手机号格式错误: {}", newPhone);
                return false;
            }
            
            // 更新手机号
            int result = userDAO.updatePhone(userId, newPhone);
            
            if (result > 0) {
                logger.info("手机号更新成功: {}", userId);
                return true;
            } else {
                logger.warn("手机号更新失败: {}", userId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新手机号异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取用户角色描述
     * @param user 用户对象
     * @return 角色描述
     */
    public String getUserRoleDescription(User user) {
        if (user == null || user.getRole() == null) {
            return "未知";
        }
        return user.getRole().getDesc();
    }
}
