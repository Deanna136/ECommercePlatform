package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {

    /**
     * 查询订单列表
     */
    @Select("SELECT * FROM orders WHERE seller_id = #{sellerId} ORDER BY create_time DESC")
    List<Orders> list(Long sellerId);

    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders findById(Long id);

    /**
     * 条件筛选
     */
    @Select("<script>" +
            "SELECT * FROM orders " +
            "WHERE seller_id = #{sellerId} " +

            "<if test='orderNo != null and orderNo != \"\"'>" +
            "AND order_no = #{orderNo} " +
            "</if>" +

            "<if test='buyerId != null'>" +
            "AND buyer_id = #{buyerId} " +
            "</if>" +

            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status} " +
            "</if>" +

            "<if test='startTime != null and startTime != \"\"'>" +
            "AND create_time <![CDATA[>=]]> #{startTime} " +
            "</if>" +

            "<if test='endTime != null and endTime != \"\"'>" +
            "AND create_time <![CDATA[<=]]> #{endTime} " +
            "</if>" +

            "ORDER BY create_time DESC" +
            "</script>")
    List<Orders> query(
            @Param("sellerId") Long sellerId,
            @Param("orderNo") String orderNo,
            @Param("buyerId") Long buyerId,
            @Param("status") String status,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );

    /**
     * 更新订单信息
     */
    @Update("UPDATE orders SET " +
            "buyer_phone = #{buyerPhone}, " +
            "buyer_address = #{buyerAddress}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void updateInfo(Orders orders);

    /**
     * 更新订单状态
     */
    @Update("UPDATE orders SET " +
            "status = #{status}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void updateStatus(
            @Param("id") Long id,
            @Param("status") String status
    );

    /**
     * 异常申报
     */
    @Update("UPDATE orders SET " +
            "status = 'abnormal', " +
            "abnormal_reason = #{reason}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    void reportAbnormal(
            @Param("id") Long id,
            @Param("reason") String reason
    );
}