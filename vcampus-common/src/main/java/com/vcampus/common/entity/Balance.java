package com.vcampus.common.entity;

import java.math.BigDecimal;

/**
 * 用户余额实体类
 * 用于管理用户的账户余额信息
 * 编写人：AI Assistant
 */
public class Balance {
    
    /**
     * 用户ID（主键）
     */
    private String userId;
    
    /**
     * 用户余额
     */
    private BigDecimal balance;
    
    /**
     * 默认构造函数
     */
    public Balance() {
    }
    
    /**
     * 带参数的构造函数
     * @param userId 用户ID
     * @param balance 余额
     */
    public Balance(String userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }
    
    /**
     * 获取用户ID
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    /**
     * 获取余额
     * @return 余额
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * 设置余额
     * @param balance 余额
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    /**
     * 增加余额
     * @param amount 增加的金额
     */
    public void addBalance(BigDecimal amount) {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        this.balance = this.balance.add(amount);
    }
    
    /**
     * 减少余额
     * @param amount 减少的金额
     * @return 是否成功（余额足够返回true）
     */
    public boolean subtractBalance(BigDecimal amount) {
        if (this.balance == null || this.balance.compareTo(amount) < 0) {
            return false; // 余额不足
        }
        this.balance = this.balance.subtract(amount);
        return true;
    }
    
    /**
     * 检查余额是否足够
     * @param amount 需要的金额
     * @return 是否足够
     */
    public boolean hasEnoughBalance(BigDecimal amount) {
        return this.balance != null && this.balance.compareTo(amount) >= 0;
    }
    
    @Override
    public String toString() {
        return "Balance{" +
                "userId='" + userId + '\'' +
                ", balance=" + balance +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Balance balance1 = (Balance) obj;
        return userId != null ? userId.equals(balance1.userId) : balance1.userId == null;
    }
    
    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}
