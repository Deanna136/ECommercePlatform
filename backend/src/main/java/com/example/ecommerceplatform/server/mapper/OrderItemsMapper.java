package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.OrderItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemsMapper {

    @Select("select * from order_items where order_id = #{orderId}")
    List<OrderItems> getByOrderId(@Param("orderId") Long orderId);
}
