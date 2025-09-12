package com.vcampus.common.dto;

// 确保这些 import 语句是正确的
import com.vcampus.common.enums.OrderStatus;
import com.vcampus.common.enums.ShopApplicationStatus; // 修正为您项目中的正确类名
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 一个“多功能复合体”，用于承载所有和用户个人交易相关的记录。(最终修正版)
 * 字段和构造函数已根据数据库表结构进行明确分离，避免混淆。
 */
public class ShopTransaction implements Serializable {

    // 推荐为可序列化类添加serialVersionUID，每次重大不兼容修改后建议更新版本号
    private static final long serialVersionUID = 3L;

    // --- 通用字段 ---
    private Long id;
    private String userId;

    // --- 订单(Order) 和 订单项(OrderItem) 相关字段 ---
    private Long orderId;
    private Product product;
    private Integer quantity;
    private Double priceAtPurchase;
    private Double totalPrice;
    private OrderStatus orderStatus;
    private LocalDateTime createTime;  // 明确的订单创建时间
    private LocalDateTime payTime;     // 明确的订单支付时间

    // --- 收藏(Favorite) 相关字段 ---
    private LocalDateTime addTime;

    // --- 退货(ReturnRequest) 相关字段 ---
    private Long relatedOrderId;
    private String reason;
    private ShopApplicationStatus returnStatus; // 已修正为 ShopApplicationStatus
    private LocalDateTime submitTime;  // 明确的退货申请提交时间
    private LocalDateTime reviewTime;
    private String reviewerId;

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
    public ShopTransaction(Long orderId, Product product, Integer quantity, Double priceAtPurchase) {
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

    // --- 退货申请 (ReturnRequest) ---
    /**
     * 用于创建新退货申请的构造函数
     */
    public ShopTransaction(String userId, Long relatedOrderId, String reason) {
        this.userId = userId;
        this.relatedOrderId = relatedOrderId;
        this.reason = reason;
        this.returnStatus = ShopApplicationStatus.PENDING;
        this.submitTime = LocalDateTime.now();
    }

    /**
     * 用于从数据库完整加载退货申请的构造函数
     */
    public ShopTransaction(Long id, String userId, Long relatedOrderId, String reason, ShopApplicationStatus returnStatus, LocalDateTime submitTime, LocalDateTime reviewTime, String reviewerId) {
        this.id = id;
        this.userId = userId;
        this.relatedOrderId = relatedOrderId;
        this.reason = reason;
        this.returnStatus = returnStatus;
        this.submitTime = submitTime;
        this.reviewTime = reviewTime;
        this.reviewerId = reviewerId;
    }


    // --- 所有字段的 Getter 和 Setter 方法 ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
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
    public Long getRelatedOrderId() { return relatedOrderId; }
    public void setRelatedOrderId(Long relatedOrderId) { this.relatedOrderId = relatedOrderId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public ShopApplicationStatus getReturnStatus() { return returnStatus; }
    public void setReturnStatus(ShopApplicationStatus returnStatus) { this.returnStatus = returnStatus; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
}