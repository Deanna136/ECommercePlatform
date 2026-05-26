package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.dto.BuyerProductQueryDTO;
import com.example.ecommerceplatform.pojo.dto.ProductQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("select * from product where status != 'deleted'")
    List<Product> getAll();

    @Select("select * from product where status = 'onsale'")
    List<Product> listOnsale();

    @Select("select * from product where id = #{id}")
    Product getById(@Param("id") Long id);

    @Select("select * from product where id = #{id} and status = 'onsale'")
    Product getOnsaleById(@Param("id") Long id);

    @Select("<script>select * from product where status != 'deleted'" +
            "<if test='id != null'> and id = #{id}</if>" +
            "<if test='productNo != null and productNo != \"\"'> and product_no = #{productNo}</if>" +
            "<if test='name != null and name != \"\"'> and name like concat('%',#{name},'%')</if>" +
            "<if test='category != null and category != \"\"'> and category = #{category}</if>" +
            "<if test='sellerId != null'> and seller_id = #{sellerId}</if>" +
            "<if test='status != null and status != \"\"'> and status = #{status}</if>" +
            "<if test='minPrice != null'> and price >= #{minPrice}</if>" +
            "<if test='maxPrice != null'> and price &lt;= #{maxPrice}</if>" +
            "</script>")
    List<Product> query(ProductQueryDTO dto);

    List<Product> queryOnsale(BuyerProductQueryDTO dto);

    @Update("update product set status = 'onsale' where id = #{id}")
    int approve(@Param("id") Long id);

    @Update("update product set status = 'rejected' where id = #{id}")
    int reject(@Param("id") Long id);

    @Update("update product set status = 'suspend' where id = #{id}")
    int suspend(@Param("id") Long id);

    @Update("update product set sku = sku - #{quantity} where id = #{id} and sku >= #{quantity}")
    int decreaseSku(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("update product set sku = sku + #{quantity} where id = #{id}")
    int increaseSku(@Param("id") Long id, @Param("quantity") Integer quantity);

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
