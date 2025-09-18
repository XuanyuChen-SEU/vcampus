package com.vcampus.database.mapper;

import java.util.List;

import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.entity.Balance;

/**
 * 
 * @author 谌宣羽
 */
public interface ShopMapper {
    
    // ==========================================================
    // 数据库表管理操作
    // ==========================================================
    
    /**
     * 从CSV文件批量导入商品数据
     * @param filePath CSV文件的完整路径
     */
    void loadProductsFromCsv(String filePath);

    /**
     * 从CSV文件批量导入订单数据
     * @param filePath CSV文件的完整路径
     */
    void loadOrdersFromCsv(String filePath);

    /**
     * 从CSV文件批量导入收藏数据
     * @param filePath CSV文件的完整路径
     */
    void loadFavoritesFromCsv(String filePath);

    /*  
     * 从CSV文件批量导入余额数据
     * @param filePath CSV文件的完整路径
     */
    void loadBalancesFromCsv(String filePath);
    
    // ==========================================================
    // 商品(Product)相关操作
    // ==========================================================
    
    /*
     * 获取所有商品
     * @return 所有商品的列表
     */
    List<Product> getAllProducts();
    
    /*
     * 根据商品ID获取商品详情
     * @param productId 商品ID
     * @return 对应的商品对象，如果不存在则返回null
     */
    Product getProductById(Long productId);
    
    /*
     * 根据关键词模糊搜索商品名称
     * @param keyword 搜索关键词
     * @return 符合条件的商品列表
     */
    List<Product> searchProducts(String keyword);

    /*
     * 添加一个新的商品
     * @param product 包含新商品信息的商品对象
     * @return 如果添加成功，返回 true；否则返回 false
     */
    boolean addProduct(Product product);
    
    /*
     * 更新商品信息
     * @param product 包含更新信息的商品对象
     * @return 如果更新成功，返回 true；否则返回 false
     */
    boolean updateProductById(Product product);
    
    /*
     * 删除一个商品
     * @param productId 商品ID
     * @return 如果删除成功，返回 true；否则返回 false
     */
    boolean deleteProductById(String productId);
    
    // ==========================================================
    // 订单(Order)相关操作
    // ==========================================================
    
    /*
     * 获取所有订单
     * @return 所有订单的列表
     */
    List<ShopTransaction> getAllOrders();
    
    /*
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 对应的订单对象，如果不存在则返回null
     */
    ShopTransaction getOrderById(String orderId);
    
    /*
     * 根据用户ID获取该用户的所有订单
     * @param userId 用户ID
     * @return 该用户的所有订单列表
     */
    List<ShopTransaction> getOrdersByUserId(String userId);
    
    /*
     * 保存一个新的订单
     * @param order 包含新订单信息的 ShopTransaction 对象
     * @return 如果保存成功，返回 true；否则返回 false
     */
    boolean saveOrder(ShopTransaction order);
    
    // ==========================================================
    // 收藏(Favorite)相关操作
    // ==========================================================

    /*
     * 获取所有收藏
     * @return 所有收藏的列表
     */
    List<ShopTransaction> getAllFavorites();
    
    /*
     * 根据用户ID获取该用户的所有收藏
     * @param userId 用户ID
     * @return 该用户的所有收藏列表
     */
    List<ShopTransaction> getFavoritesByUserId(String userId);
    
    /*
     * 添加一个新的收藏记录
     * @param favorite 包含收藏信息的 ShopTransaction 对象
     * @return 如果添加成功，返回 true；否则返回 false
     */
    boolean addFavorite(ShopTransaction favorite);
    
    /*
     * 移除一个收藏记录
     * @param favoriteId 收藏记录的唯一ID
     * @return 如果移除成功，返回 true；否则返回 false
     */
    boolean removeFavorite(String favoriteId);

    // ===========
    // 余额(Balance)相关操作
    // ==========================================================

    /*
     * 根据用户id获取余额
     * @param userId 用户id
     * @return 对应的余额对象，如果不存在则返回null
     */
    Balance getBalanceByUserId(String userId);

    /*
     * 更新余额
     * @param balance 包含更新信息的余额对象
     * @return 如果更新成功，返回 true；否则返回 false
     */
    boolean updateBalance(Balance balance);
    
    /*
     * 创建余额记录
     * @param balance 包含用户ID和初始余额的余额对象
     * @return 如果创建成功，返回 true；否则返回 false
     */
    boolean createBalance(Balance balance);
    
    /*
     * 根据用户ID删除余额记录
     * @param userId 用户ID
     * @return 如果删除成功，返回 true；否则返回 false
     */
    boolean deleteBalanceByUserId(String userId);
    
    boolean updateOrder(ShopTransaction order);
}
