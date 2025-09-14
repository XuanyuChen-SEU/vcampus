package com.vcampus.client.controller;

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

    @FXML
    void initialize() {
        System.out.println("商店控制器初始化完成！");
        currentUser = new User(MainApp.getGlobalUserSession().getCurrentUserId(),"");        // 1. 配置UI相关的组件
        setupOrderTable();
        
        // 2. 将自己注册到消息中心，以便接收异步消息
        registerToMessageController();
        
        // 3. 初始化完成后自动加载所有商品
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

    @FXML
    void handleSearch(ActionEvent event) {
        // 1. 确保当前显示的是商品列表视图，而不是订单表格
        switchToProductView();

        // 2. 从界面的搜索输入框 (searchField) 中获取用户输入的文本
        String searchText = searchField.getText();

        // 3. 在控制台打印日志，方便调试，确认函数被触发
        System.out.println("用户正在搜索: " + searchText);

        // 4. 调用 shopService 的 searchProducts 方法，将搜索词发送给后端进行查询
        Message response = shopService.searchProducts(searchText);

        // 5. 根据后端返回的结果，更新界面
        if (response.isSuccess()) {
            // 如果成功，就调用 updateProductDisplay 方法，用返回的商品列表刷新界面
            updateProductDisplay((List<Product>) response.getData());
        } else {
            // 如果失败，就弹出一个错误提示框
            showError("搜索失败: " + response.getMessage());
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
    @FXML
    void handleShowMyOrders(ActionEvent event) {
        // 1. 在控制台打印日志，方便调试
        System.out.println("用户点击了“我的订单”");

        // 2. 确保切换到商品显示视图，与“我的收藏”功能保持界面一致
        switchToProductView();

        // 3. 检查用户是否登录
        if (currentUser == null) {
            showError("用户未登录，无法查看订单。");
            return;
        }

        // 4. 调用服务获取订单数据
        Message response = shopService.getMyOrders(currentUser.getUserId());

        // 5. 处理返回结果
        if (response.isSuccess()) {
            // 成功时，先将返回的 Object 数据转换为订单列表 (List<ShopTransaction>)
            List<ShopTransaction> orders = (List<ShopTransaction>) response.getData();

            // 然后，像“我的收藏”一样，从订单列表中提取出商品列表 (List<Product>)
            List<Product> orderedProducts = orders.stream()
                    .map(ShopTransaction::getProduct) // 假设 ShopTransaction 类有 getProduct() 方法
                    .collect(Collectors.toList());

            // 最后，调用您已有的 updateProductDisplay 方法来显示这些已购商品
            updateProductDisplay(orderedProducts);
        } else {
            // 失败时，显示错误信息
            showError("获取订单失败: " + response.getMessage());
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
     * 【新增方法，作为异步响应入口】
     * 处理“获取商品详情”的异步响应。
     * 这个方法会被 MessageController 在后台线程中调用。
     */
    public void handleGetProductDetailResponse(Message message) {
        System.out.println("客户端 ShopController：收到异步商品详情响应。");

        // 【关键】所有UI更新都必须在 JavaFX Application Thread 中执行
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof Product) {
                Product detailProduct = (Product) message.getData();

                // --- 创建和显示对话框的逻辑和之前完全一样 ---
                Dialog<Product> dialog = new Dialog<>();
                dialog.setTitle("商品详情");
                dialog.setHeaderText(detailProduct.getName());

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                ImageView imageView = new ImageView();
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                try {
                    Image image = new Image(detailProduct.getImageUrl(), true);
                    imageView.setImage(image);
                } catch (Exception e) {
                    imageView.setImage(new Image("https://via.placeholder.com/200", true));
                }

                Label priceLabel = new Label(String.format("价格: ¥ %.2f", detailProduct.getPrice()));
                Label stockLabel = new Label("库存: " + detailProduct.getStock() + " 件");
                Label descriptionLabel = new Label(detailProduct.getDescription());
                descriptionLabel.setWrapText(true);
                descriptionLabel.setPrefWidth(300);

                grid.add(imageView, 0, 0, 1, 3);
                grid.add(priceLabel, 1, 0);
                grid.add(stockLabel, 1, 1);
                grid.add(new Label("描述:"), 0, 3);
                grid.add(descriptionLabel, 0, 4, 2, 1);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                dialog.showAndWait();

            } else {
                showError("（异步）获取详情失败: " + message.getMessage());
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