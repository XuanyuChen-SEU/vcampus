package com.vcampus.client.controller;

import com.vcampus.client.service.ShopService;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

public class CreateOrderController {

    // --- FXML 控件 ---
    @FXML private ImageView productImageView;
    @FXML private Label productNameLabel;
    @FXML private Label productPriceLabel;
    @FXML private Label productDescLabel;
    @FXML private Label balanceLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmButton;
    @FXML private Label orderIdLabel;
    @FXML private Spinner<Integer> quantitySpinner; // 1. 【新增】获取 Spinner 控件

    // --- 成员变量 ---
    private ShopTransaction currentOrder;
    private double userBalance;
    private final ShopService shopService = new ShopService();

    /**
     * 由主控制器调用，用于注入数据并初始化界面。
     */
    public void initData(ShopTransaction order, double userBalance) {
        this.currentOrder = order;
        this.userBalance = userBalance;
        Product product = order.getProduct();
        if (product == null) return;

        // --- 填充UI控件 ---
        orderIdLabel.setText(order.getOrderId());
        // ... (图片、名称等填充代码保持不变) ...
        try {
            if (product.getImageData() != null && product.getImageData().length > 0) {
                productImageView.setImage(new Image(new ByteArrayInputStream(product.getImageData())));
            } else if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                productImageView.setImage(new Image(product.getImagePath()));
            }
        } catch (Exception e) { System.err.println("订单页面加载图片失败: " + e.getMessage()); }
        productNameLabel.setText(product.getName());
        productPriceLabel.setText(String.format("¥ %.2f", product.getPrice()));
        productDescLabel.setText(product.getDescription());
        balanceLabel.setText(String.format("¥ %.2f", this.userBalance));

        // 2. 【核心逻辑】初始化 Spinner
        int maxStock = (product.getStock() > 0) ? product.getStock() : 1; // 确保库存至少为1
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxStock, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // 3. 【核心逻辑】添加监听器，当数量变化时，自动更新总价
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalAndCheckBalance());

        // 4. 初始化总价和按钮状态
        updateTotalAndCheckBalance();
    }

    /**
     * 辅助方法：更新总价标签，并检查余额是否充足。
     */
    private void updateTotalAndCheckBalance() {
        int quantity = quantitySpinner.getValue();
        double unitPrice = currentOrder.getProduct().getPrice();
        double newTotal = unitPrice * quantity;

        // 更新总价标签
        totalPriceLabel.setText(String.format("¥ %.2f", newTotal));

        // 检查余额并更新按钮状态
        if (this.userBalance < newTotal) {
            confirmButton.setDisable(true);
            confirmButton.setText("余额不足");
            balanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        } else {
            confirmButton.setDisable(false);
            confirmButton.setText("确认支付");

            balanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #208837;");
        }
    }

    /**
     * 处理用户点击“确认支付”按钮的事件。
     * 【最终适配版】在 Controller 内部使用 BigDecimal 进行精确计算，
     * 最后将结果转换为 double 类型以适配未修改的 ShopTransaction 类。
     */
    @FXML
    void handleConfirmPurchase(ActionEvent event) {
        // 1. 获取最终确定的购买数量 (int)
        int finalQuantity = quantitySpinner.getValue();

        // 2. 使用 BigDecimal 进行精确的中间计算
        // a. 将商品的单价 (double) 转换为高精度的 BigDecimal
        BigDecimal unitPrice = BigDecimal.valueOf(currentOrder.getProduct().getPrice());

        // b. 将购买数量 (int) 转换为高精度的 BigDecimal
        BigDecimal quantity = new BigDecimal(finalQuantity);

        // c. 使用 BigDecimal 的 multiply 方法进行精确计算
        BigDecimal finalTotalPrice = unitPrice.multiply(quantity);

        // 3. 更新订单对象的数量
        this.currentOrder.setQuantity(finalQuantity);

        // 4. 【核心修正】将精确计算出的 BigDecimal 结果，转换回 double 类型
        //    以便 setTotalPrice(double) 方法能够接收它
        this.currentOrder.setTotalPrice(finalTotalPrice.doubleValue());

        // 5. 打印日志并发送请求
        System.out.println("用户确认支付, 数量: " + finalQuantity + ", (转换后)总价: " + finalTotalPrice.doubleValue());
        shopService.payForOrder(this.currentOrder);

        // 6. 关闭窗口
        closeWindow();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}