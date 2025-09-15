package com.vcampus.client.controller.shopAdmin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.shopAdmin.FavoriteManagementService;
import com.vcampus.client.controller.IClientController;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.ShopTransaction;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 收藏管理控制器
 * 负责显示和管理所有收藏信息
 * 编写人：AI Assistant
 */
public class FavoriteManagementViewController implements IClientController{
    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setFavoriteManagementViewController(this);
        }
    }

    // Service层
    private final FavoriteManagementService favoriteManagementService;
    
    // 搜索组件
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    // 收藏表格
    @FXML
    private TableView<FavoriteTableItem> favoriteTable;
    
    @FXML
    private TableColumn<FavoriteTableItem, String> userIdColumn;
    
    @FXML
    private TableColumn<FavoriteTableItem, String> productIdColumn;
    
    @FXML
    private TableColumn<FavoriteTableItem, String> productNameColumn;
    
    @FXML
    private TableColumn<FavoriteTableItem, String> productPriceColumn;
    
    @FXML
    private TableColumn<FavoriteTableItem, String> addTimeColumn;
    
    @FXML
    private TableColumn<FavoriteTableItem, Void> actionsColumn;
    
    // 统计信息标签
    @FXML
    private Label totalFavoritesLabel;
    
    @FXML
    private Label uniqueUsersLabel;
    
    @FXML
    private Label uniqueProductsLabel;
    
    // 收藏数据
    private ObservableList<FavoriteTableItem> favoriteData = FXCollections.observableArrayList();
    
    /**
     * 构造函数
     */
    public FavoriteManagementViewController() {
        this.favoriteManagementService = new FavoriteManagementService();
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        registerToMessageController();
        // 初始化表格
        initializeTable();
        
        // 使用统一的搜索功能加载收藏数据（全局搜索）
        searchFavorites("");
    }
    
    /**
     * 初始化表格
     */
    private void initializeTable() {
        // 设置表格数据
        favoriteTable.setItems(favoriteData);
        
        // 设置列数据绑定
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        productIdColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty());
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        productPriceColumn.setCellValueFactory(cellData -> cellData.getValue().productPriceProperty());
        addTimeColumn.setCellValueFactory(cellData -> cellData.getValue().addTimeProperty());
        
        // 设置列宽比例，让表格占满整个区域
        userIdColumn.prefWidthProperty().bind(favoriteTable.widthProperty().multiply(0.15));      // 15%
        productIdColumn.prefWidthProperty().bind(favoriteTable.widthProperty().multiply(0.12));    // 12%
        productNameColumn.prefWidthProperty().bind(favoriteTable.widthProperty().multiply(0.25));  // 25%
        productPriceColumn.prefWidthProperty().bind(favoriteTable.widthProperty().multiply(0.15)); // 15%
        addTimeColumn.prefWidthProperty().bind(favoriteTable.widthProperty().multiply(0.23));       // 23%
        actionsColumn.prefWidthProperty().bind(favoriteTable.widthProperty().multiply(0.10));       // 10%
        
        // 设置操作列
        actionsColumn.setCellFactory(new Callback<TableColumn<FavoriteTableItem, Void>, TableCell<FavoriteTableItem, Void>>() {
            @Override
            public TableCell<FavoriteTableItem, Void> call(TableColumn<FavoriteTableItem, Void> param) {
                return new TableCell<FavoriteTableItem, Void>() {
                    private final Button viewButton = new Button("👁️ 查看");
                    
                    {
                        viewButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        viewButton.setOnAction(e -> handleViewFavorite(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5);
                            buttons.getChildren().addAll(viewButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }
    
    /**
     * 搜索收藏方法
     * @param searchText 搜索关键词（用户ID）
     */
    private void searchFavorites(String searchText) {
        try {
            Message result;
            
            // 如果搜索文本为空，获取所有收藏；否则根据用户ID搜索
            if (searchText == null || searchText.trim().isEmpty()) {
                result = favoriteManagementService.getAllFavorites();
                System.out.println("发送获取所有收藏请求");
            } else {
                result = favoriteManagementService.searchFavoritesByUserId(searchText.trim());
                System.out.println("发送根据用户ID搜索收藏请求: " + searchText.trim());
            }
            
            if (result.isSuccess()) {
                System.out.println("成功发送收藏搜索请求");
                // 注意：这里只确认请求发送成功，实际数据会在后续的响应处理中更新
            } else {
                System.err.println("发送收藏搜索请求失败: " + result.getMessage());
                showError("发送收藏搜索请求失败: " + result.getMessage());
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
            if (favoriteData == null) {
                favoriteData = FXCollections.observableArrayList();
            }
            
            int totalFavorites = favoriteData.size();
            
            // 计算唯一用户数
            Set<String> uniqueUsers = favoriteData.stream()
                .filter(favorite -> favorite != null && favorite.getUserId() != null)
                .map(FavoriteTableItem::getUserId)
                .collect(Collectors.toSet());
            int uniqueUsersCount = uniqueUsers.size();
            
            // 计算唯一商品数
            Set<String> uniqueProducts = favoriteData.stream()
                .filter(favorite -> favorite != null && favorite.getProductId() != null)
                .map(FavoriteTableItem::getProductId)
                .collect(Collectors.toSet());
            int uniqueProductsCount = uniqueProducts.size();
            
            // 使用Platform.runLater确保在FX线程上更新UI
            Platform.runLater(() -> {
                try {
                    // 安全地更新UI标签
                    if (totalFavoritesLabel != null) {
                        totalFavoritesLabel.setText("总收藏数: " + totalFavorites);
                    }
                    if (uniqueUsersLabel != null) {
                        uniqueUsersLabel.setText("收藏用户数: " + uniqueUsersCount);
                    }
                    if (uniqueProductsLabel != null) {
                        uniqueProductsLabel.setText("被收藏商品数: " + uniqueProductsCount);
                    }
                } catch (Exception e) {
                    System.err.println("更新UI标签时发生错误: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            System.out.println("收藏统计信息已更新 - 总收藏: " + totalFavorites + 
                             ", 收藏用户: " + uniqueUsersCount + 
                             ", 被收藏商品: " + uniqueProductsCount);
            
        } catch (Exception e) {
            System.err.println("更新收藏统计信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            // 设置默认值
            Platform.runLater(() -> {
                try {
                    if (totalFavoritesLabel != null) {
                        totalFavoritesLabel.setText("总收藏数: 0");
                    }
                    if (uniqueUsersLabel != null) {
                        uniqueUsersLabel.setText("收藏用户数: 0");
                    }
                    if (uniqueProductsLabel != null) {
                        uniqueProductsLabel.setText("被收藏商品数: 0");
                    }
                } catch (Exception ex) {
                    System.err.println("设置默认统计值时发生错误: " + ex.getMessage());
                }
            });
        }
    }
    
    /**
     * 处理查看收藏
     */
    private void handleViewFavorite(FavoriteTableItem favorite) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("收藏详情");
        alert.setHeaderText("收藏信息");
        alert.setContentText("用户ID: " + favorite.getUserId() + "\n" +
                           "商品ID: " + favorite.getProductId() + "\n" +
                           "商品名称: " + favorite.getProductName() + "\n" +
                           "商品价格: " + favorite.getProductPrice() + "\n" +
                           "收藏时间: " + favorite.getAddTime());
        alert.showAndWait();
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
     * 收藏表格项数据类
     */
    public static class FavoriteTableItem {
        private final String userId;
        private final String productId;
        private final String productName;
        private final String productPrice;
        private final String addTime;
        
        public FavoriteTableItem(String userId, String productId, String productName, String productPrice, String addTime) {
            this.userId = userId;
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.addTime = addTime;
        }
        
        public String getUserId() { return userId; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductPrice() { return productPrice; }
        public String getAddTime() { return addTime; }
        
        public javafx.beans.property.StringProperty userIdProperty() {
            return new javafx.beans.property.SimpleStringProperty(userId);
        }
        
        public javafx.beans.property.StringProperty productIdProperty() {
            return new javafx.beans.property.SimpleStringProperty(productId);
        }
        
        public javafx.beans.property.StringProperty productNameProperty() {
            return new javafx.beans.property.SimpleStringProperty(productName);
        }
        
        public javafx.beans.property.StringProperty productPriceProperty() {
            return new javafx.beans.property.SimpleStringProperty(productPrice);
        }
        
        public javafx.beans.property.StringProperty addTimeProperty() {
            return new javafx.beans.property.SimpleStringProperty(addTime);
        }
    }

    public void handleGetAllFavoritesResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("获取所有收藏成功: " + message.getMessage());
            favoriteData.clear();
            List<ShopTransaction> favorites = (List<ShopTransaction>) message.getData();
            if (favorites != null) {
                for (ShopTransaction favorite : favorites) {
                    String addTime = favorite.getAddTime() != null ? 
                        favorite.getAddTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "未知";
                    
                    favoriteData.add(new FavoriteTableItem(
                        favorite.getUserId(),
                        favorite.getProduct().getId().toString(),
                        favorite.getProduct().getName(),
                        String.format("%.2f", favorite.getProduct().getPrice()),
                        addTime
                    ));
                }
            }
            favoriteTable.setItems(favoriteData);
            updateStatistics();
        } else {
            System.err.println("获取所有收藏失败: " + message.getMessage());
            showError("获取所有收藏失败: " + message.getMessage());
        }
    }

    /**
     * 刷新收藏列表
     */
    private void refreshFavoriteList() {
        String searchText = searchField.getText();
        searchFavorites(searchText);
    }

    /**
     * 处理搜索按钮点击事件
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        searchFavorites(searchText);
    }

    /**
     * 处理刷新按钮点击事件
     */
    @FXML
    private void handleRefresh() {
        // 清空搜索条件
        searchField.clear();
        
        // 执行搜索
        searchFavorites("");
    }
}
