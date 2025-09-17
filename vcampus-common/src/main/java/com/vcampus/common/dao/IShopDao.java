package com.vcampus.common.dao;

import java.util.List;

import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.entity.Balance;

/**
 * 商店数据访问对象接口 (IShopDao)
 * 定义了所有与商店模块相关的数据库操作。
 * 编写人：王思懿
 */
public interface IShopDao {

    // ==========================================================
    // 商品 (Product) 相关操作
    // ==========================================================

    /*
     * 获取所有商品。
     * @return 包含所有商品信息的列表 (List<Product>)。
     */
    List<Product> getAllProducts();

    /*
     * 根据商品ID获取商品详情。
     * @param productId 商品的唯一ID。
     * @return 对应的商品对象 (Product)，如果不存在则返回 null。
     */
    Product getProductById(String productId);

    /*
     * 根据关键词模糊搜索商品名称。
     * @param keyword 搜索关键词。
     * @return 符合条件的商品列表 (List<Product>)。
     */
    List<Product> searchProducts(String keyword);

    /*
     * 更新商品信息
     */
    boolean updateProductById(Product product);

    // ==========================================================
    // 订单 (Order) 相关操作
    // ==========================================================

    /*
     * 获取所有订单
     */
    List<ShopTransaction> getAllOrders();

    /*
     * 根据id获取订单
     */
    ShopTransaction getOrderById(String orderId);

    /*
     * 根据用户ID获取该用户的所有订单。
     * @param userId 用户的ID。
     * @return 包含该用户所有订单信息的列表 (List<ShopTransaction>)。
     */
    List<ShopTransaction> getOrdersByUserId(String userId);

    /*
     * 保存一个新的订单到数据库。
     * @param order 包含新订单信息的 ShopTransaction 对象。
     * @return 如果保存成功，返回 true；否则返回 false。
     */
    boolean saveOrder(ShopTransaction order);

    // ==========================================================
    // 收藏 (Favorite) 相关操作
    // ==========================================================

    /*
     * 获取所有收藏
     */
    List<ShopTransaction> getAllFavorites();

    /*
     * 根据用户ID获取该用户的所有收藏。
     * @param userId 用户的ID。
     * @return 包含该用户所有收藏信息的列表 (List<ShopTransaction>)。
     */
    List<ShopTransaction> getFavoritesByUserId(String userId);

    /*
     * 添加一个新的收藏记录。
     * @param favorite 包含收藏信息的 ShopTransaction 对象 (通常包含 userId 和 productId)。
     * @return 如果添加成功，返回 true；否则返回 false。
     */
    boolean addFavorite(ShopTransaction favorite);

    /*
     * 移除一个收藏记录。
     * @param favoriteId 收藏记录的唯一ID。
     * @return 如果移除成功，返回 true；否则返回 false。
     */
    boolean removeFavorite(String favoriteId);

    // ==========================================================
    // 余额 (Balance) 相关操作
    // ==========================================================

    /*
     * 根据用户id获取余额
     */
    Balance getBalanceByUserId(String userId);

    /*
     * 更新余额
     */
    boolean updateBalance(Balance balance);
    boolean updateOrder(ShopTransaction order);
}