package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.IShopDao;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 【重构版】一个模拟的 ShopDao 实现。
 * 它现在是一个纯粹的数据容器，不再自己创建假数据。
 * 数据由 Service 层通过 setter 方法注入。
 */
public class FakeShopDao implements IShopDao {

    // 【修改1】移除了 final 关键字，以便 setter 方法可以赋值
    private List<Product> productTable = new ArrayList<>();
    private List<ShopTransaction> orderTable = new ArrayList<>();
    private List<ShopTransaction> favoriteTable = new ArrayList<>();

    /**
     * 【修改2】构造函数现在是空的，不再创建任何假数据。
     */
    public FakeShopDao() {
        // Constructor is now empty. Data will be injected from the Service layer.
    }

    // ==================【这是新增的核心部分】==================
    //  为每个数据列表添加一个 public 的 setter 方法，作为数据注入的入口。
    // ==========================================================

    public void setProductTable(List<Product> products) {
        this.productTable = products;
    }

    public void setOrderTable(List<ShopTransaction> orders) {
        this.orderTable = orders;
    }

    public void setFavoriteTable(List<ShopTransaction> favorites) {
        this.favoriteTable = favorites;
    }

    // --- 下面的所有数据操作方法保持不变 ---

    @Override
    public void SDInit() { System.out.println("Fake DAO Initialized."); }

    @Override
    public List<Product> getAllProducts() {
        System.out.println("FAKE DAO: 返回所有商品...");
        return productTable;
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        System.out.println("FAKE DAO: 搜索商品: " + keyword);
        return productTable.stream()
                .filter(p -> p.getName().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShopTransaction> getOrdersByUserId(String userId) {
        System.out.println("FAKE DAO: 为用户 " + userId + " 获取订单...");
        return orderTable.stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShopTransaction> getFavoritesByUserId(String userId) {
        System.out.println("FAKE DAO: 为用户 " + userId + " 获取收藏...");
        return favoriteTable.stream()
                .filter(f -> f.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Product getProductById(String productId) { return null; }
    @Override
    public boolean saveOrder(ShopTransaction order) { return true; }
    @Override
    public boolean addFavorite(ShopTransaction favorite) { return true; }
    @Override
    public boolean removeFavorite(String favoriteId) { return true; }
    @Override
    public void SDClose() { System.out.println("Fake DAO Closed."); }
}