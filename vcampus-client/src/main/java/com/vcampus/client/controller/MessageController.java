package com.vcampus.client.controller;

import com.vcampus.client.controller.libraryAdmin.BookCreateViewController;
import com.vcampus.client.controller.libraryAdmin.BookListViewController;
import com.vcampus.client.controller.libraryAdmin.BorrowLogCreateController;
import com.vcampus.client.controller.libraryAdmin.BorrowLogListViewController;
import com.vcampus.client.controller.shopAdmin.FavoriteManagementViewController;
import com.vcampus.client.controller.shopAdmin.OrderManagementViewController;
import com.vcampus.client.controller.shopAdmin.ProductAddViewController;
import com.vcampus.client.controller.shopAdmin.ProductEditViewController;
import com.vcampus.client.controller.shopAdmin.ProductManagementViewController;
import com.vcampus.client.controller.userAdmin.ForgetPasswordTableViewController;
import com.vcampus.client.controller.userAdmin.UserCreateViewController;
import com.vcampus.client.controller.userAdmin.UserListViewController;
import com.vcampus.client.controller.userAdmin.UserPasswordResetViewController;
import com.vcampus.common.dto.Message;

/**
 * 客户端消息控制器
 * 作为总控制器，根据消息类型实例化并调用各个子控制器
 * 编写人：谌宣羽
 */
public class MessageController {

    private LoginController loginController;
    private StudentController studentController;
    private LibraryController libraryController;
    private StudentAdminController studentadminController;
    private ShopController shopController;
    private ChangePasswordController changePasswordController;
    private UserListViewController userListViewController;
    private UserPasswordResetViewController userPasswordResetViewController;
    private UserCreateViewController userCreateViewController;
    private ForgetPasswordTableViewController forgetPasswordTableViewController;
    private AcademicController academicController; // ⭐ 新增 AcademicController 的引用
    private ProductManagementViewController productManagementViewController;
    private ProductAddViewController productAddViewController;
    private ProductEditViewController productEditViewController;
    private OrderManagementViewController orderManagementViewController;
    private FavoriteManagementViewController favoriteManagementViewController;
    private MyTimetableController myTimetableController; // ⭐ 新增
    private BookListViewController bookListViewController;
    private BookCreateViewController bookCreateViewController;
    private BorrowLogListViewController borrowLogListViewController;
    private BorrowLogCreateController borrowLogCreateController; // 【新增】引用
    /**
     * 设置LoginController实例（由UI层调用）
     * @param controller LoginController实例
     */
    public void setLoginController(LoginController controller) {
        this.loginController = controller;
    }
    public void setChangePasswordController(ChangePasswordController controller) {
        this.changePasswordController = controller;
    }
    public void setStudentController(StudentController controller){
        this.studentController=controller;
    }
    public void setStudentAdminController(StudentAdminController controller){this.studentadminController=controller;}


    /**
     * 【修正】注册 LibraryController 时，注销掉管理员的 BookListViewController。
     * 确保主图书馆视图能收到数据。
     */
    public void setLibraryController(LibraryController controller) {
        this.libraryController = controller;
        this.bookListViewController = null; // 关键：注销另一个控制器
        System.out.println("INFO: LibraryController 已注册, BookListViewController 已注销。");
    }

    public void setShopController(ShopController controller){this.shopController=controller;}
    // ⭐ 新增 AcademicController 的注册方法
    public void setAcademicController(AcademicController controller) {
        this.academicController = controller;
        System.out.println("INFO: AcademicController 已成功注册到 MessageController。");
    }
    public void setMyTimetableController(MyTimetableController controller) {
        this.myTimetableController = controller;
    }

    public void setUserListViewController(UserListViewController controller) {
        this.userListViewController = controller;
    }
    public void setUserPasswordResetViewController(UserPasswordResetViewController controller) {
        this.userPasswordResetViewController = controller;
    }
    public void setUserCreateViewController(UserCreateViewController controller) {
        this.userCreateViewController = controller;
    }
    public void setForgetPasswordTableViewController(ForgetPasswordTableViewController controller) {
        this.forgetPasswordTableViewController = controller;
    }
    public void setProductManagementViewController(ProductManagementViewController controller) {
        this.productManagementViewController = controller;
    }
    
