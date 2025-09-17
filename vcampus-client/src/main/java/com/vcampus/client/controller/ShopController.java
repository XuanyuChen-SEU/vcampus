package com.vcampus.client.controller;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import java.io.FileInputStream;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.ShopService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.OrderStatus;
import javafx.stage.Window;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.scene.layout.AnchorPane;
import com.vcampus.common.entity.Balance;
import javafx.scene.control.TextInputDialog;
import java.math.BigDecimal;
import java.io.ByteArrayInputStream;
import javafx.scene.layout.*;

public class ShopController implements IClientController{

    // --- FXML 控件变量 ---
    @FXML private TextField searchField;
    @FXML private TilePane productPane; // 用于展示商品和收藏
    @FXML private ScrollPane productScrollPane; // 包裹 TilePane 的滚动面板
    @FXML private TableView<ShopTransaction> orderTable; // 新增：用于展示订单的表格
    @FXML private StackPane centerStackPane; // 新增：用于切换视图的 StackPane
    @FXML private Label balanceLabel;
    @FXML private Button rechargeButton;

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
        final int numColumns = 4;
        final double hgap = 15.0;
        final double vgap = 15.0;
        final Insets padding = new Insets(15.0);

        productPane.setHgap(hgap);
        productPane.setVgap(vgap);
        productPane.setPadding(padding);

        // 关键：让 TilePane 的宽度能跟随 ScrollPane 变化
        productScrollPane.setFitToWidth(true);

        // 【核心】让 TilePane (父亲) 自己计算并规定每个瓦片（儿子）的宽度
        // 这是最稳定、最不会出错的方式
        productPane.prefTileWidthProperty().bind(
                productScrollPane.widthProperty() // 我们从 ScrollPane 获取总可用宽度
                        .subtract(padding.getLeft() + padding.getRight()) // 减去左右内边距
                        .subtract((numColumns - 1) * hgap) // 减去3个水平间距
                        .subtract(18) // 减去滚动条宽度和安全边距
                        .divide(numColumns)
        );
        // --- 配置结束 ---
        System.out.println("商店控制器初始化完成！");
        currentUser = new User(MainApp.getGlobalUserSession().getCurrentUserId(),"");        // 1. 配置UI相关的组件
        setupOrderTable();

