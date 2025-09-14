package com.vcampus.client.controller.shopAdmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.application.Platform;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.shopAdmin.OrderManagementService;
import com.vcampus.client.controller.IClientController;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.OrderStatus;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单管理控制器
 * 负责显示和管理所有订单信息
 * 编写人：AI Assistant
 */
public class OrderManagementViewController implements IClientController{
    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setOrderManagementViewController(this);
        }
    }

    // Service层
    private final OrderManagementService orderManagementService;
    
    // 搜索和筛选组件
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> statusFilterCombo;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    // 订单表格
    @FXML
    private TableView<OrderTableItem> orderTable;
    
    @FXML
    private TableColumn<OrderTableItem, String> orderIdColumn;
    
    @FXML
    private TableColumn<OrderTableItem, String> userIdColumn;
    
    @FXML
    private TableColumn<OrderTableItem, String> totalPriceColumn;
    
    @FXML
    private TableColumn<OrderTableItem, String> statusColumn;
    
    @FXML
    private TableColumn<OrderTableItem, String> createTimeColumn;
    
    @FXML
    private TableColumn<OrderTableItem, String> payTimeColumn;
    
    @FXML
    private TableColumn<OrderTableItem, Void> actionsColumn;
    
    // 统计信息标签
    @FXML
    private Label totalOrdersLabel;
    
    @FXML
    private Label unpaidCountLabel;
    
    @FXML
    private Label paidCountLabel;
    
    @FXML
    private Label cancelledCountLabel;
    
    // 订单数据
    private ObservableList<OrderTableItem> orderData = FXCollections.observableArrayList();
    
    /**
     * 构造函数
     */
    public OrderManagementViewController() {
        this.orderManagementService = new OrderManagementService();
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
        
        // 使用统一的搜索功能加载订单数据（全局搜索）
        searchOrders("", "全部");
    }
    
    /**
     * 初始化状态筛选下拉框
     */
    private void initializeStatusFilter() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "全部", "未支付", "已支付", "已取消"
        ));
        statusFilterCombo.setValue("全部");
    }
    
    /**
     * 初始化表格
     */
    private void initializeTable() {
        // 设置表格数据
        orderTable.setItems(orderData);
        
        // 设置列数据绑定
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        totalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        createTimeColumn.setCellValueFactory(cellData -> cellData.getValue().createTimeProperty());
        payTimeColumn.setCellValueFactory(cellData -> cellData.getValue().payTimeProperty());
        
        // 设置列宽比例，让表格占满整个区域
        orderIdColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.12));      // 12%
        userIdColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.12));       // 12%
        totalPriceColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.12));   // 12%
        statusColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.14));      // 14%
        createTimeColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.20));  // 20%
        payTimeColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.20));     // 20%
        actionsColumn.prefWidthProperty().bind(orderTable.widthProperty().multiply(0.10));       // 10%
        
        // 设置操作列
        actionsColumn.setCellFactory(new Callback<TableColumn<OrderTableItem, Void>, TableCell<OrderTableItem, Void>>() {
            @Override
            public TableCell<OrderTableItem, Void> call(TableColumn<OrderTableItem, Void> param) {
                return new TableCell<OrderTableItem, Void>() {
                    private final Button viewButton = new Button("👁️ 查看");
                    
                    {
                        viewButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        viewButton.setOnAction(e -> handleViewOrder(getTableView().getItems().get(getIndex())));
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
     * 搜索订单方法
     * @param searchText 搜索关键词（用户ID）
     * @param selectedStatus 选择的状态
     */
    private void searchOrders(String searchText, String selectedStatus) {
        try {
            Message result;
            
            // 如果搜索文本为空，获取所有订单；否则根据用户ID搜索
            if (searchText == null || searchText.trim().isEmpty()) {
                result = orderManagementService.getAllOrders();
                System.out.println("发送获取所有订单请求");
            } else {
                result = orderManagementService.searchOrdersByUserId(searchText.trim());
                System.out.println("发送根据用户ID搜索订单请求: " + searchText.trim());
            }
            
            if (result.isSuccess()) {
                System.out.println("成功发送订单搜索请求");
                // 注意：这里只确认请求发送成功，实际数据会在后续的响应处理中更新
            } else {
                System.err.println("发送订单搜索请求失败: " + result.getMessage());
                showError("发送订单搜索请求失败: " + result.getMessage());
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
            if (orderData == null) {
                orderData = FXCollections.observableArrayList();
            }
            
            int totalOrders = orderData.size();
            int unpaidCount = (int) orderData.stream()
                .filter(order -> order != null && "未支付".equals(order.getStatus()))
                .count();
            int paidCount = (int) orderData.stream()
                .filter(order -> order != null && "已支付".equals(order.getStatus()))
                .count();
            int cancelledCount = (int) orderData.stream()
                .filter(order -> order != null && "已取消".equals(order.getStatus()))
                .count();
            
            // 使用Platform.runLater确保在FX线程上更新UI
            Platform.runLater(() -> {
                try {
                    // 安全地更新UI标签
                    if (totalOrdersLabel != null) {
                        totalOrdersLabel.setText("总订单数: " + totalOrders);
                    }
                    if (unpaidCountLabel != null) {
                        unpaidCountLabel.setText("未支付: " + unpaidCount);
                    }
                    if (paidCountLabel != null) {
                        paidCountLabel.setText("已支付: " + paidCount);
                    }
                    if (cancelledCountLabel != null) {
                        cancelledCountLabel.setText("已取消: " + cancelledCount);
                    }
                } catch (Exception e) {
                    System.err.println("更新UI标签时发生错误: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            System.out.println("订单统计信息已更新 - 总订单: " + totalOrders + 
                             ", 未支付: " + unpaidCount + 
                             ", 已支付: " + paidCount + 
                             ", 已取消: " + cancelledCount);
            
        } catch (Exception e) {
            System.err.println("更新订单统计信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            // 设置默认值
            Platform.runLater(() -> {
                try {
                    if (totalOrdersLabel != null) {
                        totalOrdersLabel.setText("总订单数: 0");
                    }
                    if (unpaidCountLabel != null) {
                        unpaidCountLabel.setText("未支付: 0");
                    }
                    if (paidCountLabel != null) {
                        paidCountLabel.setText("已支付: 0");
                    }
                    if (cancelledCountLabel != null) {
                        cancelledCountLabel.setText("已取消: 0");
                    }
                } catch (Exception ex) {
                    System.err.println("设置默认统计值时发生错误: " + ex.getMessage());
                }
            });
        }
    }
    
    /**
     * 处理查看订单
     */
    private void handleViewOrder(OrderTableItem order) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("订单详情");
        alert.setHeaderText("订单信息");
        alert.setContentText("订单ID: " + order.getOrderId() + "\n" +
                           "用户ID: " + order.getUserId() + "\n" +
                           "总金额: " + order.getTotalPrice() + "\n" +
                           "状态: " + order.getStatus() + "\n" +
                           "创建时间: " + order.getCreateTime() + "\n" +
                           "支付时间: " + order.getPayTime());
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
     * 订单表格项数据类
     */
    public static class OrderTableItem {
        private final String orderId;
        private final String userId;
        private final String totalPrice;
        private final String status;
        private final String createTime;
        private final String payTime;
        
        public OrderTableItem(String orderId, String userId, String totalPrice, String status, String createTime, String payTime) {
            this.orderId = orderId;
            this.userId = userId;
            this.totalPrice = totalPrice;
            this.status = status;
            this.createTime = createTime;
            this.payTime = payTime;
        }
        
        public String getOrderId() { return orderId; }
        public String getUserId() { return userId; }
        public String getTotalPrice() { return totalPrice; }
        public String getStatus() { return status; }
        public String getCreateTime() { return createTime; }
        public String getPayTime() { return payTime; }
        
        public javafx.beans.property.StringProperty orderIdProperty() {
            return new javafx.beans.property.SimpleStringProperty(orderId);
        }
        
        public javafx.beans.property.StringProperty userIdProperty() {
            return new javafx.beans.property.SimpleStringProperty(userId);
        }
        
        public javafx.beans.property.StringProperty totalPriceProperty() {
            return new javafx.beans.property.SimpleStringProperty(totalPrice);
        }
        
        public javafx.beans.property.StringProperty statusProperty() {
            return new javafx.beans.property.SimpleStringProperty(status);
        }
        
        public javafx.beans.property.StringProperty createTimeProperty() {
            return new javafx.beans.property.SimpleStringProperty(createTime);
        }
        
        public javafx.beans.property.StringProperty payTimeProperty() {
            return new javafx.beans.property.SimpleStringProperty(payTime);
        }
    }

    public void handleGetAllOrdersResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("获取所有订单成功: " + message.getMessage());
            orderData.clear();
            List<ShopTransaction> orders = (List<ShopTransaction>) message.getData();
            if (orders != null) {
                for (ShopTransaction order : orders) {
                    String status = getStatusFromOrder(order.getOrderStatus());
                    String createTime = order.getCreateTime() != null ? 
                        order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "未知";
                    String payTime = order.getPayTime() != null ? 
                        order.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "未支付";
                    
                    orderData.add(new OrderTableItem(
                        order.getOrderId(),
                        order.getUserId(),
                        String.format("%.2f", order.getTotalPrice()),
                        status,
                        createTime,
                        payTime
                    ));
                }
            }
            orderTable.setItems(orderData);
            Platform.runLater(this::updateStatistics);
        } else {
            System.err.println("获取所有订单失败: " + message.getMessage());
            showError("获取所有订单失败: " + message.getMessage());
        }
    }

    /**
     * 根据订单状态获取状态名称
     */
    private String getStatusFromOrder(OrderStatus status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case UNPAID:
                return "未支付";
            case PAID:
                return "已支付";
            case CANCELLED:
                return "已取消";
            default:
                return "未知";
        }
    }

    /**
     * 刷新订单列表
     */
    private void refreshOrderList() {
        String searchText = searchField.getText();
        String selectedStatus = statusFilterCombo.getValue();
        if (selectedStatus == null) {
            selectedStatus = "全部";
        }
        searchOrders(searchText, selectedStatus);
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
        searchOrders(searchText, selectedStatus);
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
        searchOrders("", "全部");
    }
}
