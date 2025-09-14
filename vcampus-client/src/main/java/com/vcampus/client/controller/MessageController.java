package com.vcampus.client.controller;

import com.vcampus.client.controller.userAdmin.ForgetPasswordTableViewController;
import com.vcampus.client.controller.userAdmin.UserCreateViewController;
import com.vcampus.client.controller.userAdmin.UserListViewController;
import com.vcampus.client.controller.userAdmin.UserPasswordResetViewController;
import com.vcampus.client.controller.shopAdmin.ProductManagementViewController;
import com.vcampus.client.controller.shopAdmin.ProductAddViewController;
import com.vcampus.client.controller.shopAdmin.ProductEditViewController;
import com.vcampus.client.controller.shopAdmin.OrderManagementViewController;
import com.vcampus.client.controller.shopAdmin.FavoriteManagementViewController;
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

    public void setLibraryController(LibraryController controller){this.libraryController=controller;}

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
                }break;
                case UPDATE_STUDENT:
                    if (studentController != null) {
                        studentController.handleUpdateStudentResponse(message);
                    } else {
                        System.err.println("StudentController未设置，无法处理学生信息获取响应");
                    }
                // --- 商店模块 ---
                case SHOP_GET_ALL_PRODUCTS:
                case SHOP_SEARCH_PRODUCTS: // 搜索和获取所有商品的响应，都由同一个方法处理(这里利用了一个很巧妙的穿透特性）
                    // 优先分发给商店管理员控制器，如果没有则分发给普通商店控制器
                    if (productManagementViewController != null) {
                        productManagementViewController.handleSearchProductsResponse(message);
                    } else if (shopController != null) {
                        shopController.handleProductListResponse(message);
                    } else {
                        System.err.println("路由警告：收到商品列表响应，但相关控制器未注册。");
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
                        System.err.println("路由警告：收到我的课程响应，但 MyTimetableController 未注册！");
                    }
                    break;

                    // --- 图书馆模块 ---
                case LIBRARY_GET_ALL_BOOKS:
                case LIBRARY_SEARCH_BOOKS: // 获取全部/搜索书籍，都返回书籍列表
                    if (libraryController != null) {
                        libraryController.handleBookListResponse(message);
                    } else {
                        System.err.println("路由警告：收到书籍列表响应，但LibraryController未注册。");
                    }
                    break;

                case LIBRARY_GET_MY_BORROWS: // 获取“我的借阅”
                    if (libraryController != null) {
                        libraryController.handleBorrowLogResponse(message);
                    } else {
                        System.err.println("路由警告：收到我的借阅响应，但LibraryController未注册。");
                    }
                    break;

                case LIBRARY_GET_ADMIN_BORROW_HISTORY: // 管理员获取“所有借阅记录”
                    if (libraryController != null) {
                        libraryController.handleBorrowLogResponse(message); // 也返回借阅记录，可复用同一个处理器
                    } else {
                        System.err.println("路由警告：收到借阅历史响应，但LibraryController未注册。");
                    }
                    break;

                case LIBRARY_GET_ALL_USERS_STATUS: // 管理员获取“所有人借阅情况”
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



                default:
                    System.out.println("未处理的消息类型: " + message.getAction());
                    break;
            }
            
        } catch (Exception e) {
            System.err.println("处理消息时发生错误: " + e.getMessage());
        }
    }
}
