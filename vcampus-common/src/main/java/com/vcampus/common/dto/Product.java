

//dto是一个纯粹的数据载体，声明在程序中这个数据长什么样子
//数据库实现永久存储
//dao层实现两者之间的转换

package com.vcampus.common.dto;

import java.io.Serializable;

import com.vcampus.common.enums.ProductStatus;

public class Product implements Serializable {
    private Long id;            // 商品唯一标识符
    private String name;        // 商品名称
    private Double price;       // 商品单价
    private Integer stock;      // 商品库存数量
    private String description; // 商品详细描述
    private String imagePath;   // 商品图片路径
    private byte[] imageData;   // 商品图片数据（二进制）
    private ProductStatus status; // 商品状态 (ON_SALE / OFF_SHELF)

    // 构造函数
    /**
     * 默认构造函数
     * 当你创建一个对象但没有立即为其属性赋值时使用。
     */
    public Product() {
    }

    /**
     * 全参构造函数
     * 当你需要创建一个包含所有属性初始值的对象时使用。
     * @param id 商品ID
     * @param name 商品名称
     * @param price 商品价格
     * @param stock 商品库存
     * @param description 商品描述
     * @param imagePath 商品图片路径
     * @param imageData 商品图片数据
     * @param status 商品状态
     */
    public Product(Long id, String name, Double price, Integer stock, String description, String imagePath, byte[] imageData, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imagePath = imagePath;
        this.imageData = imageData;
        this.status = status;
    }

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    // 你也可以选择性地重写 toString() 方法，方便调试时打印对象信息
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", status=" + status +
                '}';
    }
}