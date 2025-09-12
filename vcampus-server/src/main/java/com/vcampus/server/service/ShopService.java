package com.vcampus.server.service;

import com.vcampus.common.dao.IShopDao;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.enums.OrderStatus;
import com.vcampus.common.enums.ProductStatus;
import com.vcampus.server.dao.impl.FakeShopDao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 【重构版】商店模块的服务层 (ShopService)
 * 现在负责处理业务逻辑，并且在构造时负责【创建和注入测试数据】。
 */
public class ShopService {

    private final IShopDao shopDao;

    /**
     * 构造函数。
     * 1. 创建 DAO 实例。
     * 2. 创建所有测试用的假数据。
     * 3. 将假数据注入到 DAO 实例中。
     */
    public ShopService() {
        // 1. 先创建一个“空”的 FakeShopDao
        FakeShopDao fakeDao = new FakeShopDao();
        this.shopDao = fakeDao;

        // 2. 创建所有假数据 (这段逻辑是从 FakeShopDao 的构造函数里搬过来的)
        List<Product> productTable = new ArrayList<>();
        List<ShopTransaction> orderTable = new ArrayList<>();
        List<ShopTransaction> favoriteTable = new ArrayList<>();

        // 初始化商品
        productTable.add(new Product(1L, "高品质笔记本", "文具", 15.50, 100, "非常适合记笔记的本子", "https://via.placeholder.com/160", 1.0, ProductStatus.ON_SALE));
        productTable.add(new Product(2L, "多功能中性笔", "文具", 5.00, 200, "书写流畅，不断墨", "https://via.placeholder.com/160", 1.0, ProductStatus.ON_SALE));
        productTable.add(new Product(3L, "二手教科书《Java核心技术》", "图书", 50.00, 10, "九成新，几乎无笔记", "https://via.placeholder.com/160", 1.0, ProductStatus.ON_SALE));
        productTable.add(new Product(4L, "校园纪念T恤", "纪念品", 99.00, 50, "纯棉材质，舒适透气", "https://via.placeholder.com/160", 0.8, ProductStatus.ON_SALE));

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
        return shopDao.getAllProducts();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return shopDao.getAllProducts();
        }
        return shopDao.searchProducts(keyword.trim());
    }

    public List<ShopTransaction> getMyOrders(String userId) {
        return shopDao.getOrdersByUserId(userId);
    }

    public List<ShopTransaction> getMyFavorites(String userId) {
        return shopDao.getFavoritesByUserId(userId);
    }
}