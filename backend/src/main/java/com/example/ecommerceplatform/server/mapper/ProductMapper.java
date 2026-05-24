package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 根据商品 ID 查询商品
    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    // 根据卖家 ID 查询商品列表
    @Select("SELECT * FROM product WHERE seller_id = #{sellerId}")
    List<Product> findBySellerId(Long sellerId);

    // 插入新商品
    @Insert("INSERT INTO product (product_no, name, category, sku, price, image, description, status, seller_id, create_time, update_time) " +
            "VALUES (#{productNo}, #{name}, #{category}, #{sku}, #{price}, #{image}, #{description}, #{status}, #{sellerId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    // 更新商品信息
//    @Update("UPDATE product SET name = #{name}, category = #{category}, sku = #{sku}, price = #{price}, image = #{image}, description = #{description}, " +
//            "update_time = NOW() WHERE id = #{id} AND seller_id = #{sellerId}")
//    int update(Product product);
    @Update("UPDATE product SET " +
            "name = #{name}, " +
            "category = #{category}, " +
            "sku = #{sku}, " +
            "price = #{price}, " +
            "image = #{image}, " +
            "description = #{description}, " +
            "status = #{status}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void update(Product product);

    // 更新商品状态
    @Update("UPDATE product " +
            "SET status = #{status}, update_time = NOW() " +
            "WHERE id = #{id} AND seller_id = #{sellerId}")
    void updateStatus(@Param("id") Long id,
                      @Param("sellerId") Long sellerId,
                      @Param("status") String status);

    // 根据筛选条件查询商品
    @Select("<script>" +
            "SELECT * FROM product WHERE seller_id = #{sellerId} " +
            "<if test='category != null'>AND category = #{category}</if> " +
            "<if test='status != null'>AND status = #{status}</if> " +
            "<if test='minPrice != null'>AND price &gt;= #{minPrice}</if> " +
            "<if test='maxPrice != null'>AND price &lt;= #{maxPrice}</if> " +
            "</script>")
    List<Product> findBySellerIdWithFilters(@Param("sellerId") Long sellerId,
                                            @Param("category") String category,
                                            @Param("status") String status,
                                            @Param("minPrice") Float minPrice,
                                            @Param("maxPrice") Float maxPrice);

    // 删除商品（逻辑删除）
    @Update("UPDATE product SET status = 'deleted', update_time = NOW() WHERE id = #{id} AND seller_id = #{sellerId}")
    int deleteProduct(@Param("id") Long id, @Param("sellerId") Long sellerId);

    /**
     * 根据商品编号查询
     */
    @Select("SELECT * FROM product WHERE product_no = #{productNo} AND seller_id = #{sellerId}")
    Product findByProductNo(@Param("productNo") String productNo,
                            @Param("sellerId") Long sellerId);

    /**
     * 商品条件筛选
     */
    @Select("<script>" +
            "SELECT * FROM product " +
            "WHERE seller_id = #{sellerId} " +

            "<if test='name != null and name != \"\"'>" +
            "AND name LIKE CONCAT('%',#{name},'%') " +
            "</if>" +

            "<if test='category != null and category != \"\"'>" +
            "AND category = #{category} " +
            "</if>" +

            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status} " +
            "</if>" +

            "ORDER BY create_time DESC" +
            "</script>")
    List<Product> search(
            @Param("sellerId") Long sellerId,
            @Param("name") String name,
            @Param("category") String category,
            @Param("status") String status
    );
}