        // 2. 将自己注册到消息中心，以便接收异步消息
        registerToMessageController();
        loadInitialProducts();
        shopService.getBalance(currentUser.getUserId());

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
                    Image image;
                    if (product.getImageData() != null && product.getImageData().length > 0) {
                        // 使用图片数据创建Image
                        image = new Image(new java.io.ByteArrayInputStream(product.getImageData()), 50, 50, true, true);
                    } else {
                        // 回退到使用图片路径
                        image = new Image(product.getImagePath(), 50, 50, true, true, true);
                    }
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
            System.out.println("=== 客户端显示商品列表，共 " + products.size() + " 个商品 ===");
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                System.out.println("  位置 " + i + ": " + product.getName() + " (ID: " + product.getId() + ")");
                productPane.getChildren().add(createProductCard(product));
            }
            System.out.println("=== 商品列表显示完成 ===");
        });
    }

    /**
     * 【超整合 · 单一方法版】
     *  - 将所有逻辑（UI创建、图片加载、事件处理）都封装在此方法内。
     *  - 保证所有商品卡片的图片区域视觉效果统一。
     *  - 您只需要复制这一个方法即可。
     */
    private VBox createProductCard(Product product) {
        // ==========================================================
        // 1. 创建UI组件
        // ==========================================================

        // 根容器 (整个卡片)
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #DDDDDD; -fx-border-radius: 5; -fx-background-color: #FFFFFF; -fx-background-radius: 5;");
        card.setPrefWidth(220); // 您可以调整这个宽度来控制布局

        // 固定尺寸的“画框”，用于统一图片区域
        StackPane imageFrame = new StackPane();
        imageFrame.setPrefHeight(220); // 固定画框高度，形成1:1的图片区
        imageFrame.setStyle("-fx-background-color: #FFFFFF;");
        imageFrame.setAlignment(Pos.CENTER);

        // 图片视图
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(220);  // 图片最大宽度
        imageView.setFitHeight(220); // 图片最大高度

        // 文本和按钮的容器
        VBox textContainer = new VBox(5);
        textContainer.setPadding(new Insets(5, 0, 0, 0));
        VBox.setVgrow(textContainer, Priority.ALWAYS);

        // 商品名称
        Label nameLabel = new Label(product.getName());
        nameLabel.setWrapText(true);

        // 价格
        HBox priceBox = new HBox(2);
        Label currencyLabel = new Label("¥ ");
        currencyLabel.setTextFill(Color.RED);
        Label priceLabel = new Label(String.valueOf(product.getPrice()));
        priceLabel.setTextFill(Color.RED);
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        priceBox.getChildren().addAll(currencyLabel, priceLabel);

        // 弹簧，用于将按钮推到底部
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 按钮
        Button actionButton = new Button("查看详情");
        actionButton.setMaxWidth(Double.MAX_VALUE);


        // ==========================================================
        // 2. 图片加载逻辑（直接整合在此）
        // ==========================================================
        try {
            Image image;
            if (product.getImageData() != null && product.getImageData().length > 0) {
                // 优先从二进制数据加载
                image = new Image(new ByteArrayInputStream(product.getImageData()));
            } else if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                // 其次从文件路径加载
                image = new Image(new FileInputStream(product.getImagePath()));
            } else {
                // 如果都没有，使用占位图
                image = new Image("https://via.placeholder.com/220x220.png?text=No+Image");
            }
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("加载图片失败: " + product.getName() + " | " + e.getMessage());
            // 出现任何错误，都显示错误占位图
            imageView.setImage(new Image("https://via.placeholder.com/220x220.png?text=Error"));
        }


        // ==========================================================
        // 3. 事件处理逻辑（直接整合在此）
        // ==========================================================
        actionButton.setOnAction(event -> {
            // "查看详情" 按钮被点击后要执行的操作
            System.out.println("查看商品详情: " + product.getName());

            // 您可以在这里直接编写跳转到详情页或弹出新窗口的逻辑
            // 例如:
            // openProductDetailWindow(product);
        });


        // ==========================================================
        // 4. 组装并返回最终的卡片
        // ==========================================================
        imageFrame.getChildren().add(imageView);
        textContainer.getChildren().addAll(nameLabel, priceBox, spacer, actionButton);
        card.getChildren().addAll(imageFrame, textContainer);

        return card;
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
            System.out.println("显示商品详情: " + product.getName() + " (ID: " + product.getId() + ")");

            // --- 创建和显示对话框的逻辑 ---
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("商品详情");
            dialog.setHeaderText(product.getName());

            // --- 设置对话框内容 (GridPane) ---
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            ImageView imageView;
            if (product.getImageData() != null && product.getImageData().length > 0) {
                // 使用图片数据创建Image
                imageView = new ImageView(new Image(new java.io.ByteArrayInputStream(product.getImageData()), 200, 200, true, true));
            } else {
                // 回退到使用图片路径
                imageView = new ImageView(new Image(product.getImagePath(), 200, 200, true, true, true));
            }
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
     * 【最终版】处理“创建订单”的异步响应。
     * 当服务器成功创建一个待支付订单后，此方法被 MessageController 调用。
     * @param message 服务器返回的响应消息
     */
    public void handleCreateOrderResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                // 1. 从消息中解析出服务器已创建的订单对象
                ShopTransaction createdOrder = (ShopTransaction) message.getData();

                // 2. 【核心】调用我们新的方法，弹出独立的 FXML 窗口
                showCreateOrderWindow(createdOrder);

            } else {
                // 3. 如果下单失败，显示错误信息
                showError("下单失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【最终修正版】处理“创建订单”的异步响应。
     * 弹出独立的 FXML 窗口，并动态传入真实的订单数据和用户余额。
     * @param order 服务器返回的已创建的订单对象
     */
    private void showCreateOrderWindow(ShopTransaction order) {
        try {
            // 1. 创建 FXMLLoader 并指定 FXML 文件路径
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/store/CreateOrderView.fxml"));

            // 2. 加载 FXML 并创建新窗口 (Stage)
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("创建订单");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // 将新窗口的所有者设置为当前主窗口
            Window ownerWindow = productScrollPane.getScene().getWindow();
            dialogStage.initOwner(ownerWindow);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 3. 获取新窗口的控制器
            CreateOrderController controller = loader.getController();

            // --- 【核心修正：动态获取并传递真实余额】 ---

            // a. 从主界面的 balanceLabel 获取真实的余额文本 (例如 "余额: ¥ 1000.00")
            String balanceText = this.balanceLabel.getText();

            double realBalance = 0.0;
            try {
                // b. 使用正则表达式移除所有非数字和非小数点的字符，得到纯数字字符串 "1000.00"
                String numericString = balanceText.replaceAll("[^\\d.]", "");
                // c. 将纯数字字符串转换为 double 类型
                realBalance = Double.parseDouble(numericString);
            } catch (NumberFormatException | NullPointerException e) {
                // d. 如果解析失败（例如Label文本为空或格式错误），打印错误并提示用户
                System.err.println("严重错误：从Label解析余额失败！文本内容: '" + balanceText + "'");
                e.printStackTrace();
                showError("无法获取您的账户余额，请稍后重试。");
                return; // 关键：如果无法获取余额，则不应打开支付窗口
            }

            // 4. 【关键】调用新窗口控制器的数据初始化方法，传入【真实的余额】
            controller.initData(order, realBalance);

            // --- 修正结束 ---

            // 5. 显示窗口并等待用户操作 (例如点击“确认支付”或“取消”)
            dialogStage.showAndWait();

        } catch (IOException | IllegalStateException e) {
            // 捕获 IOException (文件加载失败) 和 IllegalStateException (路径错误)
            System.err.println("无法加载订单确认页面！请检查 FXML 路径是否正确: /fxml/store/CreateOrderView.fxml");
            e.printStackTrace();
            showError("无法加载订单页面: " + e.getMessage());
        }
    }

    public void handlePayForOrderResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                // 支付成功，服务器会返回最新的余额信息
                Balance updatedBalance = (Balance) message.getData();
                balanceLabel.setText(String.format("余额: ¥ %.2f", updatedBalance.getBalance()));

                // 显示一个真正的成功提示
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("成功");
                alert.setHeaderText(null);
                alert.setContentText("支付成功！");
                alert.showAndWait();

                // （可选）自动刷新一下订单列表
                handleShowMyOrders(null);
            } else {
                showError("支付失败: " + message.getMessage());
            }
        });
    }

    /**
     * 处理点击“充值”按钮的事件。
     */
    @FXML
    void handleRecharge(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("100");
        dialog.setTitle("账户充值");
        dialog.setHeaderText("请输入您要充值的金额");
        dialog.setContentText("金额 (元):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                // 将输入的字符串转换为 BigDecimal
                BigDecimal amount = new BigDecimal(amountStr);
                // 调用 Service 发送充值请求
                shopService.recharge(currentUser.getUserId(), amount);
            } catch (NumberFormatException e) {
                showError("无效的金额格式，请输入数字。");
            }
        });
    }

    /**
     * 【异步入口】处理“获取余额”的响应。
     */
    public void handleGetBalanceResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                Balance balance = (Balance) message.getData();
                balanceLabel.setText(String.format("余额: ¥ %.2f", balance.getBalance()));
            } else {
                balanceLabel.setText("余额: 获取失败");
                showError("获取余额失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【异步入口】处理“充值”的响应。
     */
    public void handleRechargeResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                // 充值成功后，服务器会返回最新的余额
                Balance updatedBalance = (Balance) message.getData();
                balanceLabel.setText(String.format("余额: ¥ %.2f", updatedBalance.getBalance()));
                showSuccess("充值成功！");
            } else {
                showError("充值失败: " + message.getMessage());
            }
        });
    }
}
