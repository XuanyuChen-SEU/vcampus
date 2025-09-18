package com.vcampus.server.controller;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
/**
 消息控制器
 负责消息路由和参数验证
 编写人：谌宣羽
 */
public class MessageController {
    private final UserController userController;
    private final StudentController studentController;
    private final StudentAdminController studentadminController;
    private final CourseController courseController; // 来自远程的修改
    private final ShopController shopController;     // 来自您的修改
    private final LibraryController libraryController; // 【新增】图书馆控制器实例
    private final EmailController emailController;   // 【新增】邮件控制器实例
    // --- 2. 合并构造函数 ---
    // 在构造函数中，我们需要实例化所有的控制器



    public MessageController() {
        this.userController = new UserController();
        this.studentController = new StudentController();
        this.studentadminController=new StudentAdminController();
        this.courseController = new CourseController(); // 保留
        this.shopController = new ShopController();     // 保留
        this.libraryController = new LibraryController(); // 【新增】在构造时实例化
        this.emailController = new EmailController();   // 【新增】在构造时实例化邮件控制器
    }

    /**
     * 处理客户端消息
     *
     * @param request 客户端请求消息
     * @return 服务端响应消息
     */
    public Message handleMessage(Message request) {
        try {
            // 验证消息格式
            if (request == null || request.getAction() == null) {
                return Message.failure(ActionType.LOGIN, "无效的消息格式");
            }
            // 根据ActionType调用对应的控制器
            switch (request.getAction()) {//需要什么服务  自己加上）
                // --- 用户登录相关 ---

                case LOGIN:
                    return userController.handleLogin(request);
                case FORGET_PASSWORD:
                    return userController.handleForgetPassword(request);
                case CHANGE_PASSWORD:
                    return userController.handleChangePassword(request);


                // --- 用户管理员相关 ---
                case SEARCH_USERS:
                    return userController.handleSearchUsers(request);
                case DELETE_USER:
                    return userController.handleDeleteUser(request);
                case RESET_USER_PASSWORD:
                    return userController.handleResetUserPassword(request);
                case CREATE_USER:
                    return userController.handleCreateUser(request);
                case GET_FORGET_PASSWORD_TABLE:
                    return userController.handleGetForgetPasswordTable(request);
                case APPROVE_FORGET_PASSWORD_APPLICATION:
                    return userController.handleApproveForgetPasswordApplication(request);
                case REJECT_FORGET_PASSWORD_APPLICATION:
                    return userController.handleRejectForgetPasswordApplication(request);

                // --- 学籍相关 ---
                case INFO_STUDENT:
                    return studentController.handle(request);
                case UPDATE_STUDENT:
                    return studentController.updateStudent(request);
                case STUDENT_STATUS_APPLICATION:
                    return studentController.handleStudentStatusApplication(request);
                case REVOKE_APPLICATION:
                    return studentController.handleRevokeApplication(request);

                // --- 学籍管理员相关 ---
                case ALL_STUDENT:
                    return studentadminController.getAllStudents(request);
                case SEARCH_STUDENT:
                    return studentadminController.searchStudents(request);
                case INFO_STUDENT_ADMIN:
                    return studentadminController.getStudentById(request);
                case UPDATE_STUDENT_ADMIN:
                    return studentadminController.updateStudent(request);
                case UPDATE_STUDENTS:
                    return studentadminController.updateStudents(request);
                case GET_ALL_APPLICATIONS:
                    return studentadminController.getAllApplications(request);
                case UPDATE_APPLICATION_STATUS:
                    return studentadminController.updateApplicationStatus(request);
                case ALL_TEACHER:
                    return studentadminController.getAllTeachers(request);

                // --- 教师相关 ---
                case INFO_TEACHER:
                    return studentController.handleInfoTeacher(request);
                // --- 课程相关 ---

                // --- 课程相关 ---调用服务端的controller层相关逻辑部分

                case GET_ALL_COURSES:
                    return courseController.handleGetAllCourses(request);
                case SELECT_COURSE:
                    return courseController.handleSelectCourse(request);
                case DROP_COURSE:
                    return courseController.handleDropCourse(request);
                case GET_MY_COURSES:
                    return courseController.handleGetMyCourses(request);
// 在服务端的 MessageController.java 的 switch 语句中添加:
                case SEARCH_COURSES:
                    return courseController.handleSearchCourses(request);

                // --- ⭐ 新增：路由所有教务管理员相关的请求 ---
                case ADMIN_GET_ALL_COURSES:
                    return courseController.handleGetAllCoursesAdmin(request);
                case ADMIN_ADD_COURSE:
                    return courseController.handleAddCourse(request);
                case ADMIN_MODIFY_COURSE:
                    return courseController.handleModifyCourse(request);
                case ADMIN_DELETE_COURSE:
                    return courseController.handleDeleteCourse(request);
                case ADMIN_ADD_SESSION:
                    return courseController.handleAddSession(request);
                case ADMIN_MODIFY_SESSION:
                    return courseController.handleModifySession(request);
                case ADMIN_DELETE_SESSION:
                    return courseController.handleDeleteSession(request);

                // ⭐ 新增路由
                case ADMIN_SEARCH_COURSES:
                    return courseController.handleAdminSearchCourses(request);

                // --- 商店相关 ---
                case SHOP_GET_ALL_PRODUCTS:
                    return shopController.handleGetAllProducts(request);
                case SHOP_SEARCH_PRODUCTS:
                    return shopController.handleSearchProducts(request);
                case SHOP_GET_MY_ORDERS:
                    return shopController.handleGetMyOrders(request);
                case SHOP_GET_MY_FAVORITES:
                    return shopController.handleGetMyFavorites(request);
                case SHOP_GET_PRODUCT_DETAIL:
                    return shopController.handleGetProductDetail(request);
                case SHOP_GET_BALANCE:
                    return shopController.handleGetBalance(request);
                case SHOP_PAY_FOR_ORDER:
                    return shopController.handlePayForOrder(request); // <-- 确保 (message) 在这里

                case SHOP_RECHARGE:
                    return shopController.handleRecharge(request);
                // --- 【新增】路由删除和支付的请求 ---
                case SHOP_DELETE_ORDER:
                    return shopController.handleDeleteOrder(request);

                case SHOP_PAY_FOR_UNPAID_ORDER:
                    return shopController.handlePayForUnpaidOrder(request);
                // --- 新增结束 ---


                // --- 商店管理员相关 ---
                case SHOP_ADMIN_ADD_PRODUCT:
                    return shopController.handleAddProduct(request);
                case SHOP_ADMIN_UPDATE_PRODUCT:
                    return shopController.handleUpdateProduct(request);
                case SHOP_ADMIN_DELETE_PRODUCT:
                    return shopController.handleDeleteProduct(request);
                case SHOP_ADMIN_GET_ALL_ORDERS:
                    return shopController.handleGetAllOrders(request);
                case SHOP_ADMIN_GET_ALL_FAVORITES:
                    return shopController.handleGetAllFavorites(request);
                case SHOP_ADMIN_GET_ORDERS_BY_USER:
                    return shopController.handleGetOrdersByUser(request);
                case SHOP_ADMIN_GET_FAVORITES_BY_USER:
                    return shopController.handleGetFavoritesByUser(request);
                // --- 图书馆相关 ---
                case LIBRARY_BORROW_BOOK:
                case LIBRARY_GET_ALL_BOOKS:
                case LIBRARY_SEARCH_BOOKS:
                case LIBRARY_GET_MY_BORROWS:
                case LIBRARY_GET_ADMIN_BORROW_HISTORY:
                case LIBRARY_GET_ALL_USERS_STATUS:
                case LIBRARY_RENEW_ALL:
                case LIBRARY_SEARCH_MY_BORROWS:
                case LIBRARY_ADD_BOOK:
                case LIBRARY_DELETE_BOOK:
                case LIBRARY_MODIFY_BOOK:
                case LIBRARY_SEARCH_HISTORY:
                case LIBRARY_SEARCH_USERS:
                case LIBRARY_RETURN_BOOK:
                case LIBRARY_GET_BOOK_PDF:
                case LIBRARY_CREATE_BORROW_LOG:
                    return libraryController.dispatch(request);

                case SHOP_CREATE_ORDER:
                    return shopController.handleCreateOrder(request);

                case SHOP_ADD_FAVORITE:
                    return shopController.handleAddFavorite(request);

                case SHOP_REMOVE_FAVORITE:
                    return shopController.handleRemoveFavorite(request);

                // --- 邮件系统相关 ---
                case EMAIL_SEND:
                case EMAIL_SAVE_DRAFT:
                case EMAIL_GET_INBOX:
                case EMAIL_GET_SENT:
                case EMAIL_GET_DRAFT:
                case EMAIL_READ:
                case EMAIL_DELETE:
                case EMAIL_MARK_READ:
                case EMAIL_MARK_UNREAD:
                case EMAIL_SEARCH:
                case EMAIL_BATCH_MARK_READ:
                case EMAIL_BATCH_DELETE:
                case EMAIL_ADMIN_GET_ALL:
                case EMAIL_ADMIN_SEARCH_ALL:
                case EMAIL_ADMIN_SEARCH_BY_USER:
                case EMAIL_ADMIN_GET_USER_EMAILS:
                case EMAIL_ADMIN_DELETE:
                case EMAIL_ADMIN_GET_STATISTICS:
                    return emailController.handleRequest(request);

                // --- 添加结束 ---

                default:
                    return Message.failure(request.getAction(), "不支持的操作类型: " + request.getAction());
            }
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
            return Message.failure(request.getAction(), "服务器内部错误");
        }
    }
}