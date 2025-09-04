package com.vcampus.server.service;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.Role;
import com.vcampus.server.dao.impl.UserDao;
/**
 * 用户服务类
 * 负责用户相关的业务逻辑，包括登录、忘记密码、重置密码等
 * 编写人：谌宣羽
 */
public class UserService {
    
    // 固定的测试账号密码
    private static final String TEST_USER_ID = "1000000";
    private static final String TEST_PASSWORD = "1000000";
    private final UserDao userDao ;
    public UserService()
    {
        userDao = new UserDao();
    }
    /**
     * 验证用户登录
     * @param loginUser 登录用户信息
     * @return 登录结果，包含成功/失败信息和用户数据
     */
    public LoginResult validateLogin(User loginUser) {
        try {
            // 1. 验证输入参数
            if (loginUser == null || loginUser.getUserId() == null || loginUser.getPassword() == null) {
                return new LoginResult(false, "登录信息不完整", null);
            }
            
            String userId = loginUser.getUserId();
            String plainPassword = loginUser.getPassword();

            userDao.IUDInit();
            User user=userDao.getUserById(loginUser.getUserId());
            String id="1234321";
            String pass="7654321";
            User u=new User(id,pass);
            userDao.updateUser(u);
            // 2. 验证用户ID格式
            if (!userId.matches("\\d{7}")) {
                return new LoginResult(false, "用户ID格式错误，必须为7位数字", null);
            }
            
            // 3. 验证固定的测试账号密码
            if (!TEST_USER_ID.equals(userId)) {
                return new LoginResult(false, "用户不存在", null);
            }
            
            // 4. 验证密码
            if (!TEST_PASSWORD.equals(plainPassword)) {
                return new LoginResult(false, "密码错误", null);
            }
            
            // 5. 获取用户角色
            Role role = Role.fromUserId(userId);
            
            // 6. 登录成功，返回用户信息（不包含密码）
            User resultUser = new User(userId, ""); // 密码置空，不返回给客户端
            
            return new LoginResult(true, "登录成功，欢迎 " + role.getDesc() + " " + userId, resultUser);
            
        } catch (IllegalArgumentException e) {
            return new LoginResult(false, "用户ID格式错误: " + e.getMessage(), null);
        } catch (Exception e) {
            System.err.println("登录验证过程中发生异常: " + e.getMessage());
            return new LoginResult(false, "服务器内部错误", null);
        }
    }
    
    /**
     * 处理忘记密码申请
     * @param user 用户信息
     * @return 忘记密码处理结果
     */
    public ForgetPasswordResult handleForgetPassword(User user) {
        try {
            // 1. 验证输入参数
            if (user == null || user.getUserId() == null || user.getPassword() == null) {
                return new ForgetPasswordResult(false, "用户信息不完整");
            }
            
            String userId = user.getUserId();
            String oldPassword = user.getPassword();
            
            // 2. 验证用户ID格式
            if (!userId.matches("\\d{7}")) {
                return new ForgetPasswordResult(false, "用户ID格式错误，必须为7位数字");
            }
            
            // 3. 验证用户是否存在
            if (!TEST_USER_ID.equals(userId)) {
                return new ForgetPasswordResult(false, "用户不存在");
            }
            
            
            // 5. 创建密码重置申请（模拟成功）
            // TODO: 这里可以添加数据库操作，记录密码重置申请
            boolean success = true; // 模拟成功
            
            if (success) {
                return new ForgetPasswordResult(true, "密码重置申请已提交，请等待管理员审核");
            } else {
                return new ForgetPasswordResult(false, "密码重置申请提交失败，请稍后重试");
            }
            
        } catch (Exception e) {
            System.err.println("处理忘记密码申请时发生异常: " + e.getMessage());
            return new ForgetPasswordResult(false, "服务器内部错误");
        }
    }
    
    /**
     * 登录结果内部类
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
    }
    
    /**
     * 忘记密码结果内部类
     */
    public static class ForgetPasswordResult {
        private final boolean success;
        private final String message;
        
        public ForgetPasswordResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
