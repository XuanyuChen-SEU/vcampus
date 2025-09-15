package com.vcampus.client.controller;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * 商店管理员框架控制器
 * 负责导航栏和子视图管理
 * 编写人：AI Assistant
 */
public class ShopAdminViewController {

    // 导航按钮
    @FXML
    private Button productManagementButton;
    
    @FXML
    private Button orderManagementButton;
    
    @FXML
    private Button favoriteManagementButton;
    
    // 子视图容器
    @FXML
    private AnchorPane subViewContainer;
    
    // 当前选中的视图
    private String currentView = "productManagement";

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 默认加载商品管理视图
        loadProductManagementView();
        updateButtonStyles();
    }
    
    /**
     * 处理商品管理按钮点击
     */
    @FXML
    private void handleProductManagement(ActionEvent event) {
        currentView = "productManagement";
        loadProductManagementView();
        updateButtonStyles();
    }
    
    /**
     * 处理订单管理按钮点击
     */
    @FXML
    private void handleOrderManagement(ActionEvent event) {
        currentView = "orderManagement";
        loadOrderManagementView();
        updateButtonStyles();
    }
    
    /**
     * 处理收藏管理按钮点击
     */
    @FXML
    private void handleFavoriteManagement(ActionEvent event) {
        currentView = "favoriteManagement";
        loadFavoriteManagementView();
        updateButtonStyles();
    }
    
    /**
     * 加载商品管理视图
     */
    private void loadProductManagementView() {
        loadSubView("/fxml/admin/shop/ProductManagementView.fxml", null);
    }
    
    /**
     * 加载订单管理视图
     */
    private void loadOrderManagementView() {
        loadSubView("/fxml/admin/shop/OrderManagementView.fxml", null);
    }
    
    /**
     * 加载添加商品视图
     */
    private void loadProductAddView() {
        loadSubView("/fxml/admin/shop/ProductAddView.fxml", null);
    }
    
    /**
     * 加载收藏管理视图
     */
    private void loadFavoriteManagementView() {
        loadSubView("/fxml/admin/shop/FavoriteManagementView.fxml", null);
    }
    
    /**
     * 加载子视图（带参数）
     */
    public void loadSubView(String fxmlPath, String productId) {
        loadSubView(fxmlPath, productId, null);
    }
    
    /**
     * 加载子视图（带商品对象）
     */
    public void loadSubView(String fxmlPath, String productId, com.vcampus.common.dto.Product product) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("错误: 找不到视图文件 " + fxmlPath);
                return;
            }
            
            // 加载FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Node view = loader.load();
            
            // 设置控制器引用，以便子控制器能够找到父控制器
            Object controller = loader.getController();
            if (controller != null) {
                // 将父控制器引用存储到子视图的userData中
                view.setUserData(this);
            }
            
            // 如果是商品编辑视图且有商品ID参数，则设置商品ID
            if (fxmlPath.contains("ProductEditView") && productId != null) {
                if (controller instanceof com.vcampus.client.controller.shopAdmin.ProductEditViewController) {
                    com.vcampus.client.controller.shopAdmin.ProductEditViewController editController = 
                        (com.vcampus.client.controller.shopAdmin.ProductEditViewController) controller;
                    
                    // 注册到MessageController
                    registerControllerToMessageController(editController);
                    
                    if (product != null) {
                        // 如果有完整的商品对象，直接设置
                        editController.setProduct(product);
                    } else {
                        // 否则只设置商品ID，让控制器自己去获取
                        editController.setProductId(productId);
                    }
                }
            }
            
            // 设置锚点约束，让子视图占满整个容器
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            
            // 将加载好的视图设置为子视图容器的唯一子节点
            subViewContainer.getChildren().setAll(view);
            
        } catch (IOException e) {
            System.err.println("加载子视图时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 设置当前视图状态
     */
    public void setCurrentView(String viewName) {
        currentView = viewName;
        updateButtonStyles();
    }
    
    /**
     * 更新按钮样式
     */
    private void updateButtonStyles() {
        // 重置所有按钮样式
        productManagementButton.getStyleClass().removeAll("nav-button-active");
        orderManagementButton.getStyleClass().removeAll("nav-button-active");
        favoriteManagementButton.getStyleClass().removeAll("nav-button-active");
        
        // 为当前选中的按钮添加激活样式
        switch (currentView) {
            case "productManagement":
                productManagementButton.getStyleClass().add("nav-button-active");
                break;
            case "orderManagement":
                orderManagementButton.getStyleClass().add("nav-button-active");
                break;
            case "favoriteManagement":
                favoriteManagementButton.getStyleClass().add("nav-button-active");
                break;
        }
    }
    
    /**
     * 注册控制器到MessageController
     */
    private void registerControllerToMessageController(Object controller) {
        try {
            // 获取SocketClient实例
            com.vcampus.client.net.SocketClient socketClient = com.vcampus.client.MainApp.getGlobalSocketClient();
            if (socketClient != null) {
                com.vcampus.client.controller.MessageController messageController = socketClient.getMessageController();
                if (messageController != null) {
                    // 根据控制器类型注册
                    if (controller instanceof com.vcampus.client.controller.shopAdmin.ProductEditViewController) {
                        messageController.setProductEditViewController((com.vcampus.client.controller.shopAdmin.ProductEditViewController) controller);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("注册控制器到MessageController失败: " + e.getMessage());
        }
    }
}
