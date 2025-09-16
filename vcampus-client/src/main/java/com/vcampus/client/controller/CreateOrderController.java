package com.vcampus.client.controller;

import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CreateOrderController {

    @FXML private ImageView productImageView;
    @FXML private Label productNameLabel;
    @FXML private Label productPriceLabel;
    @FXML private Label productDescLabel;
    @FXML private Label balanceLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmButton;
    @FXML private Label orderIdLabel; // 已添加

    private ShopTransaction order;
    private double userBalance;

    /**
     * 由 ShopController 手动调用，用于在显示窗口前传递数据。
     */
    public void initData(ShopTransaction order, double userBalance) {
        this.order = order;
        this.userBalance = userBalance;

        Product product = order.getProduct();

        // --- 填充UI控件 ---
        orderIdLabel.setText(order.getId() != null ? order.getId().toString() : "N/A");

        // 尝试加载图片，如果失败则不显示
        try {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                productImageView.setImage(new Image(product.getImageUrl(), true));
            }
        } catch (Exception e) {
            System.err.println("订单页面加载图片失败: " + e.getMessage());
        }

        productNameLabel.setText(product.getName());
        productPriceLabel.setText(String.format("¥ %.2f", product.getPrice()));
        productDescLabel.setText(product.getDescription());

        balanceLabel.setText(String.format("¥ %.2f", this.userBalance));
        totalPriceLabel.setText(String.format("¥ %.2f", order.getTotalPrice()));

        // --- 检查余额是否充足 ---
        if (this.userBalance < order.getTotalPrice()) {
            confirmButton.setDisable(true);
            confirmButton.setText("余额不足");
            balanceLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    /**
     * 处理用户点击“确认支付”按钮的事件。
     */
    @FXML
    void handleConfirmPurchase(ActionEvent event) {
        System.out.println("用户确认支付订单：" + order.getId());

        // TODO: 在这里调用一个新的 service 方法，向服务器发送最终的支付请求
        // 例如:
        // ShopService shopService = new ShopService();
        // shopService.payForOrder(order.getId());

        // 暂时先模拟成功，并关闭窗口。
        // 在真实的异步流程中，您应该在收到“支付成功”的响应后，再关闭窗口并显示成功提示。
        closeWindow();

        new Alert(Alert.AlertType.INFORMATION, "支付成功！(模拟)").showAndWait();
    }

    /**
     * 处理用户点击“取消”按钮的事件。
     */
    @FXML
    void handleCancel(ActionEvent event) {
        // 直接关闭窗口
        closeWindow();
    }

    /**
     * 一个辅助方法，用于关闭当前窗口。
     */
    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}