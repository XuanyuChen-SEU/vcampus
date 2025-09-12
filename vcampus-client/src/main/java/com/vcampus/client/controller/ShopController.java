package com.vcampus.client.controller;

import com.vcampus.client.service.ShopService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.OrderStatus;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ShopController implements IClientController{

    // --- FXML 控件变量 ---
    @FXML private TextField searchField;
    @FXML private TilePane productPane; // 用于展示商品和收藏
    @FXML private ScrollPane productScrollPane; // 包裹 TilePane 的滚动面板
    @FXML private TableView<ShopTransaction> orderTable; // 新增：用于展示订单的表格
    @FXML private StackPane centerStackPane; // 新增：用于切换视图的 StackPane
    @FXML private Label userInfoLabel;
    @FXML private Label balanceLabel;

    // --- 表格列定义 ---
    @FXML private TableColumn<ShopTransaction, String> orderIdColumn;
    @FXML private TableColumn<ShopTransaction, LocalDateTime> orderDateColumn;
    @FXML private TableColumn<ShopTransaction, Product> orderProductColumn;
    @FXML private TableColumn<ShopTransaction, Double> orderPriceColumn;
    @FXML private TableColumn<ShopTransaction, OrderStatus> orderStatusColumn;

    // --- 成员变量 ---
    private final ShopService shopService = new ShopService();
    private User currentUser;

    @FXML
    void initialize() {
        System.out.println("商店控制器初始化完成！");

        // 1. 配置UI相关的组件
        setupOrderTable();

        // 2. 将自己注册到消息中心，以便接收异步消息
        registerToMessageController();


    }

    @Override
    public void registerToMessageController() {
        // 通过自身的 Service 获取全局 MessageController
        com.vcampus.client.controller.MessageController messageController =
                shopService.getGlobalSocketClient().getMessageController();

        if (messageController != null) {
            messageController.setShopController(this);
            System.out.println("ShopController 已成功注册到 MessageController。");
        } else {
            System.err.println("严重错误：ShopController 注册失败，无法获取 MessageController 实例！");
        }
    }


    /**
     * 【重构后的方法，取代了之前的 load... 方法】
     * 这个方法由外部调用（比如 MainViewController 或 initialize 自身），
     * 用于设置当前用户，并触发初始数据的加载。
     * @param user 当前登录的用户对象
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();

        // 【关键逻辑】在设置完用户信息后，立即主动向服务器请求所有商品
        loadInitialProducts();
    }

    // ==========================================================
    // FXML 事件处理方法
    // ==========================================================

    @FXML
    void handleSearch(ActionEvent event) {
        switchToProductView(); // 确保显示的是商品视图
        String searchText = searchField.getText();
        System.out.println("用户正在搜索: " + searchText);
        Message response = shopService.searchProducts(searchText);
        if (response.isSuccess()) {
            updateProductDisplay((List<Product>) response.getData());
        } else {
            showError("搜索失败: " + response.getMessage());
        }
    }

    @FXML
    void handleShowMyOrders(ActionEvent event) {
        System.out.println("用户点击了“我的订单”");
        switchToOrderView(); // 切换到订单视图
        if (currentUser == null) { return; }

        Message response = shopService.getMyOrders(currentUser.getUserId());
        if (response.isSuccess()) {
            List<ShopTransaction> orders = (List<ShopTransaction>) response.getData();
            Platform.runLater(() -> orderTable.getItems().setAll(orders));
        } else {
            showError("获取订单失败: " + response.getMessage());
        }
    }

    @FXML
    void handleShowMyFavorites(ActionEvent event) {
        System.out.println("用户点击了“我的收藏”");
        switchToProductView(); // 确保显示的是商品/收藏视图
        if (currentUser == null) { return; }

        Message response = shopService.getMyFavorites(currentUser.getUserId());
        if (response.isSuccess()) {
            List<ShopTransaction> favorites = (List<ShopTransaction>) response.getData();
            List<Product> favoriteProducts = favorites.stream()
                    .map(ShopTransaction::getProduct)
                    .collect(Collectors.toList());
            updateProductDisplay(favoriteProducts);
        } else {
            showError("获取收藏失败: " + response.getMessage());
        }
    }

    // ==========================================================
    // 内部辅助方法
    // ==========================================================

    private void loadInitialProducts() {
        switchToProductView();
        Message response = shopService.getAllProducts();
        if (response.isSuccess()) {
            updateProductDisplay((List<Product>) response.getData());
        } else {
            showError("加载初始商品失败: " + response.getMessage());
        }
    }

    /**
     * 配置订单表格的列和数据如何显示
     */
    private void setupOrderTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        // 商品列是复杂的，需要自定义单元格的显示方式
        orderProductColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getProduct()));
        orderProductColumn.setCellFactory(column -> new TableCell<ShopTransaction, Product>() {
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();
            private final HBox contentBox = new HBox(10, imageView, nameLabel);

            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setGraphic(null);
                } else {
                    Image image = new Image(product.getImageUrl(), 50, 50, true, true, true);
                    imageView.setImage(image);
                    nameLabel.setText(product.getName());
                    setGraphic(contentBox);
                }
            }
        });
    }

    private void updateUserInfo() {
        Platform.runLater(() -> {
            if (currentUser != null) {
                userInfoLabel.setText("用户ID: " + currentUser.getUserId());
                balanceLabel.setText(String.format("余额: ¥ %.2f", currentUser.getBalance()));
            }
        });
    }

    private void updateProductDisplay(List<Product> products) {
        Platform.runLater(() -> {
            productPane.getChildren().clear();
            if (products == null || products.isEmpty()) {
                productPane.getChildren().add(new Label("没有找到任何商品。"));
                return;
            }
            for (Product product : products) {
                productPane.getChildren().add(createProductCard(product));
            }
        });
    }

    /**
     * 升级版：创建一个更接近淘宝风格的商品卡片
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #DDDDDD; -fx-border-radius: 5; -fx-background-color: #FFFFFF;");
        card.setPrefWidth(180);

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(product.getImageUrl(), 160, 160, true, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(new Image("https://via.placeholder.com/160", true));
        }

        Label nameLabel = new Label(product.getName());
        nameLabel.setWrapText(true);
        nameLabel.setPrefHeight(40); // 给名称两行的高度

        HBox priceBox = new HBox(5);
        Label currencyLabel = new Label("¥");
        currencyLabel.setTextFill(Color.RED);
        Label priceLabel = new Label(String.valueOf(product.getPrice()));
        priceLabel.setTextFill(Color.RED);
        priceLabel.setFont(new Font("System Bold", 16));
        priceBox.getChildren().addAll(currencyLabel, priceLabel);

        Button actionButton = new Button("查看详情");
        actionButton.setOnAction(event -> {
            // TODO: 实现查看商品详情的逻辑
            System.out.println("查看详情: " + product.getName());
        });

        card.getChildren().addAll(imageView, nameLabel, priceBox, actionButton);
        return card;
    }

    private void switchToProductView() {
        Platform.runLater(() -> {
            productScrollPane.setVisible(true);
            orderTable.setVisible(false);
        });
    }

    private void switchToOrderView() {
        Platform.runLater(() -> {
            productScrollPane.setVisible(false);
            orderTable.setVisible(true);
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("操作失败");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * 【新增方法，作为异步入口】
     * 处理所有返回“商品列表”的异步响应（包括获取全部商品和搜索商品）。
     */
    public void handleProductListResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步商品列表响应。");
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                updateProductDisplay((List<Product>) message.getData());
                switchToProductView();
            } else {
                showError("（异步）获取商品列表失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增方法，作为异步入口】
     * 处理“获取我的订单”的异步响应。
     */
    public void handleGetMyOrdersResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步订单列表响应。");
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                List<ShopTransaction> orders = (List<ShopTransaction>) message.getData();
                orderTable.getItems().setAll(orders);
                switchToOrderView();
            } else {
                showError("（异步）获取订单失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增方法，作为异步入口】
     * 处理“获取我的收藏”的异步响应。
     */
    public void handleGetMyFavoritesResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步收藏列表响应。");
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                List<ShopTransaction> favorites = (List<ShopTransaction>) message.getData();
                List<Product> favoriteProducts = favorites.stream()
                        .map(ShopTransaction::getProduct)
                        .collect(Collectors.toList());
                updateProductDisplay(favoriteProducts);
                switchToProductView();
            } else {
                showError("（异步）获取收藏失败: " + message.getMessage());
            }
        });
    }

}