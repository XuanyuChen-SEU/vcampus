package com.vcampus.client.session;

import com.vcampus.common.enums.Role;

/**
 * 全局用户会话管理器
 * 负责管理当前登录用户的状态信息
 * 采用单例模式，确保整个应用程序中只有一个用户会话实例
 * 
 * 编写人：谌宣羽
 */
public class UserSession {
    
    private static UserSession instance;
    
    // 当前登录用户的ID
    private String currentUserId;
    
    // 当前登录用户的角色
    private Role currentUserRole;
    
    // 登录状态
    private boolean isLoggedIn;
    
    /**
     * 私有构造方法，防止外部实例化
     */
    private UserSession() {
        this.currentUserId = null;
        this.currentUserRole = null;
        this.isLoggedIn = false;
    }
    
    /**
     * 获取单例实例
     * @return UserSession实例
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    /**
     * 设置当前登录用户
     * @param userId 用户ID
     */
    public void setCurrentUser(String userId) {
        this.currentUserId = userId;
        this.isLoggedIn = true;
        
        // 根据用户ID解析角色
        try {
            this.currentUserRole = Role.fromUserId(userId);
        } catch (IllegalArgumentException e) {
            System.err.println("无法解析用户角色: " + e.getMessage());
            this.currentUserRole = null;
        }
        
        System.out.println("用户会话已设置: " + userId + " (" + 
                          (currentUserRole != null ? currentUserRole.getDesc() : "未知角色") + ")");
    }
    
    /**
     * 清除当前用户会话（登出）
     */
    public void clearSession() {
        this.currentUserId = null;
        this.currentUserRole = null;
        this.isLoggedIn = false;
        System.out.println("用户会话已清除");
    }
    
    /**
     * 获取当前用户ID
     * @return 用户ID，如果未登录则返回null
     */
    public String getCurrentUserId() {
        return currentUserId;
    }




    /**
     * 获取当前用户角色
     * @return 用户角色，如果未登录则返回null
     */
    public Role getCurrentUserRole() {
        return currentUserRole;
    }
    
    /**
     * 检查是否已登录
     * @return true如果已登录，false如果未登录
     */
    public boolean isLoggedIn() {
        return isLoggedIn && currentUserId != null;
    }
    
    /**
     * 获取当前用户的显示名称
     * @return 格式化的用户信息字符串
     */
    public String getCurrentUserDisplayName() {
        if (!isLoggedIn()) {
            return "未登录";
        }
        
        String roleDesc = currentUserRole != null ? currentUserRole.getDesc() : "未知角色";
        return roleDesc + " " + currentUserId;
    }
    
    /**
     * 检查当前用户是否具有指定角色
     * @param role 要检查的角色
     * @return true如果当前用户具有指定角色
     */
    public boolean hasRole(Role role) {
        return isLoggedIn() && currentUserRole == role;
    }
    
    /**
     * 获取会话状态信息（用于调试）
     * @return 包含会话状态的字符串
     */
    public String getSessionInfo() {
        return String.format("UserSession{userId='%s', role=%s, loggedIn=%s}", 
                           currentUserId, 
                           currentUserRole != null ? currentUserRole.getDesc() : "null",
                           isLoggedIn);
    }
}