package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.OrderItems;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemsMapper {

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItems> findByOrderId(Long orderId);

    @Insert("INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price, create_time, update_time) " +
            "VALUES (#{orderId}, #{productId}, #{quantity}, #{unitPrice}, #{totalPrice}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItems orderItems);
}
