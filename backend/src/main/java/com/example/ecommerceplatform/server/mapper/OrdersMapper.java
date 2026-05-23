package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.dto.OrderQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrdersMapper {

    @Select("select * from orders")
    List<Orders> getAll();

    @Select("select * from orders where id = #{id}")
    Orders getById(@Param("id") Long id);

    @Select("select * from orders where buyer_id = #{buyerId}")
    List<Orders> getByBuyerId(@Param("buyerId") Long buyerId);

    @Select("<script>select * from orders where 1=1" +
            "<if test='id != null'> and id = #{id}</if>" +
            "<if test='orderNo != null and orderNo != \"\"'> and order_no = #{orderNo}</if>" +
            "<if test='buyerId != null'> and buyer_id = #{buyerId}</if>" +
            "<if test='sellerId != null'> and seller_id = #{sellerId}</if>" +
            "<if test='status != null and status != \"\"'> and status = #{status}</if>" +
            "<if test='startTime != null and startTime != \"\"'> and create_time >= #{startTime}</if>" +
            "<if test='endTime != null and endTime != \"\"'> and create_time &lt;= #{endTime}</if>" +
            "</script>")
    List<Orders> query(OrderQueryDTO dto);

    @Insert("insert into orders(order_no, buyer_id, seller_id, amount, status, phone, address) values (#{orderNo}, #{buyerId}, #{sellerId}, #{amount}, #{status}, #{phone}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Orders orders);

    @Update("update orders set order_no = #{orderNo} where id = #{id}")
    int updateOrderNo(@Param("id") Long id, @Param("orderNo") String orderNo);

    @Update("update orders set phone = #{phone}, address = #{address} where id = #{id}")
    int updateContact(@Param("id") Long id, @Param("phone") String phone, @Param("address") String address);

    @Update("update orders set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
