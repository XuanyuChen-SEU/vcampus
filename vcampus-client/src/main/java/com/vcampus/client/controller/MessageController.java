package com.vcampus.client.controller;

import com.vcampus.client.controller.ChangePasswordController;
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
                    if (shopController != null) {
                        shopController.handleProductListResponse(message);
                    } else {
                        System.err.println("路由警告：收到商品列表响应，但ShopController未注册。");
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
