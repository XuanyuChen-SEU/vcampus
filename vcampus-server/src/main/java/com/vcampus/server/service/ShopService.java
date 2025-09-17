package com.vcampus.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.entity.Balance;
import com.vcampus.server.dao.impl.ShopDao;
import com.vcampus.common.enums.OrderStatus;

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
        
        System.out.println("=== ShopService.searchProducts 返回 " + products.size() + " 个商品 ===");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            System.out.println("  位置 " + i + ": " + product.getName() + " (ID: " + product.getId() + ")");
        }
        
        // 为每个商品加载图片数据
        for (Product product : products) {
            loadProductImage(product);
        }
        
        System.out.println("=== 商品搜索处理完成 ===");
        return products;
    }

    public List<ShopTransaction> getMyOrders(String userId) {
        List<ShopTransaction> orders = shopDao.getOrdersByUserId(userId);
        // 为每个订单中的商品加载图片数据
        if (orders != null) {
            for (ShopTransaction order : orders) {
                if (order.getProduct() != null) {
                    loadProductImage(order.getProduct());
                }
            }
        }
        return orders;
    }

    public List<ShopTransaction> getMyFavorites(String userId) {
        List<ShopTransaction> favorites = shopDao.getFavoritesByUserId(userId);
        // 为每个收藏中的商品加载图片数据
        if (favorites != null) {
            for (ShopTransaction favorite : favorites) {
                if (favorite.getProduct() != null) {
                    loadProductImage(favorite.getProduct());
                }
            }
        }
        return favorites;
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
            
            // 如果有图片数据，先保存图片并设置路径
            if (product.getImageData() != null && product.getImageData().length > 0) {
                String imagePath = saveProductImage(product.getImageData(), null); // null表示新商品，ID将在数据库插入后生成
                product.setImagePath(imagePath);
                System.out.println("商品图片已保存到: " + imagePath);
            }
            
            // 使用真实数据库DAO
            boolean result = shopDao.addProduct(product);
            
            // 如果添加成功且有图片数据，重新保存图片（使用生成的ID）
            if (result && product.getImageData() != null && product.getImageData().length > 0) {
                String finalImagePath = saveProductImage(product.getImageData(), product.getId());
                product.setImagePath(finalImagePath);
                // 更新数据库中的图片路径
                shopDao.updateProductById(product);
                System.out.println("商品图片最终保存到: " + finalImagePath);
            }
            
            return result;
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
            
            // 如果有图片数据，保存图片并设置路径
            if (product.getImageData() != null && product.getImageData().length > 0) {
                String imagePath = saveProductImage(product.getImageData(), product.getId());
                product.setImagePath(imagePath);
                System.out.println("商品图片已更新到: " + imagePath);
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
            List<ShopTransaction> orders = shopDao.getAllOrders();
            // 为每个订单中的商品加载图片数据
            if (orders != null) {
                for (ShopTransaction order : orders) {
                    if (order.getProduct() != null) {
                        loadProductImage(order.getProduct());
                    }
                }
            }
            return orders;
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
            List<ShopTransaction> favorites = shopDao.getAllFavorites();
            // 为每个收藏中的商品加载图片数据
            if (favorites != null) {
                for (ShopTransaction favorite : favorites) {
                    if (favorite.getProduct() != null) {
                        loadProductImage(favorite.getProduct());
                    }
                }
            }
            return favorites;
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


    public ShopTransaction createOrder(ShopTransaction orderRequest) {
        // 1. 数据校验和库存更新 (这部分保持不变)
        Product product = shopDao.getProductById(orderRequest.getProduct().getId().toString());
        if (product == null) throw new RuntimeException("商品不存在或已下架");
        if (product.getStock() <= 0) throw new RuntimeException("商品库存不足");
        product.setStock(product.getStock() - 1);
        if (!shopDao.updateProductById(product)) throw new RuntimeException("更新商品库存失败");

        // 2. 创建并准备新的订单对象
        ShopTransaction newOrder = new ShopTransaction();


        // --- 【核心修复】 ---

        // a. 【关键】生成一个全球唯一的字符串ID
        String generatedOrderId = UUID.randomUUID().toString();

        // b. 【关键】将这个字符串ID设置到 'orderId' 字段中
        //    这正是为了满足 ShopMapper.xml 的需要！
        newOrder.setOrderId(generatedOrderId);

        // c. 设置其他订单信息
        newOrder.setUserId(orderRequest.getUserId());
        newOrder.setProduct(product);
        newOrder.setTotalPrice(product.getPrice());
        newOrder.setQuantity(1);
        newOrder.setPriceAtPurchase(product.getPrice());
        newOrder.setOrderStatus(OrderStatus.UNPAID);
        newOrder.setCreateTime(LocalDateTime.now());

        // d. 【我们的侦探工具】在保存前打印这个ID，确认它不是null
        System.out.println("【服务器DEBUG】准备保存到数据库的 orderId 是: " + newOrder.getOrderId());

        // 3. 保存订单到数据库
        boolean orderSaved = shopDao.saveOrder(newOrder);
        if (!orderSaved) {
            // 回滚库存
            product.setStock(product.getStock() + 1);
            shopDao.updateProductById(product);
            throw new RuntimeException("数据库保存订单失败");
        }

        System.out.println("服务器：成功创建订单，OrderID 为: " + newOrder.getOrderId());
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
    
    /**
     * 保存商品图片到服务器
     * @param imageData 图片数据
     * @param productId 商品ID（如果为null，表示新商品）
     * @return 图片路径
     */
    private String saveProductImage(byte[] imageData, Long productId) {
        try {
            // 获取项目根目录的绝对路径
            String projectRoot = System.getProperty("user.dir");
            System.out.println("当前工作目录: " + projectRoot);
            
            // 如果当前在vcampus-server目录，需要回到上级目录
            if (projectRoot.endsWith("vcampus-server")) {
                projectRoot = projectRoot.substring(0, projectRoot.lastIndexOf("vcampus-server"));
                System.out.println("调整后的项目根目录: " + projectRoot);
            }
            
            // 创建图片目录 - 使用绝对路径
            Path imageDir = Paths.get(projectRoot, "vcampus-database", "src", "main", "resources", "db_img", "products");
            if (!Files.exists(imageDir)) {
                Files.createDirectories(imageDir);
                System.out.println("创建图片目录: " + imageDir.toAbsolutePath());
            }
            
            // 生成文件名
            String fileName;
            if (productId != null) {
                fileName = productId + ".png";
            } else {
                // 对于新商品，使用时间戳作为临时文件名
                fileName = "temp_" + System.currentTimeMillis() + ".png";
            }
            
            // 保存图片文件
            Path imagePath = imageDir.resolve(fileName);
            Files.write(imagePath, imageData);
            
            // 返回相对路径
            String relativePath = "/db_img/products/" + fileName;
            System.out.println("图片已保存: " + imagePath.toAbsolutePath());
            System.out.println("相对路径: " + relativePath);
            
            return relativePath;
        } catch (IOException e) {
            System.err.println("保存图片失败: " + e.getMessage());
            throw new RuntimeException("保存图片失败", e);
        }
    }

    /**
     * 获取指定用户的余额信息。
     * @param userId 用户ID
     * @return 用户的余额对象
     * @throws RuntimeException 如果用户不存在或查询失败
     */
    public Balance getBalance(String userId) {
        Balance balance = shopDao.getBalanceByUserId(userId);
        if (balance == null) {
            // 在真实系统中，可能需要为新用户创建一个余额记录
            // 这里我们先简化处理，假设用户一定有余额记录
            throw new RuntimeException("未找到该用户的余额信息");
        }
        return balance;
    }

    /**
     * 为用户账户充值。
     * @param rechargeRequest 包含 userId 和 amount 的 Balance 对象
     * @return 充值后最新的余额对象
     * @throws RuntimeException 如果充值失败或用户不存在
     */
    public Balance recharge(Balance rechargeRequest) {
        String userId = rechargeRequest.getUserId();
        BigDecimal amount = rechargeRequest.getBalance();

        // 业务逻辑校验
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        // 1. 获取当前余额
        Balance currentBalance = shopDao.getBalanceByUserId(userId);
        if (currentBalance == null) {
            throw new RuntimeException("充值失败：用户不存在");
        }

        // 2. 计算新余额
        BigDecimal newAmount = currentBalance.getBalance().add(amount);
        currentBalance.setBalance(newAmount);

        // 3. 更新数据库
        boolean success = shopDao.updateBalance(currentBalance);
        if (!success) {
            throw new RuntimeException("数据库更新余额失败");
        }

        // 4. 返回更新后的完整余额对象
        return currentBalance;
    }

    /**
     * 【最终正确版】处理支付订单的核心业务逻辑。
     * - 完全遵循你的要求，使用 ShopDao 进行所有数据库操作。
     * - 所有计算逻辑都在此方法内完成。
     *
     * @param orderToPay 客户端传来的、包含订单ID和用户ID的支付请求对象
     * @return 支付成功后，返回包含了最新余额的 Balance 对象
     * @throws RuntimeException 如果发生任何业务校验失败或数据库操作失败
     */
    public Balance payForOrder(ShopTransaction orderToPay) {
        // --- 1. 数据准备与安全校验 ---
        String userId = orderToPay.getUserId();
        String orderId = orderToPay.getOrderId();

        // a. 【使用你的DAO】从数据库获取用户当前的真实余额
        Balance currentBalance = shopDao.getBalanceByUserId(userId);
        if (currentBalance == null) {
            throw new RuntimeException("支付失败：无法找到该用户的账户信息。");
        }
        System.out.println(currentBalance);

        // b. 【使用你的DAO】从数据库获取订单的真实信息
        //    (你需要确保 shopDao.getOrderById() 能通过 orderId 正常工作)
        ShopTransaction realOrder = shopDao.getOrderById(orderId);
        if (realOrder == null) {
            throw new RuntimeException("支付失败：订单不存在或已被删除。");
        }
        System.out.println(realOrder);

        // c. 业务状态校验
        if (realOrder.getOrderStatus() != OrderStatus.UNPAID) {
            throw new RuntimeException("支付失败：此订单已支付或已取消，请勿重复操作。");
        }

        // --- 2. 核心计算逻辑 ---
        BigDecimal userMoney = currentBalance.getBalance();
        BigDecimal orderPrice = BigDecimal.valueOf(realOrder.getTotalPrice()); // 将double转为BigDecimal以保证精度

        // a. 检查余额是否充足
        if (userMoney.compareTo(orderPrice) < 0) {
            throw new RuntimeException("支付失败：您的账户余额不足！");
        }

        // b. 计算扣款后的新余额
        BigDecimal newBalanceAmount = userMoney.subtract(orderPrice);

        // --- 3. 准备更新数据库 ---

        // a. 创建一个新的 Balance 对象用于更新数据库
        Balance balanceToUpdate = new Balance();
        balanceToUpdate.setUserId(userId);
        balanceToUpdate.setBalance(newBalanceAmount);


        // b. 更新订单对象的状态和支付时间
        //    (你需要确保 shopDao.updateProductById 这种方式可以更新订单状态)
        //    或者最好有一个专门的 updateOrder 方法
        realOrder.setOrderStatus(OrderStatus.PAID);
        realOrder.setPayTime(LocalDateTime.now());

        // --- 4. 执行数据库事务性操作 ---
        // 在真实项目中，以下两步应在一个数据库事务中完成，以保证数据一致性。
        // 对于当前项目，我们按顺序执行。

        // a. 【使用你的DAO】更新用户余额
        boolean balanceUpdated = shopDao.updateBalance(balanceToUpdate);
        if (!balanceUpdated) {
            // 如果更新失败，直接抛出异常，不继续执行
            throw new RuntimeException("支付失败：更新用户余额时发生数据库错误。");
        }

        // b. 【使用一个假设的DAO方法】更新订单状态
        //    注意：你的 ShopDao 目前没有一个完美的 updateOrder 方法，我们暂时借用
        //    updateProductById 的逻辑，你需要一个 shopDao.updateOrder(realOrder) 才最标准。
        //    这里需要你提供一个更新订单的方法，我们先假设它叫 updateOrder。
        boolean orderUpdated = shopDao.updateOrder(realOrder); // <--- 你需要实现这个DAO方法

        if (!orderUpdated) {
            // 【重要】如果更新订单失败，必须把刚才扣掉的钱还给用户（数据回滚）
            shopDao.updateBalance(currentBalance); // 恢复旧的余额
            throw new RuntimeException("支付失败：更新订单状态时发生数据库错误，已回滚余额。");
        }

        System.out.println("用户 " + userId + " 支付成功！订单号: " + orderId);

        // --- 5. 返回最新的余额信息给上层 ---
        return balanceToUpdate;
    }
}