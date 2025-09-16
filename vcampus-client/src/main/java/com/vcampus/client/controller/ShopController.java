package com.vcampus.client.controller;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.vcampus.client.MainApp;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.ButtonBar;

public class ShopController implements IClientController{

    // --- FXML 控件变量 ---
    @FXML private TextField searchField;
    @FXML private TilePane productPane; // 用于展示商品和收藏
    @FXML private ScrollPane productScrollPane; // 包裹 TilePane 的滚动面板
    @FXML private TableView<ShopTransaction> orderTable; // 新增：用于展示订单的表格
    @FXML private StackPane centerStackPane; // 新增：用于切换视图的 StackPane

    // --- 表格列定义 ---
    @FXML private TableColumn<ShopTransaction, String> orderIdColumn;
    @FXML private TableColumn<ShopTransaction, LocalDateTime> orderDateColumn;
    @FXML private TableColumn<ShopTransaction, Product> orderProductColumn;
    @FXML private TableColumn<ShopTransaction, Double> orderPriceColumn;
    @FXML private TableColumn<ShopTransaction, OrderStatus> orderStatusColumn;


    // --- 成员变量 ---
    private final ShopService shopService = new ShopService();
    private User currentUser;
    private boolean isViewingFavorites = false; // 【新增】状态标志位
    private List<ShopTransaction> currentFavoritesList; // 【新增】用于存储当前用户的收藏列表

    @FXML
    void initialize() {
        System.out.println("商店控制器初始化完成！");
        currentUser = new User(MainApp.getGlobalUserSession().getCurrentUserId(),"");        // 1. 配置UI相关的组件
        setupOrderTable();

        // 2. 将自己注册到消息中心，以便接收异步消息
        registerToMessageController();
        loadInitialProducts();
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
     * 【已修正】处理“搜索”按钮的点击事件 (纯同步模型)
     */
    /**
     * 【异步模型】处理“搜索”按钮的点击事件。
     * 这个方法只负责发送请求，不等待响应，以确保UI流畅。
     */
    @FXML
    void handleSearch(ActionEvent event) {
        // 1. 立即设置好UI状态和视图
        this.isViewingFavorites = false;
        switchToProductView(); // 确保显示的是商品网格

        String searchText = searchField.getText();
        System.out.println("客户端：异步发送搜索请求 - \"" + searchText + "\"");

        // 2. 调用Service层【异步发送】请求，方法立刻返回，不阻塞UI
        shopService.searchProducts(searchText);
    }
    /**
     * 【异步模型】处理所有返回“商品列表”的异步响应。
     * 这包括处理“获取全部商品”和“搜索商品”的响应。
     * @param message 服务器返回的响应消息
     */
    public void handleProductListResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步商品列表响应。");

        // 【关键】所有UI更新都必须在 JavaFX Application Thread 中执行
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                // 1. 从消息中安全地解析出商品列表
                List<Product> products = (List<Product>) message.getData();
                // 2. 调用UI更新方法
                updateProductDisplay(products);
            } else {
                // 3. 如果失败，显示错误信息
                showError("获取商品列表失败: " + message.getMessage());
            }
        });
    }


    /**
     * 【异步模型】处理“我的收藏”按钮的点击事件。
     * 只负责发送请求，不等待响应。
     */
    @FXML
    void handleShowMyFavorites(ActionEvent event) {
        System.out.println("用户点击了“我的收藏”");
        this.isViewingFavorites = true;
        switchToProductView(); // 立即切换视图

        if (currentUser == null) {
            showError("用户未登录！");
            return;
        }

        System.out.println("客户端：异步发送获取收藏请求...");
        shopService.getMyFavorites(currentUser.getUserId()); // 只发送，不等待
    }

    /**
     * 【异步模型】处理“获取我的收藏”的异步响应。
     * 由 MessageController 调用。
     * @param message 服务器返回的响应消息
     */
    public void handleGetMyFavoritesResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步收藏列表响应。");
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                // 【关键】将完整的收藏列表保存下来，以支持“取消收藏”
                this.currentFavoritesList = (List<ShopTransaction>) message.getData();

                List<Product> favoriteProducts = this.currentFavoritesList.stream()
                        .map(ShopTransaction::getProduct)
                        .collect(Collectors.toList());
                updateProductDisplay(favoriteProducts);
            } else {
                showError("（异步）获取收藏失败: " + message.getMessage());
            }
        });
    }


    // 这是“懂得授权的经理”
    @FXML
    void handleShowMyOrders(ActionEvent event) {
        // 1. 经理只需要下达一个指令（发起请求）
        isViewingFavorites = false;
        switchToOrderView(); // 先把办公室准备好
        shopService.getMyOrders(currentUser.getUserId());

        // 2. 经理立刻就去做别的事了（方法结束，UI保持流畅）
    }

    // -------------------------------------------------------------
