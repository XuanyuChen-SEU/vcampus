package com.vcampus.server.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vcampus.common.dao.IShopDao;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.OrderStatus;
import com.vcampus.common.enums.ProductStatus;
import com.vcampus.server.dao.impl.FakeShopDao;
import com.vcampus.server.dao.impl.ShopDao;

/**
 * 【重构版】商店模块的服务层 (ShopService)
 * 现在负责处理业务逻辑，并且在构造时负责【创建和注入测试数据】。
 */
public class ShopService {

    private final IShopDao shopDao;
    private final ShopDao realShopDao; // 新增：用于商店管理员功能的真实数据库DAO

    /**
     * 构造函数。
     * 1. 创建 DAO 实例。
     * 2. 创建所有测试用的假数据。
     * 3. 将假数据注入到 DAO 实例中。
     */
    public ShopService() {
        // 1. 先创建一个"空"的 FakeShopDao
        FakeShopDao fakeDao = new FakeShopDao();
        this.shopDao = fakeDao;
        this.realShopDao = new ShopDao(); // 初始化真实数据库DAO

        // 2. 创建所有假数据 (这段逻辑是从 FakeShopDao 的构造函数里搬过来的)
        List<Product> productTable = new ArrayList<>();
        List<ShopTransaction> orderTable = new ArrayList<>();
        List<ShopTransaction> favoriteTable = new ArrayList<>();

        // 初始化商品
        productTable.add(new Product(1L, "高品质笔记本", 15.50, 100, "非常适合记笔记的本子", "https://via.placeholder.com/160", ProductStatus.ON_SALE));
        productTable.add(new Product(2L, "多功能中性笔", 5.00, 200, "书写流畅，不断墨", "https://via.placeholder.com/160", ProductStatus.ON_SALE));
        productTable.add(new Product(3L, "二手教科书《Java核心技术》", 50.00, 10, "九成新，几乎无笔记", "https://via.placeholder.com/160", ProductStatus.ON_SALE));
        productTable.add(new Product(4L, "校园纪念T恤", 99.00, 50, "纯棉材质，舒适透气", "https://via.placeholder.com/160", ProductStatus.ON_SALE));

        // 初始化订单
        ShopTransaction order1 = new ShopTransaction(1001L, "1234567", 15.50, OrderStatus.PAID, LocalDateTime.now().minusDays(1), null);
        order1.setProduct(productTable.get(0));
        orderTable.add(order1);

        ShopTransaction order2 = new ShopTransaction(1002L, "1234567", 50.00, OrderStatus.RETURNED, LocalDateTime.now().minusDays(5), null);
        order2.setProduct(productTable.get(2));
        orderTable.add(order2);

        // 初始化收藏
        favoriteTable.add(new ShopTransaction(2001L, "1234567", productTable.get(3), LocalDateTime.now()));

        // 3. 将创建好的假数据列表，“喂”给 FakeShopDao
        System.out.println("ShopService: 正在将测试数据注入到 FakeShopDao...");
        fakeDao.setProductTable(productTable);
        fakeDao.setOrderTable(orderTable);
        fakeDao.setFavoriteTable(favoriteTable);
    }

    // --- 下面的所有业务逻辑方法保持不变 ---

    public List<Product> getAllProducts() {
        return realShopDao.getAllProducts();
    }

    public List<Product> searchProducts(String keyword) {
        // 商店管理员功能使用真实数据库DAO
        if (keyword == null || keyword.trim().isEmpty()) {
            return realShopDao.getAllProducts();
        }
        return realShopDao.searchProducts(keyword.trim());
    }

    public List<ShopTransaction> getMyOrders(String userId) {
        return shopDao.getOrdersByUserId(userId);
    }

    public List<ShopTransaction> getMyFavorites(String userId) {
        return shopDao.getFavoritesByUserId(userId);
    }
    public Product getProductDetail(String productId) {
        // 1. 在 Service 层进行业务逻辑校验
        if (productId == null || productId.trim().isEmpty()) {
            // 抛出异常，由 Controller 层捕获并处理
            throw new IllegalArgumentException("无效的请求数据：商品ID不能为空。");
        }

        // 2. 调用 DAO 层获取数据
        return shopDao.getProductById(productId);
    }

    // ==========================================================
    // 商店管理员相关方法 - 使用真实数据库DAO
    // ==========================================================

    /**
     * 添加新商品
     * @param product 商品信息
     * @return 是否添加成功
     */
    public boolean addProduct(Product product) {
        try {
            // 业务逻辑验证
            if (product == null) {
                throw new IllegalArgumentException("商品信息不能为空");
            }
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("商品名称不能为空");
            }
            if (product.getPrice() == null || product.getPrice() <= 0) {
                throw new IllegalArgumentException("商品价格必须大于0");
            }
            if (product.getStock() == null || product.getStock() < 0) {
                throw new IllegalArgumentException("库存不能为负数");
            }
            
            // 使用真实数据库DAO
            return realShopDao.addProduct(product);
        } catch (Exception e) {
            System.err.println("添加商品失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新商品信息
     * @param product 商品信息
     * @return 是否更新成功
     */
    public boolean updateProduct(Product product) {
        try {
            // 业务逻辑验证
            if (product == null || product.getId() == null) {
                throw new IllegalArgumentException("商品信息或ID不能为空");
            }
            
            // 使用真实数据库DAO
            return realShopDao.updateProductById(product);
        } catch (Exception e) {
            System.err.println("更新商品失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除商品
     * @param productId 商品ID
     * @return 是否删除成功
     */
    public boolean deleteProduct(String productId) {
        try {
            // 业务逻辑验证
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("商品ID不能为空");
            }
            
            // 使用真实数据库DAO
            return realShopDao.deleteProductById(productId);
        } catch (Exception e) {
            System.err.println("删除商品失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取所有订单（管理员功能）
     * @return 所有订单列表
     */
    public List<ShopTransaction> getAllOrders() {
        try {
            // 使用真实数据库DAO
            return realShopDao.getAllOrders();
        } catch (Exception e) {
            System.err.println("获取所有订单失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 获取所有收藏（管理员功能）
     * @return 所有收藏列表
     */
    public List<ShopTransaction> getAllFavorites() {
        try {
            // 使用真实数据库DAO
            return realShopDao.getAllFavorites();
        } catch (Exception e) {
            System.err.println("获取所有收藏失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 管理员根据用户ID获取订单
     * @param userId 用户ID
     * @return 该用户的所有订单
     */
    public List<ShopTransaction> getOrdersByUserIdForAdmin(String userId) {
        try {
            // 业务逻辑验证
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            // 使用真实数据库DAO
            return realShopDao.getOrdersByUserId(userId);
        } catch (Exception e) {
            System.err.println("管理员获取用户订单失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 管理员根据用户ID获取收藏
     * @param userId 用户ID
     * @return 该用户的所有收藏
     */
    public List<ShopTransaction> getFavoritesByUserIdForAdmin(String userId) {
        try {
            // 业务逻辑验证
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            
            // 使用真实数据库DAO
            return realShopDao.getFavoritesByUserId(userId);
        } catch (Exception e) {
            System.err.println("管理员获取用户收藏失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}