    public ProductManagementViewController getProductManagementViewController() {
        return this.productManagementViewController;
    }
    public void setProductAddViewController(ProductAddViewController controller) {
        this.productAddViewController = controller;
    }
    public void setProductEditViewController(ProductEditViewController controller) {
        this.productEditViewController = controller;
    }
    public void setOrderManagementViewController(OrderManagementViewController controller) {
        this.orderManagementViewController = controller;
    }
    public void setFavoriteManagementViewController(FavoriteManagementViewController controller) {
        this.favoriteManagementViewController = controller;
    }
    /**
     * 【修正】注册 BookListViewController 时，注销掉学生/教师的 LibraryController。
     * 确保管理员图书列表视图能收到数据。
     */
    public void setBookListViewController(BookListViewController controller) {
        this.bookListViewController = controller;
        this.libraryController = null; // 关键：注销另一个控制器
        System.out.println("INFO: BookListViewController 已注册, LibraryController 已注销。");
    }
    // 【新增】添加 BookCreateViewController 的注册方法
    public void setBookCreateViewController(BookCreateViewController controller) {
        this.bookCreateViewController = controller;
    }
    public void setBorrowLogListViewController(BorrowLogListViewController controller) {
        this.borrowLogListViewController = controller;
    }
    // 【新增】注册 BorrowLogCreateController 的方法
    public void setBorrowLogCreateController(BorrowLogCreateController controller) {
        this.borrowLogCreateController = controller;
    }


    /**
     * 处理服务端消息
     * @param message 服务端发送的消息
     */

