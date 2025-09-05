package com.vcampus.server.service;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.Role;
import com.vcampus.server.dao.impl.UserDao;

/**
 * 用户服务类
 * 负责用户相关的业务逻辑，包括登录、忘记密码、重置密码等
 * 编写人：谌宣羽
 */
public class UserService {
    private final UserDao userDao;

    public UserService()
    {
        userDao = new UserDao();
    }

    /**
     * 验证用户登录
     * @param loginUser 登录用户信息
     * @return 登录结果，包含成功/失败信息和用户数据
     */
    public Message validateLogin(User loginUser) {
        try {
            User user = userDao.getUserById(loginUser.getUserId());
            if (user.getUserId().equals("")) {
                return Message.failure(ActionType.LOGIN, "用户不存在");
            }
            if (!user.getPassword().equals(loginUser.getPassword())) {
                return Message.failure(ActionType.LOGIN, "密码错误");
            }
            Role role = Role.fromUserId(loginUser.getUserId());
            
            User resultUser = new User(loginUser.getUserId(), ""); // 密码置空，不返回给客户端
            
            return Message.success(ActionType.LOGIN, resultUser, "登录成功，欢迎 " + role.getDesc() + " " + loginUser.getUserId());
            
        } catch (Exception e) {
            System.err.println("登录验证过程中发生异常: " + e.getMessage());
            return Message.failure(ActionType.LOGIN, "服务器内部错误");
        }
    }
    
    /**
     * 处理忘记密码申请
     * @param user 用户信息
     * @return 忘记密码处理结果
     */
    public Message handleForgetPassword(User user) {
        try {
            // 1. 验证输入参数
            if (user == null || user.getUserId() == null) {
                return Message.failure(ActionType.FORGET_PASSWORD, "用户信息不完整");
            }
            
            String userId = user.getUserId();
            
            // 2. 验证用户ID格式
            if (!userId.matches("\\d{7}")) {
                return Message.failure(ActionType.FORGET_PASSWORD, "用户ID格式错误，必须为7位数字");
            }
            
            // 3. 验证用户是否存在
            if (!user.getUserId().equals(userId)) {
                return Message.failure(ActionType.FORGET_PASSWORD, "用户不存在");
            }
            
            
            // 5. 创建密码重置申请（模拟成功）
            // TODO: 这里可以添加数据库操作，记录密码重置申请
            boolean success = true; // 模拟成功
            
            if (success) {
                return Message.success(ActionType.FORGET_PASSWORD, "密码重置申请已提交，请等待管理员审核");
            } else {
                return Message.failure(ActionType.FORGET_PASSWORD, "密码重置申请提交失败，请稍后重试");
            }
            
        } catch (Exception e) {
            System.err.println("处理忘记密码申请时发生异常: " + e.getMessage());
            return Message.failure(ActionType.FORGET_PASSWORD, "服务器内部错误");
        }
    }
}
