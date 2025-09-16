package com.vcampus.client.controller.shopAdmin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.vcampus.client.controller.ShopAdminViewController;
import com.vcampus.client.service.shopAdmin.ProductManagementService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.enums.ProductStatus;

import java.io.File;
import java.util.Arrays;

/**
 * 商品编辑视图控制器
 */
public class ProductEditViewController {
    
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane formContainer;
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private Spinner<Integer> stockSpinner;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField imagePathField;
    @FXML private TextArea descriptionField;
    @FXML private Button updateButton;
    @FXML private Button resetButton;
    @FXML private Button cancelButton;
    @FXML private Button selectImageButton;
    
    private ProductManagementService productManagementService;
    private Long productId;
    private Product originalProduct;
    private File selectedImageFile; // 存储选择的图片文件
    
    /**
     * 初始化控制器
     */
    @FXML
    private void initialize() {
        // 初始化服务
        productManagementService = new ProductManagementService();
        
        // 初始化库存微调器
        stockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        
        // 初始化状态下拉框
        statusCombo.getItems().addAll(Arrays.asList("在售", "下架", "缺货"));
        statusCombo.setValue("在售");
        
        // 设置按钮样式
        setupButtonStyles();
    }
    
    /**
     * 设置商品ID（由父控制器调用）
     * @param productId 商品ID
     */
    public void setProductId(String productId) {
        try {
            this.productId = Long.parseLong(productId);
            loadProductData();
        } catch (NumberFormatException e) {
            showError("无效的商品ID格式");
        }
    }
    
    /**
     * 设置商品对象（由父控制器调用，直接传递完整商品信息）
     * @param product 商品对象
     */
    public void setProduct(Product product) {
        if (product != null) {
            this.productId = product.getId();
            this.originalProduct = product;
            populateForm(product);
        } else {
            showError("商品信息不能为空");
        }
    }
    
