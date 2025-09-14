package com.vcampus.server.test;

import java.util.List;
import java.time.LocalDateTime;

import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.OrderStatus;
import com.vcampus.server.dao.impl.ShopDao;

/**
 * ShopDao测试类
 * 用于测试ShopDao的各种功能
 * 编写人：谌宣羽
 */
public class ShopDaoTest {
    
    private ShopDao shopDao;
    
    public ShopDaoTest() {
        this.shopDao = new ShopDao();
    }
    
    /**
     * 检查MyBatis是否可用
     */
    public boolean checkMyBatisAvailable() {
        try {
            Class.forName("org.apache.ibatis.session.SqlSessionFactory");
            System.out.println("✅ MyBatis依赖可用");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MyBatis依赖不可用: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试商品相关功能
     */
    public void testProductOperations() {
        System.out.println("=== 测试商品相关功能 ===");
        
        // 1. 测试获取所有商品
        System.out.println("1. 测试获取所有商品:");
        List<Product> allProducts = shopDao.getAllProducts();
        System.out.println("   商品总数: " + allProducts.size());
        if (!allProducts.isEmpty()) {
            Product firstProduct = allProducts.get(0);
            System.out.println("   第一个商品: " + firstProduct.getName() + " - ¥" + firstProduct.getPrice());
        }
        
        // 2. 测试根据ID获取商品
        System.out.println("\n2. 测试根据ID获取商品:");
        Product product = shopDao.getProductById("1");
        if (product != null) {
            System.out.println("   找到商品: " + product.getName() + " - ¥" + product.getPrice());
        } else {
            System.out.println("   未找到ID为1的商品");
        }
        
        // 3. 测试关键词搜索
        System.out.println("\n3. 测试关键词搜索:");
        List<Product> searchResults = shopDao.searchProducts("笔记本");
        System.out.println("   搜索'笔记本'结果数量: " + searchResults.size());
        for (Product p : searchResults) {
            System.out.println("   - " + p.getName() + " - ¥" + p.getPrice());
        }
        
        // 4. 测试更新商品
        System.out.println("\n4. 测试更新商品:");
        if (product != null) {
            product.setPrice(product.getPrice() + 1.0);
            boolean updateResult = shopDao.updateProductById(product);
            System.out.println("   更新结果: " + (updateResult ? "成功" : "失败"));
        }
    }
    
    /**
     * 测试订单相关功能
     */
    public void testOrderOperations() {
        System.out.println("\n=== 测试订单相关功能 ===");
        
        // 1. 测试获取所有订单
        System.out.println("1. 测试获取所有订单:");
        try {
            List<ShopTransaction> allOrders = shopDao.getAllOrders();
            System.out.println("   订单总数: " + allOrders.size());
            if (!allOrders.isEmpty()) {
                ShopTransaction firstOrder = allOrders.get(0);
                System.out.println("   第一个订单: " + firstOrder.getOrderId() + " - 用户: " + firstOrder.getUserId());
            }
        } catch (Exception e) {
            System.err.println("   ❌ 获取订单失败: " + e.getMessage());
            System.err.println("   可能数据库中有无效的orderStatus值，请清理数据库后重试");
            return;
        }
        
        // 2. 测试根据ID获取订单
        System.out.println("\n2. 测试根据ID获取订单:");
        try {
            ShopTransaction order = shopDao.getOrderById("ORD001");
            if (order != null) {
                System.out.println("   找到订单: " + order.getOrderId() + " - 总价: ¥" + order.getTotalPrice());
                if (order.getProduct() != null) {
                    System.out.println("   商品信息: " + order.getProduct().getName() + " - 数量: " + order.getQuantity());
                }
            } else {
                System.out.println("   未找到ID为ORD001的订单");
            }
        } catch (Exception e) {
            System.err.println("   ❌ 根据ID获取订单失败: " + e.getMessage());
        }
        
        // 3. 测试根据用户ID获取订单
        System.out.println("\n3. 测试根据用户ID获取订单:");
        try {
            List<ShopTransaction> userOrders = shopDao.getOrdersByUserId("1234567");
            System.out.println("   用户1234567的订单数量: " + userOrders.size());
            for (ShopTransaction o : userOrders) {
                System.out.println("   - " + o.getOrderId() + " - ¥" + o.getTotalPrice() + " - " + o.getOrderStatus());
            }
        } catch (Exception e) {
            System.err.println("   ❌ 根据用户ID获取订单失败: " + e.getMessage());
        }
        
        // 4. 测试保存新订单
        System.out.println("\n4. 测试保存新订单:");
        // 先获取一个商品作为订单商品
        Product testProduct = shopDao.getProductById("1");
        if (testProduct != null) {
            ShopTransaction newOrder = new ShopTransaction();
            newOrder.setOrderId("TEST001"); // 使用String类型的orderId
            newOrder.setUserId("1234567");
            newOrder.setProduct(testProduct); // 设置Product对象
            newOrder.setQuantity(2);
            newOrder.setPriceAtPurchase(15.50);
            newOrder.setTotalPrice(31.00);
            newOrder.setOrderStatus(OrderStatus.UNPAID);
            newOrder.setCreateTime(LocalDateTime.now());
            
            boolean saveResult = shopDao.saveOrder(newOrder);
            System.out.println("   保存订单结果: " + (saveResult ? "成功" : "失败"));
        } else {
            System.out.println("   无法获取测试商品，跳过订单保存测试");
        }
    }
    
    /**
     * 测试收藏相关功能
     */
    public void testFavoriteOperations() {
        System.out.println("\n=== 测试收藏相关功能 ===");
        
        // 1. 测试获取所有收藏
        System.out.println("1. 测试获取所有收藏:");
        List<ShopTransaction> allFavorites = shopDao.getAllFavorites();
        System.out.println("   收藏总数: " + allFavorites.size());
        if (!allFavorites.isEmpty()) {
            ShopTransaction firstFavorite = allFavorites.get(0);
            System.out.println("   第一个收藏: 用户" + firstFavorite.getUserId() + " - 商品: " + 
                (firstFavorite.getProduct() != null ? firstFavorite.getProduct().getName() : "未知"));
        }
        
        // 2. 测试根据用户ID获取收藏
        System.out.println("\n2. 测试根据用户ID获取收藏:");
        List<ShopTransaction> userFavorites = shopDao.getFavoritesByUserId("1234567");
        System.out.println("   用户1234567的收藏数量: " + userFavorites.size());
        for (ShopTransaction f : userFavorites) {
            System.out.println("   - 商品: " + (f.getProduct() != null ? f.getProduct().getName() : "未知") + 
                " - 收藏时间: " + f.getAddTime());
        }
        
        // 3. 测试添加收藏
        System.out.println("\n3. 测试添加收藏:");
        // 先获取一个商品作为收藏商品
        Product favoriteProduct = shopDao.getProductById("2");
        if (favoriteProduct != null) {
            ShopTransaction newFavorite = new ShopTransaction();
            newFavorite.setUserId("1234567");
            newFavorite.setProduct(favoriteProduct); // 设置Product对象
            newFavorite.setAddTime(LocalDateTime.now());
            
            boolean addResult = shopDao.addFavorite(newFavorite);
            System.out.println("   添加收藏结果: " + (addResult ? "成功" : "失败"));
        } else {
            System.out.println("   无法获取测试商品，跳过收藏添加测试");
        }
        
        // 4. 测试删除收藏
        System.out.println("\n4. 测试删除收藏:");
        boolean removeResult = shopDao.removeFavorite("1");
        System.out.println("   删除收藏结果: " + (removeResult ? "成功" : "失败"));
    }
    
    /**
     * 运行所有测试
     */
    public void runAllTests() {
        System.out.println("开始测试ShopDao...");
        System.out.println("=====================================");
        
        // 首先检查MyBatis是否可用
        if (!checkMyBatisAvailable()) {
            System.err.println("❌ 无法运行测试，MyBatis依赖不可用");
            System.err.println("请确保：");
            System.err.println("1. 数据库服务正在运行");
            System.err.println("2. MyBatis配置正确");
            System.err.println("3. 所有依赖已正确安装");
            return;
        }
        
        try {
            testProductOperations();
            testOrderOperations();
            testFavoriteOperations();
            
            System.out.println("\n=====================================");
            System.out.println("所有测试完成！");
            
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 主方法 - 运行测试
     */
    public static void main(String[] args) {
        ShopDaoTest test = new ShopDaoTest();
        test.runAllTests();
    }
}
