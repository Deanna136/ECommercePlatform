package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    @Select("SELECT * FROM product WHERE product_no = #{productNo}")
    Product findByProductNo(String productNo);

    @Select("SELECT * FROM product WHERE status != 'deleted'")
    List<Product> findAll();

    @Insert("INSERT INTO product (product_no, name, category, sku, price, image, description, status, sales_count, seller_id, create_time, update_time) " +
            "VALUES (#{productNo}, #{name}, #{category}, #{sku}, #{price}, #{image}, #{description}, #{status}, #{salesCount}, #{sellerId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET " +
            "name = #{name}, " +
            "category = #{category}, " +
            "sku = #{sku}, " +
            "price = #{price}, " +
            "image = #{image}, " +
            "description = #{description}, " +
            "status = #{status}, " +
            "sales_count = #{salesCount}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int update(Product product);

    @Update("UPDATE product SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM product WHERE product_no = #{productNo}")
    int countByProductNo(String productNo);

    @Select("<script>" +
            "SELECT * FROM product WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND status != 'deleted'" +
            "</script>")
    List<Product> findByIds(@Param("ids") List<Long> ids);
}
