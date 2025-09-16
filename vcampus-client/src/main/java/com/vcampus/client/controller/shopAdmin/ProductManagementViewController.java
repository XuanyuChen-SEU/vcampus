package com.vcampus.client.controller.shopAdmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import com.vcampus.client.controller.ShopAdminViewController;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.shopAdmin.ProductManagementService;
import com.vcampus.client.controller.IClientController;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.enums.ProductStatus;
import java.util.List;

/**
 * 商品管理控制器
 * 负责显示和管理所有商品信息
 * 编写人：AI Assistant
 */
public class ProductManagementViewController implements IClientController{
    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setProductManagementViewController(this);
        }
    }

    // Service层
    private final ProductManagementService productManagementService;
    
    // 搜索和筛选组件
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> statusFilterCombo;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button addProductButton;
    
    // 商品表格
    @FXML
    private TableView<ProductTableItem> productTable;
    
    @FXML
    private TableColumn<ProductTableItem, String> idColumn;
    
    @FXML
    private TableColumn<ProductTableItem, String> nameColumn;
    
    @FXML
    private TableColumn<ProductTableItem, String> priceColumn;
    
    @FXML
    private TableColumn<ProductTableItem, String> stockColumn;
    
    @FXML
    private TableColumn<ProductTableItem, String> statusColumn;
    
    @FXML
    private TableColumn<ProductTableItem, Void> actionsColumn;
    
    // 统计信息标签
    @FXML
    private Label totalProductsLabel;
    
    @FXML
    private Label onSaleCountLabel;
    
    @FXML
    private Label offShelfCountLabel;
    
    @FXML
    private Label lowStockCountLabel;
    
    // 商品数据
    private ObservableList<ProductTableItem> productData = FXCollections.observableArrayList();
    private java.util.Map<String, Product> originalProducts = new java.util.HashMap<>();
    
    /**
     * 构造函数
     */
    public ProductManagementViewController() {
        this.productManagementService = new ProductManagementService();
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 初始化状态筛选下拉框
        initializeStatusFilter();
        registerToMessageController();
        // 初始化表格
        initializeTable();
        
        // 初始化统计信息
        updateStatistics();
        
        // 使用统一的搜索功能加载商品数据（全局搜索）
        searchProducts("", "全部");
    }
    
    /**
     * 初始化状态筛选下拉框
     */
    private void initializeStatusFilter() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "全部", "在售", "下架"
        ));
        statusFilterCombo.setValue("全部");
    }
    
    /**
     * 初始化表格
     */
    private void initializeTable() {
        // 设置表格数据
        productTable.setItems(productData);
        
        // 设置列数据绑定
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        // 设置商品名称列显示图片和文字
        nameColumn.setCellFactory(new Callback<TableColumn<ProductTableItem, String>, TableCell<ProductTableItem, String>>() {
            @Override
            public TableCell<ProductTableItem, String> call(TableColumn<ProductTableItem, String> param) {
                return new TableCell<ProductTableItem, String>() {
                    private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
                    private final javafx.scene.control.Label nameLabel = new javafx.scene.control.Label();
                    private final javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(8);
                    
                    {
                        // 设置图片大小（比文字稍大）
                        imageView.setFitWidth(32);
                        imageView.setFitHeight(32);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);
                        
                        // 设置文字样式
                        nameLabel.setStyle("-fx-font-size: 14px;");
                        
                        // 设置HBox布局
                        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(imageView, nameLabel);
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            ProductTableItem productItem = getTableView().getItems().get(getIndex());
                            if (productItem != null) {
                                nameLabel.setText(productItem.getName());
                                
                                // 获取原始商品数据以获取图片
                                Product originalProduct = originalProducts.get(productItem.getId());
                                if (originalProduct != null) {
                                    try {
                                        // 优先使用imageData，其次使用imagePath
                                        if (originalProduct.getImageData() != null && originalProduct.getImageData().length > 0) {
                                            javafx.scene.image.Image image = new javafx.scene.image.Image(
                                                new java.io.ByteArrayInputStream(originalProduct.getImageData()), 32, 32, true, true);
                                            imageView.setImage(image);
                                        } else if (originalProduct.getImagePath() != null && !originalProduct.getImagePath().isEmpty()) {
                                            javafx.scene.image.Image image = new javafx.scene.image.Image(originalProduct.getImagePath(), 32, 32, true, true, true);
                                            imageView.setImage(image);
                                        } else {
                                            // 设置默认图片
                                            imageView.setImage(new javafx.scene.image.Image("https://via.placeholder.com/32", true));
                                        }
                                    } catch (Exception e) {
                                        // 设置默认图片
                                        imageView.setImage(new javafx.scene.image.Image("https://via.placeholder.com/32", true));
                                    }
                                } else {
                                    // 设置默认图片
                                    imageView.setImage(new javafx.scene.image.Image("https://via.placeholder.com/32", true));
                                }
                                
                                setGraphic(hbox);
                            }
                        }
                    }
                };
            }
        });
        
        // 设置列宽比例，让表格占满整个区域
        idColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.12));      // 12%
        nameColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.30));    // 30% (增加宽度以容纳图片)
        priceColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.15));   // 15%
        stockColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.12));   // 12%
        statusColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.16));  // 16%
        actionsColumn.prefWidthProperty().bind(productTable.widthProperty().multiply(0.15)); // 15% (减少操作列宽度)
        
        // 设置操作列
        actionsColumn.setCellFactory(new Callback<TableColumn<ProductTableItem, Void>, TableCell<ProductTableItem, Void>>() {
            @Override
            public TableCell<ProductTableItem, Void> call(TableColumn<ProductTableItem, Void> param) {
                return new TableCell<ProductTableItem, Void>() {
                    private final Button editButton = new Button("✏️ 编辑");
                    private final Button deleteButton = new Button("🗑️ 删除");
                    
                    {
                        editButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        
                        editButton.setOnAction(e -> handleEditProduct(getTableView().getItems().get(getIndex())));
                        deleteButton.setOnAction(e -> handleDeleteProduct(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5);
                            buttons.getChildren().addAll(editButton, deleteButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }
    
    /**
     * 搜索商品方法
     * @param searchText 搜索关键词
     * @param selectedStatus 选择的状态
     */
    private void searchProducts(String searchText, String selectedStatus) {
        try {
            // 使用统一的搜索方法
            Message result = productManagementService.searchProducts(searchText, selectedStatus);
            
            if (result.isSuccess()) {
                if (searchText.isEmpty() && "全部".equals(selectedStatus)) {
                    System.out.println("成功发送全局搜索请求");
                } else if (!searchText.isEmpty()) {
                    System.out.println("成功发送关键词搜索请求: " + searchText);
                } else {
                    System.out.println("成功发送状态筛选请求: " + selectedStatus);
                }
                // 注意：这里只确认请求发送成功，实际数据会在后续的响应处理中更新
            } else {
                System.err.println("发送搜索请求失败: " + result.getMessage());
                showError("发送搜索请求失败: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("执行搜索时发生异常: " + e.getMessage());
            showError("执行搜索时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        try {
            // 确保数据不为null
            if (productData == null) {
                productData = FXCollections.observableArrayList();
            }
            
            int totalProducts = productData.size();
            int onSaleCount = (int) productData.stream()
                .filter(product -> product != null && "在售".equals(product.getStatus()))
                .count();
            int offShelfCount = (int) productData.stream()
                .filter(product -> product != null && "下架".equals(product.getStatus()))
                .count();
            int lowStockCount = (int) productData.stream()
                .filter(product -> {
                    if (product == null || product.getStock() == null) {
                        return false;
                    }
                    try {
                        int stock = Integer.parseInt(product.getStock());
                        return stock < 10; // 库存不足10件
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .count();
            
            // 安全地更新UI标签
            if (totalProductsLabel != null) {
                totalProductsLabel.setText("总商品数: " + totalProducts);
            }
            if (onSaleCountLabel != null) {
                onSaleCountLabel.setText("在售: " + onSaleCount);
            }
            if (offShelfCountLabel != null) {
                offShelfCountLabel.setText("下架: " + offShelfCount);
            }
            if (lowStockCountLabel != null) {
                lowStockCountLabel.setText("库存不足: " + lowStockCount);
            }
            
            System.out.println("统计信息已更新 - 总商品: " + totalProducts + 
                             ", 在售: " + onSaleCount + 
                             ", 下架: " + offShelfCount + 
                             ", 库存不足: " + lowStockCount);
            
        } catch (Exception e) {
            System.err.println("更新统计信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            // 设置默认值
            if (totalProductsLabel != null) {
                totalProductsLabel.setText("总商品数: 0");
            }
            if (onSaleCountLabel != null) {
                onSaleCountLabel.setText("在售: 0");
            }
            if (offShelfCountLabel != null) {
                offShelfCountLabel.setText("下架: 0");
            }
            if (lowStockCountLabel != null) {
                lowStockCountLabel.setText("库存不足: 0");
            }
        }
    }
    
    /**
     * 处理编辑商品
     */
    private void handleEditProduct(ProductTableItem product) {
        // 从原始商品映射中获取完整的商品信息
        Product originalProduct = originalProducts.get(product.getId());
        
        // 直接调用父控制器的loadSubView方法，传递完整的商品对象
        ShopAdminViewController parentController = getParentController();
        if (parentController != null) {
            if (originalProduct != null) {
                // 使用原始商品数据
                parentController.loadSubView("/fxml/admin/shop/ProductEditView.fxml", product.getId(), originalProduct);
            } else {
                // 如果找不到原始数据，使用转换后的数据
                Product productObj = convertTableItemToProduct(product);
                parentController.loadSubView("/fxml/admin/shop/ProductEditView.fxml", product.getId(), productObj);
            }
            // 更新父控制器的状态和按钮样式
            parentController.setCurrentView("productEdit");
        } else {
            System.out.println("无法找到父控制器");
        }
    }
    
    /**
     * 将ProductTableItem转换为Product对象
     * @param tableItem 表格项
     * @return Product对象
     */
    private Product convertTableItemToProduct(ProductTableItem tableItem) {
        Product product = new Product();
        product.setId(Long.parseLong(tableItem.getId()));
        product.setName(tableItem.getName());
        product.setPrice(Double.parseDouble(tableItem.getPrice()));
        product.setStock(Integer.parseInt(tableItem.getStock()));
        
        // 转换状态
        String statusStr = tableItem.getStatus();
        if ("在售".equals(statusStr)) {
            product.setStatus(com.vcampus.common.enums.ProductStatus.ON_SALE);
        } else if ("下架".equals(statusStr) || "已下架".equals(statusStr)) {
            product.setStatus(com.vcampus.common.enums.ProductStatus.OFF_SHELF);
        } else {
            product.setStatus(com.vcampus.common.enums.ProductStatus.ON_SALE); // 默认值
        }
        
        // 设置其他字段的默认值（因为表格中没有显示）
        product.setImagePath(""); // 默认空字符串
        product.setDescription(""); // 默认空字符串
        
        return product;
    }
    
    /**
     * 获取父控制器
     */
    private ShopAdminViewController getParentController() {
        // 通过反射获取父控制器
        try {
            // 获取当前场景
            javafx.scene.Scene scene = productTable.getScene();
            if (scene != null) {
                // 查找场景中的ShopAdminViewController
                return findControllerInScene(scene.getRoot());
            }
        } catch (Exception e) {
            System.out.println("获取父控制器失败: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 在场景中查找ShopAdminViewController
     */
    private ShopAdminViewController findControllerInScene(javafx.scene.Node node) {
        if (node.getUserData() instanceof ShopAdminViewController) {
            return (ShopAdminViewController) node.getUserData();
        }
        
        if (node instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                ShopAdminViewController controller = findControllerInScene(child);
                if (controller != null) {
                    return controller;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 处理删除商品
     */
    private void handleDeleteProduct(ProductTableItem product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除商品");
        alert.setContentText("确定要删除商品 " + product.getName() + " 吗？此操作不可撤销。");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // 使用Service层发送删除商品请求
                    Message result = productManagementService.deleteProduct(product.getId());
                    
                    if (result.isSuccess()) {
                        System.out.println("成功发送删除商品请求: " + product.getId());
                    } else {
                        // 发送请求失败
                        System.err.println("发送删除商品请求失败: " + result.getMessage());
                        showError("发送删除商品请求失败: " + result.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("发送删除商品请求时发生异常: " + e.getMessage());
                    showError("发送删除商品请求时发生异常: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * 处理添加商品
     */
    @FXML
    private void handleAddProduct() {
        // 直接调用父控制器的loadSubView方法
        ShopAdminViewController parentController = getParentController();
        if (parentController != null) {
            parentController.loadSubView("/fxml/admin/shop/ProductAddView.fxml", null);
            // 更新父控制器的状态和按钮样式
            parentController.setCurrentView("productAdd");
        } else {
            System.out.println("无法找到父控制器");
        }
    }
    
    /**
     * 显示错误信息
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("操作失败");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 商品表格项数据类
     */
    public static class ProductTableItem {
        private final String id;
        private final String name;
        private final String price;
        private final String stock;
        private final String status;
        
        public ProductTableItem(String id, String name, String price, String stock, String status) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.status = status;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPrice() { return price; }
        public String getStock() { return stock; }
        public String getStatus() { return status; }
        
        public javafx.beans.property.StringProperty idProperty() {
            return new javafx.beans.property.SimpleStringProperty(id);
        }
        
        public javafx.beans.property.StringProperty nameProperty() {
            return new javafx.beans.property.SimpleStringProperty(name);
        }
        
        public javafx.beans.property.StringProperty priceProperty() {
            return new javafx.beans.property.SimpleStringProperty(price);
        }
        
        public javafx.beans.property.StringProperty stockProperty() {
            return new javafx.beans.property.SimpleStringProperty(stock);
        }
        
        public javafx.beans.property.StringProperty statusProperty() {
            return new javafx.beans.property.SimpleStringProperty(status);
        }
    }

    public void handleSearchProductsResponse(Message message) {
        // 确保在JavaFX Application Thread中执行UI更新
        javafx.application.Platform.runLater(() -> {
            if (message.isSuccess()) {
                System.out.println("搜索商品成功: " + message.getMessage());
                productData.clear();
                originalProducts.clear(); // 清空原始商品映射
                
                List<Product> products = (List<Product>) message.getData();
                if (products != null) {
                    for (Product product : products) {
                        String status = getStatusFromProduct(product.getStatus());
                        productData.add(new ProductTableItem(
                            product.getId().toString(),
                            product.getName(),
                            String.format("%.2f", product.getPrice()),
                            product.getStock().toString(),
                            status
                        ));
                        
                        // 存储原始商品数据
                        originalProducts.put(product.getId().toString(), product);
                    }
                }
                productTable.setItems(productData);
                updateStatistics();
            } else {
                System.err.println("搜索商品失败: " + message.getMessage());
                showError("搜索商品失败: " + message.getMessage());
            }
        });
    }

    public void handleDeleteProductResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("删除商品成功: " + message.getMessage());
            // 重新加载商品列表以反映删除结果
            refreshProductList();
            updateStatistics();
        } else {
            System.err.println("删除商品失败: " + message.getMessage());
            showError("删除商品失败: " + message.getMessage());
        }
    }

    /**
     * 根据商品状态获取状态名称
     */
    private String getStatusFromProduct(ProductStatus status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case ON_SALE:
                return "在售";
            case OFF_SHELF:
                return "下架";
            default:
                return "未知";
        }
    }

    /**
     * 刷新商品列表
     */
    public void refreshProductList() {
        String searchText = searchField.getText();
        String selectedStatus = statusFilterCombo.getValue();
        if (selectedStatus == null) {
            selectedStatus = "全部";
        }
        searchProducts(searchText, selectedStatus);
    }

    /**
     * 处理搜索按钮点击事件
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        String selectedStatus = statusFilterCombo.getValue();
        if (selectedStatus == null) {
            selectedStatus = "全部";
        }
        searchProducts(searchText, selectedStatus);
    }

    /**
     * 处理刷新按钮点击事件
     */
    @FXML
    private void handleRefresh() {
        // 清空搜索条件
        searchField.clear();
        statusFilterCombo.setValue("全部");
        
        // 执行搜索
        searchProducts("", "全部");
    }
}
