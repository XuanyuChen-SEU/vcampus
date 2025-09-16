package com.vcampus.client.controller.shopAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.controller.ShopAdminViewController;
import com.vcampus.client.service.shopAdmin.ProductAddService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Product;
import com.vcampus.common.enums.ProductStatus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/**
 * 商品添加控制器
 * 负责添加新商品的功能
 * 编写人：AI Assistant
 */
public class ProductAddViewController implements IClientController{

    // Service层
    private final ProductAddService productAddService;
    
    // 表单组件
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private Spinner<Integer> stockSpinner;
    
    @FXML
    private ComboBox<String> statusCombo;
    
    @FXML
    private javafx.scene.image.ImageView imagePreview;
    
    @FXML
    private TextArea descriptionField;
    
    // 按钮组件
    @FXML
    private Button addButton;
    
    @FXML
    private Button resetButton;
    
    @FXML
    private Button selectImageButton;
    
    // 存储选择的图片文件
    private File selectedImageFile;
    
    /**
     * 构造函数
     */
    public ProductAddViewController() {
        this.productAddService = new ProductAddService();
    }

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setProductAddViewController(this);
        }
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 设置输入验证
        setupInputValidation();
        // 初始化组件
        initializeComponents();
        registerToMessageController();
    }
    
    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 初始化库存微调器
        SpinnerValueFactory<Integer> stockFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0);
        stockSpinner.setValueFactory(stockFactory);
        
        // 初始化状态下拉框
        statusCombo.getItems().addAll("在售", "下架");
        statusCombo.setValue("在售");
        
        // 设置默认焦点
        nameField.requestFocus();
    }
    
    /**
     * 设置输入验证
     */
    private void setupInputValidation() {
        // 价格输入验证（只允许数字和小数点）
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldValue);
            }
        });
        
        // 图片路径输入验证已移除，因为现在使用图片预览
        
        // 商品名称长度限制
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) { // 限制名称长度
                nameField.setText(oldValue);
            }
        });
        
        // 商品描述长度限制
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1000) { // 限制描述长度
                descriptionField.setText(oldValue);
            }
        });
    }
    
    /**
     * 处理添加商品
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        // 验证输入
        if (!validateInput()) {
            return;
        }
        
        // 获取表单数据
        String name = nameField.getText().trim();
        double price = Double.parseDouble(priceField.getText().trim());
        int stock = stockSpinner.getValue();
        ProductStatus status = getStatusFromString(statusCombo.getValue());
        String description = descriptionField.getText().trim();
        
        try {
            // 创建商品对象
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setPrice(price);
            newProduct.setStock(stock);
            newProduct.setStatus(status);
            newProduct.setDescription(description.isEmpty() ? null : description);
            
            // 如果有选择的图片文件，设置图片数据
            if (selectedImageFile != null) {
                try {
                    // 读取图片文件为字节数组
                    byte[] imageData = java.nio.file.Files.readAllBytes(selectedImageFile.toPath());
                    newProduct.setImageData(imageData);
                    System.out.println("图片文件大小: " + imageData.length + " bytes");
                } catch (Exception e) {
                    System.err.println("读取图片文件失败: " + e.getMessage());
                    showError("读取图片文件失败: " + e.getMessage());
                    return;
                }
            }
            
            // 使用Service层发送添加商品请求
            Message result = productAddService.addProduct(newProduct);
            
            if (result.isSuccess()) {
                // 发送请求成功
                System.out.println("成功发送添加商品请求: " + name);
                
                // 显示发送成功消息
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("请求已发送");
                alert.setHeaderText("添加商品请求已发送");
                alert.setContentText("商品 " + name + " 的添加请求已发送到服务器，请等待处理结果。");
                alert.showAndWait();
                
                // 重置表单
                resetForm();
            } else {
                // 发送请求失败
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("发送失败");
                alert.setHeaderText("添加商品请求发送失败");
                alert.setContentText(result.getMessage());
                alert.showAndWait();
                System.err.println("发送添加商品请求失败: " + result.getMessage());
            }
        } catch (Exception e) {
            System.err.println("发送添加商品请求时发生异常: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("发送失败");
            alert.setHeaderText("添加商品请求发送失败");
            alert.setContentText("发送请求时发生异常: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * 处理取消
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        // 返回到商品管理视图
        goBackToProductManagement();
    }
    
    /*
     * 处理重置表单
     */
    @FXML
    private void handleReset(ActionEvent event) {
        resetForm();
    }
    

    /**
     * 处理选择图片
     */
    @FXML
    private void handleSelectImage(ActionEvent event) {
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
                
                // 显示图片预览
                try {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(selectedFile.toURI().toString());
                    imagePreview.setImage(image);
                    System.out.println("选择的图片文件: " + selectedFile.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println("加载图片预览失败: " + e.getMessage());
                    showError("加载图片预览失败: " + e.getMessage());
                }
            } else {
                showError("请选择PNG格式的图片文件");
            }
        }
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        
        // 验证商品名称
        if (name.isEmpty()) {
            showError("请输入商品名称");
            nameField.requestFocus();
            return false;
        }
        
        if (name.length() < 2) {
            showError("商品名称至少需要2个字符");
            nameField.requestFocus();
            return false;
        }
        
        // 验证价格
        if (priceText.isEmpty()) {
            showError("请输入商品价格");
            priceField.requestFocus();
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                showError("商品价格必须大于0");
                priceField.requestFocus();
                return false;
            }
            if (price > 999999.99) {
                showError("商品价格不能超过999999.99");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("请输入有效的价格数字");
            priceField.requestFocus();
            return false;
        }
        
        // 验证库存
        int stock = stockSpinner.getValue();
        if (stock < 0) {
            showError("库存不能为负数");
            stockSpinner.requestFocus();
            return false;
        }
        
        // 验证状态
        if (statusCombo.getValue() == null) {
            showError("请选择商品状态");
            statusCombo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * 重置表单
     */
    private void resetForm() {
        nameField.clear();
        priceField.clear();
        stockSpinner.getValueFactory().setValue(0);
        statusCombo.setValue("在售");
        imagePreview.setImage(null); // 清除图片预览
        descriptionField.clear();
        selectedImageFile = null; // 重置选择的图片文件
        nameField.requestFocus();
    }
    
    /**
     * 返回到商品管理视图
     */
    private void goBackToProductManagement() {
        // 通过反射获取父控制器
        try {
            // 获取当前场景
            javafx.scene.Scene scene = nameField.getScene();
            if (scene != null) {
                // 查找场景中的ShopAdminViewController
                ShopAdminViewController parentController = findControllerInScene(scene.getRoot());
                if (parentController != null) {
                    parentController.loadSubView("/fxml/admin/shop/ProductManagementView.fxml", null);
                    parentController.setCurrentView("productManagement");
                }
            }
        } catch (Exception e) {
            System.out.println("返回商品管理视图失败: " + e.getMessage());
        }
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
     * 根据字符串获取商品状态
     */
    private ProductStatus getStatusFromString(String statusString) {
        if ("在售".equals(statusString)) {
            return ProductStatus.ON_SALE;
        } else if ("下架".equals(statusString)) {
            return ProductStatus.OFF_SHELF;
        } else {
            return ProductStatus.ON_SALE; // 默认在售
        }
    }
    
    /**
     * 显示错误信息
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("输入错误");
        alert.setHeaderText("请检查输入信息");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 处理添加商品响应
     */
    public void handleAddProductResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("添加商品成功: " + message.getMessage());
            // 可以显示成功消息或自动返回商品管理视图
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("添加成功");
            alert.setHeaderText("商品添加成功");
            alert.setContentText(message.getMessage());
            alert.showAndWait();
            
            // 通知商品管理控制器刷新列表
            notifyProductManagementRefresh();
            
            // 自动返回商品管理视图
            goBackToProductManagement();
        } else {
            System.err.println("添加商品失败: " + message.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("添加失败");
            alert.setHeaderText("商品添加失败");
            alert.setContentText(message.getMessage());
            alert.showAndWait();
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
}