    //这个是回收处理服务端端消息
    public void handleMessage(Message message) {
        try {
            // 验证消息格式
            if (message == null || message.getAction() == null) {
                System.err.println("接收到无效的消息格式");
                return;
            }
            System.out.println("接收到消息: " + message.getAction() + " " + message.isStatus() + " " + message.getMessage());

            
            // 根据ActionType调用对应的子控制器
            //其实这个更像分发层，最先接受到服务器传来的消息，然后分配给各个Controller进行操作
            // 这里可以根据实际需求添加更多的case，哥哥controller根据消息进行处理，进而回传给终端进行改变
            switch (message.getAction()) {
                // --- 登录与密码模块 ---
                case LOGIN:
                    if (loginController != null) {
                        loginController.handleLoginResponse(message);
                    } else {
                        System.err.println("LoginController未设置，无法处理登录响应");
                    }
                    break;
                case FORGET_PASSWORD:
                    if (loginController != null) {
                        loginController.handleForgetPasswordResponse(message);
                    } else {
                        System.err.println("LoginController未设置，无法处理密码重置响应");
                    }
                    break;
                case CHANGE_PASSWORD:
                    if (changePasswordController != null) {
                        changePasswordController.handleChangePasswordResponse(message);
                    } else {
                        System.err.println("ChangePasswordController未设置，无法处理修改密码响应");
                    }
                    break;
                case SEARCH_USERS:
                    if (userListViewController != null) {
                        userListViewController.handleSearchUsersResponse(message);
                    } else {
                        System.err.println("UserListViewController未设置，无法处理搜索用户响应");
                    }
                    break;
                case DELETE_USER:
                    if (userListViewController != null) {
                        userListViewController.handleDeleteUserResponse(message);
                    } else {
                        System.err.println("UserListViewController未设置，无法处理删除用户响应");
                    }
                    break;
                case RESET_USER_PASSWORD:
                    if (userPasswordResetViewController != null) {
                        userPasswordResetViewController.handleResetUserPasswordResponse(message);
                    } else {
                        System.err.println("UserPasswordResetViewController未设置，无法处理重置用户密码响应");
                    }
                    break;
                case CREATE_USER:
                    if (userCreateViewController != null) {
                        userCreateViewController.handleCreateUserResponse(message);
                    } else {
                        System.err.println("UserCreateViewController未设置，无法处理创建用户响应");
                    }
                    break;
                case GET_FORGET_PASSWORD_TABLE:
                    if (forgetPasswordTableViewController != null) {
                        forgetPasswordTableViewController.handleGetForgetPasswordTableResponse(message);
                    } else {
                        System.err.println("ForgetPasswordTableViewController未设置，无法处理获取忘记密码申请响应");
                    }
                    break;
                case APPROVE_FORGET_PASSWORD_APPLICATION:
                    if (forgetPasswordTableViewController != null) {
                        forgetPasswordTableViewController.handleApproveForgetPasswordApplicationResponse(message);
                    } else {
                        System.err.println("ForgetPasswordTableViewController未设置，无法处理批准忘记密码申请响应");
                    }
                    break;
                case REJECT_FORGET_PASSWORD_APPLICATION:
                    if (forgetPasswordTableViewController != null) {
                        forgetPasswordTableViewController.handleRejectForgetPasswordApplicationResponse(message);
                    } else {
                        System.err.println("ForgetPasswordTableViewController未设置，无法处理拒绝忘记密码申请响应");
                    }
                    break;
                // --- 学生信息模块 ---
                case INFO_STUDENT:
                    if (studentController != null) {
                    studentController.handleStudentInfoResponse(message);
                } else {
                    System.err.println("StudentController未设置，无法处理学生信息获取响应");
                }
                break;
                case UPDATE_STUDENT:
                    if (studentController != null) {
                        studentController.handleUpdateStudentResponse(message);
                    } else {
                        System.err.println("StudentController未设置，无法处理学生信息获取响应");
                    }
                    break;
                case ALL_STUDENT:
                    if(studentadminController!=null){
                        studentadminController.handleAllStudentResponse(message);
                    }else{
                        System.err.println("StudentAdminController未设置，无法处理学生信息获取响应");
                    }
                    break;
                case SEARCH_STUDENT:
                    if(studentadminController!=null){
                        studentadminController.handleSearchStudentResponse(message);
                    }else{
                        System.err.println("StudentAdminController未设置，无法处理学生信息获取响应");
                    }
                    break;
                case INFO_STUDENT_ADMIN:
                    if(studentadminController!=null){
                        studentadminController.handleInfoStudentAdminResponse(message);
                    }else{
                        System.err.println("StudentAdminController未设置，无法处理学生信息获取响应");
                    }
                    break;
                case UPDATE_STUDENT_ADMIN:
                    if(studentadminController!=null){
                        studentadminController.handleUpdateStudentResponse(message);
                    }else{
                        System.err.println("StudentAdminController未设置，无法处理学生信息获取响应");
                    }
                    break;
                // --- 商店模块 ---
                case SHOP_GET_ALL_PRODUCTS:
                case SHOP_SEARCH_PRODUCTS: // 搜索和获取所有商品的响应，都由同一个方法处理(这里利用了一个很巧妙的穿透特性）
                    // 根据当前用户角色决定路由
                    com.vcampus.client.session.UserSession userSession = com.vcampus.client.session.UserSession.getInstance();
                    if (userSession.isLoggedIn() && userSession.getCurrentUserRole() != null) {
                        String roleDesc = userSession.getCurrentUserRole().getDesc();
                        if ("商店管理员".equals(roleDesc)) {
                            // 管理员角色，路由到管理员控制器
                            if (productManagementViewController != null) {
                                productManagementViewController.handleSearchProductsResponse(message);
                            } else {
                                System.err.println("路由警告：收到管理员商品列表响应，但ProductManagementViewController未注册。");
                            }
                        } else {
                            // 学生/教师角色，路由到学生控制器
                            if (shopController != null) {
                                shopController.handleProductListResponse(message);
                            } else {
                                System.err.println("路由警告：收到学生商品列表响应，但ShopController未注册。");
                            }
                        }
                    } else {
                        // 未登录或角色未知，优先分发给商店管理员控制器，如果没有则分发给普通商店控制器
                        if (productManagementViewController != null) {
                            productManagementViewController.handleSearchProductsResponse(message);
                        } else if (shopController != null) {
                            shopController.handleProductListResponse(message);
                        } else {
                            System.err.println("路由警告：收到商品列表响应，但相关控制器未注册。");
                        }
                    }
                    break;
                case SHOP_GET_MY_ORDERS:
                    if (shopController != null) {
                        shopController.handleGetMyOrdersResponse(message);
                    } else {
                        System.err.println("路由警告：收到订单列表响应，但ShopController未注册。");
                    }
                    break;

                case SHOP_GET_MY_FAVORITES:
                    if (shopController != null) {
                        shopController.handleGetMyFavoritesResponse(message);
                    } else {
                        System.err.println("路由警告：收到收藏列表响应，但ShopController未注册。");
                    }
                    break;
                case SHOP_GET_PRODUCT_DETAIL:
                    if (shopController != null) {
                        shopController.handleGetProductDetailResponse(message);
                    }else {
                        System.err.println("路由警告：收到商品详情响应，但ShopController未注册。");
                    }
                    break;
                case SHOP_CREATE_ORDER:
                    if (shopController != null) {
                        // 当收到创建订单的响应时，调用 ShopController 中对应的处理方法
                        shopController.handleCreateOrderResponse(message);
                    } else {
                        // 如果 ShopController 没有被注册，打印一个清晰的错误日志
                        System.err.println("路由警告：收到创建订单响应，但ShopController未注册。");
                    }
                    break;

                case SHOP_ADD_FAVORITE:
                    if (shopController != null) {
                        shopController.handleAddFavoriteResponse(message);
                    } else {
                        System.err.println("路由警告：收到添加收藏响应，但ShopController未注册。");
                    }
                    break;

                case SHOP_REMOVE_FAVORITE:
                    if (shopController != null) {
                        shopController.handleRemoveFavoriteResponse(message);
                    } else {
                        System.err.println("路由警告：收到取消收藏响应，但ShopController未注册。");
                    }
                    break;


                // 商店管理员相关响应处理
                case SHOP_ADMIN_ADD_PRODUCT:
                    if (productAddViewController != null) {
                        productAddViewController.handleAddProductResponse(message);
                    } else {
                        System.err.println("ProductAddViewController未设置，无法处理添加商品响应");
                    }
                    break;
                case SHOP_ADMIN_DELETE_PRODUCT:
                    if (productManagementViewController != null) {
                        productManagementViewController.handleDeleteProductResponse(message);
                    } else {
                        System.err.println("ProductManagementViewController未设置，无法处理删除商品响应");
                    }
                    break;
                case SHOP_ADMIN_UPDATE_PRODUCT:
                    if (productEditViewController != null) {
                        productEditViewController.handleUpdateProductResponse(message);
                    } else {
                        System.err.println("ProductEditViewController未设置，无法处理更新商品响应");
                    }
                    break;
                case SHOP_ADMIN_GET_ALL_ORDERS:
                    if (orderManagementViewController != null) {
                        orderManagementViewController.handleGetAllOrdersResponse(message);
                    } else {
                        System.err.println("OrderManagementViewController未设置，无法处理获取所有订单响应");
                    }
                    break;
                case SHOP_ADMIN_GET_ALL_FAVORITES:
                    if (favoriteManagementViewController != null) {
                        favoriteManagementViewController.handleGetAllFavoritesResponse(message);
                    } else {
                        System.err.println("FavoriteManagementViewController未设置，无法处理获取所有收藏响应");
                    }
                    break;
                case SHOP_ADMIN_GET_ORDERS_BY_USER:
                    if (orderManagementViewController != null) {
                        orderManagementViewController.handleGetAllOrdersResponse(message);
                    } else {
                        System.err.println("OrderManagementViewController未设置，无法处理根据用户ID获取订单响应");
                    }
                    break;
                case SHOP_ADMIN_GET_FAVORITES_BY_USER:
                    if (favoriteManagementViewController != null) {
                        favoriteManagementViewController.handleGetAllFavoritesResponse(message);
                    } else {
                        System.err.println("FavoriteManagementViewController未设置，无法处理根据用户ID获取收藏响应");
                    }
                    break;


                    //处理新增课程相关业务
                // --- ⭐ 新增：处理课程相关的响应 ---
                case GET_ALL_COURSES_RESPONSE:
                    if (academicController != null) {
                        academicController.handleGetAllCoursesResponse(message);
                    } else {
                        System.err.println("路由警告：收到课程列表响应，但 AcademicController 未注册！");
                    }
                    break;

                case SELECT_COURSE_RESPONSE:

                case DROP_COURSE_RESPONSE:
                    if (academicController != null) {
                        academicController.handleSelectOrDropCourseResponse(message);
                    } else {
                        System.err.println("路由警告：收到选/退课响应，但 AcademicController 未注册！");
                    }
                    break;

                case GET_MY_COURSES_RESPONSE:
                    if (myTimetableController != null) {
                        myTimetableController.handleMyCoursesResponse(message);
                    }else {
                        System.err.println("路由警告：收到我的课程响应，但 MyTimetableController 未注册！");
                    }
                    break;

                    // --- 图书馆模块 ---
                // --- 图书馆模块 ---
                case LIBRARY_GET_ALL_BOOKS:
                case LIBRARY_SEARCH_BOOKS:
                    // 【修正后】这个路由逻辑现在可以正确工作了
                    if (bookListViewController != null) {
                        bookListViewController.handleBookListResponse(message);
                    } else if (libraryController != null) {
                        libraryController.handleBookListResponse(message);
                    } else {
                        System.err.println("路由警告：收到书籍列表响应，但相关控制器均未注册。");
                    }
                    break;

                case LIBRARY_GET_MY_BORROWS:
                    // 用户的“我的借阅”仍然由 LibraryController 处理
                    if (libraryController != null) {
                        libraryController.handleBorrowLogResponse(message);
                    } else {
                        System.err.println("路由警告：收到我的借阅响应，但LibraryController未注册。");
                    }
                    break;
                case LIBRARY_GET_ADMIN_BORROW_HISTORY:
                case LIBRARY_SEARCH_HISTORY: // 搜索和获取所有记录，都由同一个方法处理
                    // 【修改】将管理员的借阅历史响应路由给新的控制器
                    if (borrowLogListViewController != null) {
                        borrowLogListViewController.handleBorrowLogListResponse(message);
                    } else if (libraryController != null) {
                        // 保留对旧控制器的兼容
                        libraryController.handleBorrowLogResponse(message);

                    } else {
                        System.err.println("路由警告：收到借阅记录响应，但相关控制器未注册。");
                    }
                    break;

                case LIBRARY_GET_ALL_USERS_STATUS:
                    if (libraryController != null) {
                        libraryController.handleUserStatusResponse(message);
                    } else {
                        System.err.println("路由警告：收到用户借阅情况响应，但LibraryController未注册。");
                    }
                    break;
                case LIBRARY_GET_BOOK_PDF:
                    if (libraryController != null) {
                        libraryController.handleGetBookPdfResponse(message);
                    } else {
                        System.err.println("路由警告：收到打开PDF文件响应，但LibraryController未注册。");
                    }
                    break;

                case LIBRARY_ADD_BOOK:
                    // 【修改】将 ADD_BOOK 的响应路由给 BookCreateViewController
                    if (bookCreateViewController != null) {
                        bookCreateViewController.handleCreateBookResponse(message);
                    } else if (bookListViewController != null) {
                        // 如果创建页面不存在，则将消息给列表页面刷新
                        bookListViewController.handleBookUpdateResponse(message);
                    } else {
                        System.err.println("路由警告：收到创建图书响应，但相关控制器未注册。");
                    }
                    break;
                case LIBRARY_DELETE_BOOK:
                case LIBRARY_MODIFY_BOOK:
                    if (bookListViewController != null) {
                        bookListViewController.handleBookUpdateResponse(message);
                    } else if (libraryController != null) {
                        libraryController.handleBookUpdateResponse(message);
                    }
                    else {
                        System.err.println("路由警告：收到书籍更新响应，但相关控制器未注册。");
                    }
                    break;
                // 【新增】为修改和归还（删除）操作添加路由
                case LIBRARY_UPDATE_BORROW_LOG:
                case LIBRARY_RETURN_BOOK:
                    if (borrowLogListViewController != null) {
                        borrowLogListViewController.handleBorrowLogUpdateResponse(message);
                    } else {
                        System.err.println("路由警告：收到借阅记录更新响应，但BorrowLogListViewController未注册。");
                    }
                    break;
                // --- 图书馆模块 ---
                // 【新增】为创建借阅记录操作添加路由
                case LIBRARY_CREATE_BORROW_LOG:
                    if (borrowLogCreateController != null) {
                        borrowLogCreateController.handleCreateBorrowLogResponse(message);
                    } else {
                        System.err.println("路由警告：收到创建借阅记录响应，但BorrowLogCreateController未注册。");
                    }
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
        }
    }
}