package com.vcampus.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.server.dao.impl.ShopDao;

/**
 * 【重构版】商店模块的服务层 (ShopService)
 * 现在负责处理业务逻辑，并且在构造时负责【创建和注入测试数据】。
 */
public class ShopService {

    private final ShopDao shopDao;

    /**
     * 构造函数。
     * 1. 创建 DAO 实例。
     * 2. 创建所有测试用的假数据。
     * 3. 将假数据注入到 DAO 实例中。
     */
    public ShopService() {

        // 1. 先创建一个“空”的 FakeShopDao
        this.shopDao = new ShopDao();

    }

    // --- 下面的所有业务逻辑方法保持不变 ---

    public List<Product> getAllProducts() {
        List<Product> products = shopDao.getAllProducts();
        System.out.println("=== ShopService.getAllProducts 返回 " + products.size() + " 个商品 ===");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("  位置 " + i + ": " + product.getName() + " (ID: " + product.getId() + ")");
        }
        // 为每个商品加载图片数据
        for (Product product : products) {
            loadProductImage(product);
        }
        System.out.println("=== 商品列表处理完成 ===");
        return products;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products;
        if (keyword == null || keyword.trim().isEmpty()) {
            products = shopDao.getAllProducts();
        } else {
            products = shopDao.searchProducts(keyword.trim());
        }
        // 为每个商品加载图片数据
        for (Product product : products) {
            loadProductImage(product);
        }
        return products;
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

        System.out.println("ShopService.getProductDetail 被调用，商品ID: " + productId);
        
        // 2. 调用 DAO 层获取数据
        Product product = shopDao.getProductById(productId);
        if (product != null) {
            System.out.println("找到商品: " + product.getName() + " (ID: " + product.getId() + ")");
            // 加载图片数据
            loadProductImage(product);
        } else {
            System.out.println("未找到商品，ID: " + productId);
        }
        return product;
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
            return shopDao.addProduct(product);
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
            return shopDao.updateProductById(product);
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
            return shopDao.deleteProductById(productId);
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
            return shopDao.getAllOrders();
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
            return shopDao.getAllFavorites();
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
            return shopDao.getOrdersByUserId(userId);
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
            return shopDao.getFavoritesByUserId(userId);
        } catch (Exception e) {
            System.err.println("管理员获取用户收藏失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 【已增强】创建订单的业务逻辑，使用异常处理来明确失败原因。
     * @param orderRequest 包含用户和商品信息的请求
     * @return 创建成功则返回包含了数据库ID的完整订单对象
     * @throws RuntimeException 如果库存不足或数据库保存失败
     */
    public ShopTransaction createOrder(ShopTransaction orderRequest) {
        Product product = shopDao.getProductById(orderRequest.getProduct().getId().toString());

        if (product == null) {
            // 明确抛出异常，而不是返回null
            throw new RuntimeException("商品不存在或已下架");
        }
        if (product.getStock() <= 0) {
            // 明确抛出异常
            throw new RuntimeException("商品库存不足");
        }

        product.setStock(product.getStock() - 1);
        shopDao.updateProductById(product);

        ShopTransaction newOrder = new ShopTransaction(orderRequest.getUserId(), product.getPrice());
        newOrder.setProduct(product);

        boolean success = shopDao.saveOrder(newOrder);

        if (!success) {
            // 明确抛出异常
            throw new RuntimeException("数据库保存订单失败");
        }

        return newOrder;
    }

    /**
     * 【已补全】添加收藏的业务逻辑，增加了重复收藏检查。
     * @param favoriteRequest 包含用户ID和商品信息的请求
     * @return 如果成功返回 true, 否则返回 false
     */
    public boolean addFavorite(ShopTransaction favoriteRequest) {
        // 1. 从请求中获取关键信息
        String userId = favoriteRequest.getUserId();
        Long productId = favoriteRequest.getProduct().getId();

        // 【核心逻辑】在添加之前，先检查用户是否已经收藏过此商品
        // a. 获取该用户的所有收藏记录
        List<ShopTransaction> userFavorites = shopDao.getFavoritesByUserId(userId);

        // b. 检查这些记录中是否已包含当前要收藏的商品ID
        boolean alreadyFavorited = userFavorites.stream()
                .anyMatch(fav -> fav.getProduct() != null && fav.getProduct().getId().equals(productId));

        // 2. 如果已经收藏过，直接返回 false，表示操作“失败”（因为无需再次添加）
        if (alreadyFavorited) {
            System.out.println("业务逻辑：用户 " + userId + " 尝试重复收藏商品 " + productId + "，操作被阻止。");
            return false;
        }

        // 3. 如果未收藏，才调用 DAO 将新收藏记录写入数据库
        System.out.println("业务逻辑：用户 " + userId + " 收藏商品 " + productId + "，写入数据库...");
        return shopDao.addFavorite(favoriteRequest);
    }

    /**
     * 【已补全】取消收藏的业务逻辑，增加了参数校验。
     * @param favoriteId 收藏记录在数据库中的ID (String类型)
     * @return 如果成功返回 true, 否则返回 false
     */
    public boolean removeFavorite(String favoriteId) {
        // 1. 【核心逻辑】增加健壮性检查，确保传入的ID不是空的或无效的
        if (favoriteId == null || favoriteId.trim().isEmpty()) {
            System.err.println("业务逻辑错误：尝试取消收藏时，传入的 favoriteId 为空。");
            return false;
        }

        // 2. 将 favoriteId 转换为 Long 类型，以防后续数据库操作因类型不匹配而出错
        try {
            Long.parseLong(favoriteId);
        } catch (NumberFormatException e) {
            System.err.println("业务逻辑错误：传入的 favoriteId '" + favoriteId + "' 不是一个有效的数字。");
            return false;
        }

        // 3. 调用 DAO 从数据库中删除对应的收藏记录
        System.out.println("业务逻辑：请求从数据库删除收藏记录，ID为 " + favoriteId);
        return shopDao.removeFavorite(favoriteId);
    }

    /**
     * 为商品加载图片数据
     * @param product 商品对象
     */
    private void loadProductImage(Product product) {
        if (product == null || product.getImagePath() == null) {
            return;
        }
        
        try {
            String resourcePath = product.getImagePath();
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            
            if (inputStream != null) {
                byte[] imageBytes = readAllBytes(inputStream);
                // 将图片数据存储到商品对象中
                product.setImageData(imageBytes);
                System.out.println("成功加载商品图片: " + product.getName() + " (" + imageBytes.length + " bytes)");
            } else {
                System.out.println("未找到商品图片: " + product.getName() + " - " + resourcePath);
            }
        } catch (IOException e) {
            System.err.println("加载商品图片失败: " + product.getName() + " - " + e.getMessage());
        }
    }

    /**
     * 工具方法：将 InputStream 转换为 byte[]
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        return outputStream.toByteArray();
    }
}