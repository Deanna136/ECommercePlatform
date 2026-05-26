package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.SellerProducts;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SellerProductsMapper {

    @Select("SELECT * FROM seller_products WHERE seller_id = #{sellerId}")
    List<SellerProducts> findBySellerId(Long sellerId);

    @Select("SELECT product_id FROM seller_products WHERE seller_id = #{sellerId}")
    List<Long> findProductIdsBySellerId(Long sellerId);

    @Insert("INSERT INTO seller_products (seller_id, product_id, create_time, update_time) " +
            "VALUES (#{sellerId}, #{productId}, NOW(), NOW())")
    int insert(SellerProducts sellerProducts);

    @Delete("DELETE FROM seller_products WHERE seller_id = #{sellerId} AND product_id = #{productId}")
    int delete(@Param("sellerId") Long sellerId, @Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM seller_products WHERE seller_id = #{sellerId} AND product_id = #{productId}")
    int countBySellerIdAndProductId(@Param("sellerId") Long sellerId, @Param("productId") Long productId);
}
