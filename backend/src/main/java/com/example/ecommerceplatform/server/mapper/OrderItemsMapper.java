package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.OrderItems;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemsMapper {
    @Select("select * from order_items where order_id = #{orderId}")
    List<OrderItems> getByOrderId(@Param("orderId") Long orderId);

    @Insert("insert into order_items(order_id, product_id, quantity, unit_price, total_price) values (#{orderId}, #{productId}, #{quantity}, #{unitPrice}, #{totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OrderItems orderItems);

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItems> findByOrderId(Long orderId);
}
