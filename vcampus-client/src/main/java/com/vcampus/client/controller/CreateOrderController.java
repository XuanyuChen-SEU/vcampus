package com.vcampus.client.controller;

import com.vcampus.client.service.ShopService; // 1. 【新增】导入ShopService
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

import java.io.ByteArrayInputStream;

/**
 * 确认订单窗口的控制器。
 * 负责显示订单详情、检查余额，并向服务器发送最终的支付请求。
 */
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

    // --- 成员变量 ---
    private ShopTransaction currentOrder; // 用于保存当前正在处理的订单
    private double userBalance;           // 用于保存用户的余额
    private final ShopService shopService = new ShopService(); // 2. 【新增】创建Service实例，用于和服务器通信

    /**
     * FXML 初始化方法，在加载界面时自动调用。
     */
    @FXML
    void initialize() {
        // 可以在这里做一些初始设置，如果需要的话
    }

    /**
     * 由主控制器(ShopController)调用，用于在显示窗口前注入必要的数据。
     *
     * @param order       服务器创建的、待支付的订单对象
     * @param userBalance 用户当前的账户余额
     */
    public void initData(ShopTransaction order, double userBalance) {
        this.currentOrder = order;
        this.userBalance = userBalance;

        Product product = order.getProduct();
        if (product == null) {
            System.err.println("严重错误：传入的订单对象中不包含商品信息！");
            return;
        }

        // --- 填充UI控件 ---
        // 3. 【修正】使用 getOrderId() 来获取字符串类型的订单号，更可靠
        orderIdLabel.setText(order.getOrderId());

        // 尝试加载图片
        try {
            if (product.getImageData() != null && product.getImageData().length > 0) {
                productImageView.setImage(new Image(new ByteArrayInputStream(product.getImageData())));
            } else if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                productImageView.setImage(new Image(product.getImagePath()));
            }
        } catch (Exception e) {
            System.err.println("订单页面加载图片失败: " + e.getMessage());
        }

        productNameLabel.setText(product.getName());
        productPriceLabel.setText(String.format("¥ %.2f", product.getPrice()));
        productDescLabel.setText(product.getDescription());

        balanceLabel.setText(String.format("¥ %.2f", this.userBalance));
        totalPriceLabel.setText(String.format("¥ %.2f", order.getTotalPrice()));

        // --- 检查余额是否充足，并更新UI状态 ---
        if (this.userBalance < order.getTotalPrice()) {
            confirmButton.setDisable(true); // 禁用按钮
            confirmButton.setText("余额不足");   // 修改按钮文本
            balanceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;"); // 将余额显示为红色
        }
    }

    /**
     * 处理用户点击“确认支付”按钮的事件。
     * 【核心修改】将模拟支付替换为向服务器发送真实的支付请求。
     */
    @FXML
    void handleConfirmPurchase(ActionEvent event) {
        // 4. 【核心逻辑】
        System.out.println("用户确认支付，订单ID: " + this.currentOrder.getOrderId());

        // a. 调用ShopService，向服务器异步发送“支付订单”的请求
        shopService.payForOrder(this.currentOrder);

        // b. 请求发送后，此窗口的任务已经完成，直接关闭即可。
        //    服务器的响应将由主控制器(ShopController)的响应处理器来接收，
        //    并由主控制器负责更新余额和显示最终的成功/失败提示。
        closeWindow();
    }

    /**
     * 处理用户点击“取消”按钮的事件。
     */
    @FXML
    void handleCancel(ActionEvent event) {
        System.out.println("用户取消了支付。");
        closeWindow();
    }

    /**
     * 一个辅助方法，用于获取当前窗口的Stage并关闭它。
     */
    private void closeWindow() {
        // 从任意一个控件获取其所在的场景(Scene)，再从场景获取窗口(Stage)
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}