    /**
     * 加载商品数据
     */
    private void loadProductData() {
        if (productId == null) {
            showError("商品ID不能为空");
            return;
        }
        
        try {
            // 发送获取商品详情请求
            Message result = productManagementService.getProductDetail(productId.toString());
            if (result.isSuccess()) {
                System.out.println("成功发送获取商品详情请求");
                // 注意：这里只确认请求发送成功，实际数据会在后续的响应处理中更新
            } else {
                System.err.println("发送获取商品详情请求失败: " + result.getMessage());
                showError("发送获取商品详情请求失败: " + result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("加载商品数据时发生异常: " + e.getMessage());
            showError("加载商品数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 填充表单数据
     * @param product 商品信息
     */
    private void populateForm(Product product) {
        Platform.runLater(() -> {
            try {
                idField.setText(product.getId().toString());
                nameField.setText(product.getName());
                priceField.setText(String.valueOf(product.getPrice()));
                stockSpinner.getValueFactory().setValue(product.getStock());
                statusCombo.setValue(product.getStatus().toString());
                imagePathField.setText(product.getImagePath());
                descriptionField.setText(product.getDescription());
            } catch (Exception e) {
                System.err.println("填充表单数据时发生错误: " + e.getMessage());
                showError("填充表单数据失败");
            }
        });
    }
    
    /**
     * 处理更新商品
     */
    @FXML
    private void handleUpdate() {
        try {
            // 验证输入
            if (!validateInput()) {
                return;
            }
            
            // 创建商品对象
            Product product = createProductFromForm();
            
            // 如果有选择的图片文件，设置图片数据
            if (selectedImageFile != null) {
                try {
                    // 读取图片文件为字节数组
                    byte[] imageData = java.nio.file.Files.readAllBytes(selectedImageFile.toPath());
                    product.setImageData(imageData);
                    System.out.println("图片文件大小: " + imageData.length + " bytes");
                } catch (Exception e) {
                    System.err.println("读取图片文件失败: " + e.getMessage());
                    showError("读取图片文件失败: " + e.getMessage());
                    return;
                }
            }
            
            // 发送更新请求
            Message result = productManagementService.updateProduct(product);
            if (result.isSuccess()) {
                System.out.println("成功发送商品更新请求");
                // 注意：这里只确认请求发送成功，实际结果会在后续的响应处理中更新
            } else {
                System.err.println("发送商品更新请求失败: " + result.getMessage());
                showError("发送商品更新请求失败: " + result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("更新商品时发生异常: " + e.getMessage());
            showError("更新商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理选择图片
     */
    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择商品图片");
        
        // 设置文件过滤器，只允许PNG文件
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG图片文件", "*.png");
        fileChooser.getExtensionFilters().add(pngFilter);
        
        // 设置初始目录
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        // 获取当前窗口
        Stage stage = (Stage) selectImageButton.getScene().getWindow();
        
        // 显示文件选择对话框
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            // 验证文件是否为PNG格式
            if (selectedFile.getName().toLowerCase().endsWith(".png")) {
                selectedImageFile = selectedFile;
                imagePathField.setText(selectedFile.getName());
                System.out.println("选择的图片文件: " + selectedFile.getAbsolutePath());
            } else {
                showError("请选择PNG格式的图片文件");
            }
        }
    } {
        if (originalProduct != null) {
            populateForm(originalProduct);
        } else {
            clearForm();
        }
    }
    
    /**
     * 处理取消操作
     */
    @FXML
    private void handleCancel() {
        returnToProductManagement();
    }

    /**
     * 处理重置表单
     */
    @FXML
    private void handleReset() {
        if (originalProduct != null) {
            populateForm(originalProduct);
        } else {
            clearForm();
        }
    }
    
    /**
     * 验证输入
     * @return 验证是否通过
     */
    private boolean validateInput() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showError("请输入商品名称");
            nameField.requestFocus();
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                showError("商品价格不能为负数");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("请输入有效的商品价格");
            priceField.requestFocus();
            return false;
        }
        
        int stock = stockSpinner.getValue();
        if (stock < 0) {
            showError("库存数量不能为负数");
            stockSpinner.requestFocus();
            return false;
        }
        
        if (statusCombo.getValue() == null || statusCombo.getValue().trim().isEmpty()) {
            showError("请选择商品状态");
            statusCombo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 从表单创建商品对象
     * @return 商品对象
     */
    private Product createProductFromForm() {
        Product product = new Product();
        product.setId(productId); // 保持原有ID
        product.setName(nameField.getText().trim());
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setStock(stockSpinner.getValue());
        
        // 转换状态字符串为枚举
        String statusStr = statusCombo.getValue();
        if ("在售".equals(statusStr)) {
            product.setStatus(ProductStatus.ON_SALE);
        } else if ("下架".equals(statusStr) || "已下架".equals(statusStr)) {
            product.setStatus(ProductStatus.OFF_SHELF);
        } else {
            product.setStatus(ProductStatus.ON_SALE); // 默认值
        }
        
        product.setImagePath(imagePathField.getText().trim());
        product.setDescription(descriptionField.getText().trim());
        return product;
    }
    
    /**
     * 清空表单（保留商品ID）
     */
    private void clearForm() {
        Platform.runLater(() -> {
            // 不清空商品ID字段，因为它应该保持不变
            // idField.clear(); // 注释掉，保持商品ID不变
            nameField.clear();
            priceField.clear();
            // 检查ValueFactory是否已初始化
            if (stockSpinner.getValueFactory() != null) {
                stockSpinner.getValueFactory().setValue(0);
            }
            statusCombo.setValue("在售");
            imagePathField.clear();
            descriptionField.clear();
        });
    }
    
    /**
     * 返回商品管理页面
     */
    private void returnToProductManagement() {
        try {
            ShopAdminViewController parentController = getParentController();
            if (parentController != null) {
                parentController.loadSubView("/fxml/admin/shop/ProductManagementView.fxml", null);
                parentController.setCurrentView("productManagement");
            } else {
                System.err.println("无法获取父控制器");
            }
        } catch (Exception e) {
            System.err.println("返回商品管理页面时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取父控制器
     */
    private ShopAdminViewController getParentController() {
        try {
            // 通过userData获取父控制器
            AnchorPane parent = (AnchorPane) scrollPane.getParent();
            if (parent != null) {
                Object controller = parent.getUserData();
                if (controller instanceof ShopAdminViewController) {
                    return (ShopAdminViewController) controller;
                }
            }
        } catch (Exception e) {
            System.err.println("获取父控制器时发生异常: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 设置按钮样式
     */
    private void setupButtonStyles() {
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
    }
    
    /**
     * 显示成功消息
     * @param message 消息内容
     */
    private void showSuccess(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("操作成功");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * 显示错误消息
     * @param message 消息内容
     */
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
     * 处理更新商品响应
     * @param message 服务器响应消息
     */
    public void handleUpdateProductResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("商品更新成功: " + message.getMessage());
            showSuccess("商品更新成功！");
            
            // 通知商品管理控制器刷新列表
            notifyProductManagementRefresh();
            
            // 延迟返回商品管理页面
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1000);
                    returnToProductManagement();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    returnToProductManagement();
                }
            });
        } else {
            System.err.println("商品更新失败: " + message.getMessage());
            showError("商品更新失败: " + message.getMessage());
        }
    }
    
    /**
     * 通知商品管理控制器刷新列表
     */
    private void notifyProductManagementRefresh() {
        try {
            // 通过MessageController获取ProductManagementViewController
            com.vcampus.client.controller.MessageController messageController = 
                com.vcampus.client.MainApp.getGlobalSocketClient().getMessageController();
            
            if (messageController != null && messageController.getProductManagementViewController() != null) {
                // 触发刷新操作
                messageController.getProductManagementViewController().refreshProductList();
                System.out.println("已通知商品管理控制器刷新列表");
            }
        } catch (Exception e) {
            System.err.println("通知刷新失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理获取商品详情响应
     * @param message 服务器响应消息
     */
    public void handleGetProductDetailResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("获取商品详情成功: " + message.getMessage());
            originalProduct = (Product) message.getData();
            if (originalProduct != null) {
                populateForm(originalProduct);
            } else {
                showError("商品不存在");
            }
        } else {
            System.err.println("获取商品详情失败: " + message.getMessage());
            showError("获取商品详情失败: " + message.getMessage());
        }
    }
}
