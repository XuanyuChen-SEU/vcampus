package com.vcampus.server.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.vcampus.common.dao.IShopDao;
import com.vcampus.common.dto.Product;
import com.vcampus.common.dto.ShopTransaction;
import com.vcampus.common.entity.Balance;
import com.vcampus.database.mapper.ShopMapper;
import com.vcampus.database.utils.MyBatisUtil;

/**
 * 商店数据访问对象实现类
 * 实现商店模块相关的数据库操作，包括商品管理和交易记录管理
 * 编写人：谌宣羽
 */
public class ShopDao implements IShopDao {

    // ==========================================================
    // 商品 (Product) 相关操作
    // ==========================================================

    @Override
    public List<Product> getAllProducts() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Product getProductById(String productId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            Long id = Long.parseLong(productId);
            return shopMapper.getProductById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.searchProducts(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateProductById(Product product) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.updateProductById(product);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加新商品
     * @param product 商品信息
     * @return 是否添加成功
     */
    public boolean addProduct(Product product) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.addProduct(product);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除商品
     * @param productId 商品ID
     * @return 是否删除成功
     */
    public boolean deleteProductById(String productId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.deleteProductById(productId);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================================
    // 订单 (Order) 相关操作
    // ==========================================================

    @Override
    public List<ShopTransaction> getAllOrders() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public ShopTransaction getOrderById(String orderId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getOrderById(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ShopTransaction> getOrdersByUserId(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getOrdersByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean saveOrder(ShopTransaction order) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.saveOrder(order);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================================
    // 收藏 (Favorite) 相关操作
    // ==========================================================

    @Override
    public List<ShopTransaction> getAllFavorites() {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getAllFavorites();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<ShopTransaction> getFavoritesByUserId(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getFavoritesByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean addFavorite(ShopTransaction favorite) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.addFavorite(favorite);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeFavorite(String favoriteId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.removeFavorite(favoriteId);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================================
    // 余额 (Balance) 相关操作
    // ==========================================================

    @Override
    public Balance getBalanceByUserId(String userId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            return shopMapper.getBalanceByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateBalance(Balance balance) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.updateBalance(balance);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 【新增】更新订单的具体实现。
     */
    @Override
    public boolean updateOrder(ShopTransaction order) {
        SqlSession sqlSession = null;
        try {
            // 1. 获取 SqlSession
            sqlSession = MyBatisUtil.openSession();
            // 2. 获取 Mapper 实例
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            // 3. 调用 Mapper 方法，并获取受影响的行数
            int affectedRows = shopMapper.updateOrder(order);
            // 4. 提交事务
            sqlSession.commit();
            // 5. 判断操作是否成功（只有当恰好更新了1行时才算成功）
            return affectedRows == 1;
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            // 确保 SqlSession 被关闭
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    /**
     * 【新增】根据订单ID删除订单。
     * @param orderId 要删除的订单ID
     * @return 是否删除成功
     */
    public boolean deleteOrderById(String orderId) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            ShopMapper shopMapper = sqlSession.getMapper(ShopMapper.class);
            boolean result = shopMapper.deleteOrderById(orderId);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
