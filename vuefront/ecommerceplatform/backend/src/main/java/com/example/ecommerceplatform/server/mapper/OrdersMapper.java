package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders findById(Long id);

    @Select("SELECT * FROM orders WHERE seller_id = #{sellerId}")
    List<Orders> findBySellerId(Long sellerId);

    @Select("SELECT * FROM orders")
    List<Orders> findAll();

    @Update("UPDATE orders SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Update("UPDATE orders SET amount = #{amount}, update_time = NOW() WHERE id = #{id}")
    int updateAmount(@Param("id") Long id, @Param("amount") Float amount);
}
