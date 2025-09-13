package com.vcampus.server.service;
import java.util.List;

import com.vcampus.common.dto.ChangePassword;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.PasswordResetApplication;
import com.vcampus.common.dto.User;
import com.vcampus.common.dto.UserSearch;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.Role;
import com.vcampus.server.dao.impl.PasswordResetApplicationDao;
import com.vcampus.server.dao.impl.UserDao;

/**
 * 用户服务类
 * 负责用户相关的业务逻辑，包括登录、忘记密码、重置密码等
 * 编写人：谌宣羽
 */
public class UserService {
    private final UserDao userDao;
    private final PasswordResetApplicationDao passwordResetApplicationDao;

    public UserService()  {
        userDao = new UserDao();
        passwordResetApplicationDao = new PasswordResetApplicationDao();
    }

    /**
     * 验证用户登录
     * @param loginUser 登录用户信息
     * @return 登录结果，包含成功/失败信息和用户数据
     */
    public Message validateLogin(User loginUser) {
        try {

            User user = userDao.getUserById(loginUser.getUserId());

            if (user==null||user.getUserId()==null||user.getUserId().equals("")) {
                return Message.failure(ActionType.LOGIN, "用户不存在");
            }
            if (!user.getPassword().trim().equals(loginUser.getPassword().trim())) {
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
            if (user == null || user.getUserId() == null || user.getUserId().equals("")) {
                return Message.failure(ActionType.FORGET_PASSWORD, "用户不存在");
            }
            
            // 验证用户是否存在
            User existingUser = userDao.getUserById(user.getUserId());
            if (existingUser == null) {
                return Message.failure(ActionType.FORGET_PASSWORD, "用户不存在");
            }

            // 验证用户是否曾经提交过申请
            PasswordResetApplication oldApplication = passwordResetApplicationDao.selectById(user.getUserId());
            PasswordResetApplication newApplication = new PasswordResetApplication(user.getUserId(), user.getPassword());
            if (oldApplication != null) {
                passwordResetApplicationDao.update(newApplication);
            } else {
                passwordResetApplicationDao.add(newApplication);
            }
            
            return Message.success(ActionType.FORGET_PASSWORD, "密码重置申请已提交，请等待管理员审核");
        } catch (Exception e) {
            System.err.println("处理忘记密码申请时发生异常: " + e.getMessage());
            return Message.failure(ActionType.FORGET_PASSWORD, "服务器内部错误");
        }
    }

    /*
     * 
     */

    public Message handleChangePassword(ChangePassword changePassword) {
        try {
        User user = userDao.getUserById(changePassword.getUserId());
        if (user == null || user.getUserId() == null || user.getUserId().equals("")) {
            return Message.failure(ActionType.CHANGE_PASSWORD, "用户不存在");
        }
        if (!user.getPassword().equals(changePassword.getOldPassword())) {
            return Message.failure(ActionType.CHANGE_PASSWORD, "原密码错误");
        }
        user.setPassword(changePassword.getNewPassword());
        userDao.updateUser(user);
            return Message.success(ActionType.CHANGE_PASSWORD, "密码修改成功");
        } catch (Exception e) {
            System.err.println("处理修改密码申请时发生异常: " + e.getMessage());
            return Message.failure(ActionType.CHANGE_PASSWORD, "服务器内部错误");
        }
    }

    /**
     * 搜索用户
     * @param userSearch 搜索关键词
     * @return 搜索结果
     */
    public Message searchUsers(UserSearch userSearch) {
        try {
            List<User> users = userDao.searchUsers(userSearch.getSearchText(), userSearch.getSelectedRole());
            return Message.success(ActionType.SEARCH_USERS, users, "搜索完成，找到 " + users.size() + " 个用户");
        } catch (Exception e) {
            System.err.println("搜索用户时发生异常: " + e.getMessage());
            return Message.failure(ActionType.SEARCH_USERS, "服务器内部错误");
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    public Message deleteUser(String userId) {
        try {
            boolean success = userDao.deleteUser(userId);
            if (success) {
                return Message.success(ActionType.DELETE_USER, userId, "用户删除成功");
            } else {
                return Message.failure(ActionType.DELETE_USER, "用户删除失败");
            }
        } catch (Exception e) {
            System.err.println("删除用户时发生异常: " + e.getMessage());
            return Message.failure(ActionType.DELETE_USER, "服务器内部错误");
        }
    }

    /**
     * 重置用户密码
     * @param user 用户ID
     * @return 重置结果
     */
    public Message resetUserPassword(User user) {
        try {
            if (!userDao.isUserIdExists(user.getUserId())) {
                return Message.failure(ActionType.RESET_USER_PASSWORD, "用户不存在");
            }
            boolean success = userDao.resetUserPassword(user);
            if (success) {
                return Message.success(ActionType.RESET_USER_PASSWORD, "密码重置成功");
            } else {
                return Message.failure(ActionType.RESET_USER_PASSWORD, "密码重置失败");
            }
        } catch (Exception e) {
            System.err.println("重置用户密码时发生异常: " + e.getMessage());
            return Message.failure(ActionType.RESET_USER_PASSWORD, "服务器内部错误");
        }
    }

    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建结果
     */
    public Message createUser(User user) {
        try {
            // 检查用户ID是否已存在
            if (userDao.isUserIdExists(user.getUserId())) {
                return Message.failure(ActionType.CREATE_USER, "用户ID已存在");
            }
            
            boolean success = userDao.createUser(user);
            if (success) {
                return Message.success(ActionType.CREATE_USER, "用户创建成功");
            } else {
                return Message.failure(ActionType.CREATE_USER, "用户创建失败");
            }
        } catch (Exception e) {
            System.err.println("创建用户时发生异常: " + e.getMessage());
            return Message.failure(ActionType.CREATE_USER, "服务器内部错误");
        }
    }

    /*
     * 处理获取忘记密码申请请求
     * @return 获取忘记密码申请响应消息
     */
    public Message getForgetPasswordTable() {
        try {
            return Message.success(ActionType.GET_FORGET_PASSWORD_TABLE, passwordResetApplicationDao.selectAll(), "获取忘记密码申请成功");
        } catch (Exception e) {
            System.err.println("处理获取忘记密码申请请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.GET_FORGET_PASSWORD_TABLE, "服务器内部错误");
        }
    }

    /*
     * 处理批准忘记密码申请请求
     * @param userId 用户ID
     * @return 批准忘记密码申请响应消息
     */
    public Message approveForgetPasswordApplication(String userId) {
        try {
            PasswordResetApplication passwordResetApplication = passwordResetApplicationDao.selectById(userId);
            User user = new User(passwordResetApplication.getUserId(), passwordResetApplication.getNewPassword());
            userDao.updateUser(user);
            passwordResetApplicationDao.deleteById(userId);
            return Message.success(ActionType.APPROVE_FORGET_PASSWORD_APPLICATION, "密码重置申请批准成功");
        } catch (Exception e) {
            System.err.println("处理批准忘记密码申请请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.APPROVE_FORGET_PASSWORD_APPLICATION, "服务器内部错误");
        }
    }

    /*
     * 处理拒绝忘记密码申请请求
     * @param userId 用户ID
     * @return 拒绝忘记密码申请响应消息
     */
    public Message rejectForgetPasswordApplication(String userId) {
        try {
            passwordResetApplicationDao.deleteById(userId);
            return Message.success(ActionType.REJECT_FORGET_PASSWORD_APPLICATION, "密码重置申请拒绝成功");
        } catch (Exception e) {
            System.err.println("处理拒绝忘记密码申请请求时发生错误: " + e.getMessage());
            return Message.failure(ActionType.REJECT_FORGET_PASSWORD_APPLICATION, "服务器内部错误");
        }
    }       

}
