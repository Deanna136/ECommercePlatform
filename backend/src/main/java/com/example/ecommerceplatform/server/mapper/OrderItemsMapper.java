package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.OrderItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemsMapper {

    /**
     * 查询订单商品明细
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItems> listByOrderId(Long orderId);
}