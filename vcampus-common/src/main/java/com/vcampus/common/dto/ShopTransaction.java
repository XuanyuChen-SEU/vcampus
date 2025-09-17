package com.vcampus.common.dto;

// 确保这些 import 语句是正确的
import java.io.Serializable;
import java.time.LocalDateTime; // 修正为您项目中的正确类名

import com.vcampus.common.enums.OrderStatus;

/**
 * 一个“多功能复合体”，用于承载所有和用户个人交易相关的记录。(最终修正版)
 * 字段和构造函数已根据数据库表结构进行明确分离，避免混淆。
 */
public class ShopTransaction implements Serializable {

    // 推荐为可序列化类添加serialVersionUID，每次重大不兼容修改后建议更新版本号
    private static final long serialVersionUID = 3L;

    // --- 通用字段 ---
    private Long id;//收藏id
    private String userId;

    // --- 订单(Order) 和 订单项(OrderItem) 相关字段 ---
    private String orderId;
    private Product product;
    private Integer quantity;
    private Double priceAtPurchase;
    private Double totalPrice;
    private OrderStatus orderStatus;
    private LocalDateTime createTime;  // 明确的订单创建时间
    private LocalDateTime payTime;     // 明确的订单支付时间

    // --- 收藏(Favorite) 相关字段 ---
    private LocalDateTime addTime;

    // --- 商店用户信息(ShopUser) 相关字段 ---
    private String cardNumber;
    private Double balance;

    // --- 构造函数 ---

    //这后面所有同一功能两个构造函数的都是第一个是创建对象 另一个是读取查看该功能相关数据
    /**
     * 默认构造函数
     */
    public ShopTransaction() {
    }

    // --- 商店用户信息 (ShopUser) ---
    /**
     * 用于封装/加载商店用户信息的构造函数
     */
    public ShopTransaction(String userId, String cardNumber, Double balance) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    // --- 订单 (Order) ---
    /**
     * 用于创建新订单的构造函数
     */
    public ShopTransaction(String userId, Double totalPrice) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderStatus = OrderStatus.UNPAID;
        this.createTime = LocalDateTime.now();
        this.payTime = null;
    }

    /**
     * 用于从数据库完整加载订单信息的构造函数
     */
    public ShopTransaction(Long id, String userId, Double totalPrice, OrderStatus orderStatus, LocalDateTime createTime, LocalDateTime payTime) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createTime = createTime;
        this.payTime = payTime;
    }

    // --- 订单项 (OrderItem) ---
    /**
     * 用于创建/加载订单项的构造函数
     */
    public ShopTransaction(String orderId, Product product, Integer quantity, Double priceAtPurchase) {
        this.id = null; // 订单项通常在数据库中有自己的ID，这里设为null表示是新创建的内存对象
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    // --- 收藏 (Favorite) ---
    /**
     * 用于创建新收藏的构造函数
     */
    public ShopTransaction(String userId, Product product) {
        this.userId = userId;
        this.product = product;
        this.addTime = LocalDateTime.now();
    }

    /**
     * 用于从数据库完整加载收藏记录的构造函数
     */
    public ShopTransaction(Long id, String userId, Product product, LocalDateTime addTime) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.addTime = addTime;
    }



    // --- 所有字段的 Getter 和 Setter 方法 ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(Double priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
    public LocalDateTime getAddTime() { return addTime; }
    public void setAddTime(LocalDateTime addTime) { this.addTime = addTime; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShopTransaction{");

        // --- 始终打印通用字段 ---
        if (id != null) {
            sb.append("id=").append(id).append(", ");
        }
        if (userId != null) {
            sb.append("userId='").append(userId).append("', ");
        }

        // --- 根据角色打印特定字段 ---

        // 检查是否像一个“订单” (有 orderId 或 orderStatus)
        if (orderId != null || orderStatus != null) {
            sb.append("role=ORDER, ");
            if (orderId != null) {
                sb.append("orderId='").append(orderId).append("', ");
            }
            if (totalPrice != null) {
                sb.append("totalPrice=").append(totalPrice).append(", ");
            }
            if (orderStatus != null) {
                sb.append("orderStatus=").append(orderStatus).append(", ");
            }
            if (createTime != null) {
                sb.append("createTime=").append(createTime).append(", ");
            }
            if (payTime != null) {
                sb.append("payTime=").append(payTime).append(", ");
            }
        }

        // 检查是否像一个“订单项” (有 quantity)
        if (quantity != null) {
            sb.append("role=ORDER_ITEM, ");
            if (quantity != null) {
                sb.append("quantity=").append(quantity).append(", ");
            }
            if (priceAtPurchase != null) {
                sb.append("priceAtPurchase=").append(priceAtPurchase).append(", ");
            }
        }

        // 检查是否像一个“收藏” (有 addTime)
        if (addTime != null) {
            sb.append("role=FAVORITE, ");
            sb.append("addTime=").append(addTime).append(", ");
        }

        // 检查是否像一个“用户信息” (有 balance 或 cardNumber)
        if (balance != null || cardNumber != null) {
            sb.append("role=USER_INFO, ");
            if (cardNumber != null) {
                sb.append("cardNumber='").append(cardNumber).append("', ");
            }
            if (balance != null) {
                sb.append("balance=").append(balance).append(", ");
            }
        }

        // --- 打印关联的 Product 对象 (如果存在) ---
        if (product != null) {
            // 只打印商品的关键信息，避免无限循环或信息过载
            sb.append("product={id=").append(product.getId())
                    .append(", name='").append(product.getName()).append("'}, ");
        }

        // --- 清理末尾多余的 ", " ---
        if (sb.charAt(sb.length() - 2) == ',' && sb.charAt(sb.length() - 1) == ' ') {
            sb.setLength(sb.length() - 2);
        }

        sb.append('}');
        return sb.toString();
    }
}