// 这是经理的“助理”：handleGetMyOrdersResponse
    public void handleGetMyOrdersResponse(Message message) {
        // 3. “助理”在后台等文件送达（MessageController调用）
        // 4. 文件送到后，“助理”负责整理并展示（处理响应并更新UI）
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                List<ShopTransaction> orders = (List<ShopTransaction>) message.getData();
                orderTable.getItems().setAll(orders);
            } // ...
        });
    }

    // ==========================================================
    // 内部辅助方法
    // ==========================================================

    private void loadInitialProducts() {
        this.isViewingFavorites = false;
        switchToProductView();
        System.out.println("客户端：异步发送初始商品加载请求...");
        shopService.getAllProducts(); // 只发送，不等待
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
        actionButton.setOnAction(event -> handleViewDetails(product)); // 修改这里

        card.getChildren().addAll(imageView, nameLabel, priceBox, actionButton);
        return card;
    }

    // --- 请将这个完整的方法添加到你的 ShopController.java 中 ---

    // --- 这是修改后的 handleViewDetails ---
    private void handleViewDetails(Product product) {
        System.out.println("用户请求查看商品详情: " + product.getName());
        // 【核心修正】只调用 service 发送请求，不接收返回值
        shopService.getProductDetail(String.valueOf(product.getId()));
    }


    /**
     * 【最终版】处理“获取商品详情”的异步响应。
     * 这个方法会被 MessageController 在后台线程中调用，
     * 它负责创建并显示一个功能丰富的商品详情对话框。
     */
    public void handleGetProductDetailResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步商品详情响应。");

        // 【关键】所有UI更新都必须在 JavaFX Application Thread 中执行
        Platform.runLater(() -> {
            if (!message.isSuccess() || !(message.getData() instanceof Product)) {
                showError("（异步）获取详情失败: " + message.getMessage());
                return; // 如果失败或数据类型不对，直接返回
            }

            Product product = (Product) message.getData();

            // --- 创建和显示对话框的逻辑 ---
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("商品详情");
            dialog.setHeaderText(product.getName());

            // --- 设置对话框内容 (GridPane) ---
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            ImageView imageView = new ImageView(new Image(product.getImageUrl(), 200, 200, true, true, true));
            grid.add(imageView, 0, 0, 1, 3);
            grid.add(new Label(String.format("价格: ¥ %.2f", product.getPrice())), 1, 0);
            grid.add(new Label("库存: " + product.getStock() + " 件"), 1, 1);
            grid.add(new Label("描述:"), 0, 3);
            Label descLabel = new Label(product.getDescription());
            descLabel.setWrapText(true);
            grid.add(descLabel, 0, 4, 2, 1);
            dialog.getDialogPane().setContent(grid);

            // --- 【核心逻辑】根据 isViewingFavorites 状态动态添加按钮 ---
            ButtonType buyButton = new ButtonType("立即购买");
            ButtonType closeButton = new ButtonType("关闭", ButtonBar.ButtonData.CANCEL_CLOSE);

            if (isViewingFavorites) {
                // 如果当前正在查看收藏夹
                ButtonType removeFavButton = new ButtonType("取消收藏");
                dialog.getDialogPane().getButtonTypes().addAll(buyButton, removeFavButton, closeButton);
            } else {
                // 如果当前正在查看普通商品列表
                ButtonType addFavButton = new ButtonType("加入收藏");
                dialog.getDialogPane().getButtonTypes().addAll(buyButton, addFavButton, closeButton);
            }

            // --- 显示对话框并处理用户的点击结果 ---
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent()) {
                String buttonText = result.get().getText();
                switch (buttonText) {
                    case "立即购买":
                        handleBuyNow(product);
                        break;
                    case "加入收藏":
                        handleAddToFavorites(product);
                        break;
                    case "取消收藏":
                        handleRemoveFavorite(product);
                        break;
                }
            }
        });
    }

    // --- 新增的按钮事件处理逻辑 ---



    /**
     * 【已修正为异步发送】处理用户点击“立即购买”按钮的逻辑
     * @param product 用户想要购买的商品
     */
    private void handleBuyNow(Product product) {
        System.out.println("用户点击“立即购买”商品: " + product.getName());
        // 1. 创建请求对象
        ShopTransaction orderRequest = new ShopTransaction(currentUser.getUserId(), product.getPrice());
        orderRequest.setProduct(product);

        // 2. 调用 Service 层【异步发送】网络请求，不等待返回值
        shopService.createOrder(orderRequest);
    }
    /**
     * 【已修正为异步发送】处理用户点击“加入收藏”按钮的逻辑
     * @param product 用户想要收藏的商品
     */
    private void handleAddToFavorites(Product product) {
        System.out.println("用户点击“加入收藏”商品: " + product.getName());
        // 1. 创建请求对象
        ShopTransaction favoriteRequest = new ShopTransaction(currentUser.getUserId(), product);

        // 2. 调用 Service 层【异步发送】网络请求，不等待返回值
        shopService.addFavorite(favoriteRequest);
    }

    /**
     * 【已修正为异步发送】处理用户点击“取消收藏”按钮的逻辑
     * @param product 用户想要取消收藏的商品
     */
    private void handleRemoveFavorite(Product product) {
        System.out.println("用户点击“取消收藏”商品: " + product.getName());

        Optional<ShopTransaction> favoriteToRemove = currentFavoritesList.stream()
                .filter(fav -> fav.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (favoriteToRemove.isPresent()) {
            String favoriteDatabaseId = favoriteToRemove.get().getId().toString();
            // 【异步发送】网络请求，不等待返回值
            shopService.removeFavorite(favoriteDatabaseId);
        } else {
            showError("在本地收藏列表中未找到该商品，无法取消。请尝试刷新收藏列表。");
        }
    }

    /**
     * 【新增，作为异步入口】处理“创建订单”的异步响应。
     * @param message 服务器返回的响应消息
     */
    public void handleCreateOrderResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                ShopTransaction createdOrder = (ShopTransaction) message.getData();
                showPaymentDialog(createdOrder); // 成功后弹出支付对话框
            } else {
                showError("下单失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增，作为异步入口】处理“添加收藏”的异步响应。
     * @param message 服务器返回的响应消息
     */
    public void handleAddFavoriteResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showSuccess("收藏成功！");
            } else {
                showError("收藏失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增，作为异步入口】处理“取消收藏”的异步响应。
     * @param message 服务器返回的响应消息
     */
    public void handleRemoveFavoriteResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showSuccess("已取消收藏！");
                // 刷新当前的收藏列表视图，让用户看到变化
                handleShowMyFavorites(null);
            } else {
                showError("取消收藏失败: " + message.getMessage());
            }
        });
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

    private void showSuccess(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, message).showAndWait());
    }
    /**
     * 【已升级】显示功能更丰富的支付确认对话框
     * @param order 服务器成功创建并返回的订单对象
     */
    private void showPaymentDialog(ShopTransaction order) {
        // 1. 创建一个自定义对话框
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("订单确认");
        dialog.setHeaderText("订单已创建成功！请确认您的订单信息");

        // 2. 创建用于布局的 GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10); // 水平间距
        grid.setVgap(10); // 垂直间距
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 3. 从 order 对象中提取信息并创建标签
        //    【注意】我们假设订单中只有一个商品，所以直接从 order.getProduct() 获取
        Product product = order.getProduct();

        // 订单号
        grid.add(new Label("订单号:"), 0, 0);
        grid.add(new Label(order.getId() != null ? order.getId().toString() : "N/A"), 1, 0);

        // 商品名称
        grid.add(new Label("商品:"), 0, 1);
        grid.add(new Label(product.getName()), 1, 1);

        // 购买数量 (假设每次买一个)
        grid.add(new Label("数量:"), 0, 2);
        grid.add(new Label("1"), 1, 2);

        // 商品单价
        grid.add(new Label("单价:"), 0, 3);
        grid.add(new Label(String.format("¥ %.2f", product.getPrice())), 1, 3);

        // 订单总价
        grid.add(new Label("总计:"), 0, 4);
        Label totalPriceLabel = new Label(String.format("¥ %.2f", order.getTotalPrice()));
        totalPriceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: red;");
        grid.add(totalPriceLabel, 1, 4);

        // 4. 将 GridPane 设置为对话框的内容
        dialog.getDialogPane().setContent(grid);

        // 5. 创建自定义按钮
        ButtonType confirmButton = new ButtonType("确认支付", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("稍后支付", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        // 6. 显示对话框并处理用户的点击结果
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            System.out.println("用户确认支付订单：" + order.getId());
            // TODO: 在这里调用一个新的 service 方法，向服务器发送支付请求
            // 例如: Message response = shopService.payForOrder(order.getId());
            showSuccess("支付成功！(模拟)"); // 暂时先模拟成功
        } else {
            showError("支付已取消，您可以在“我的订单”中找到该笔订单。");
        }
    }




}
