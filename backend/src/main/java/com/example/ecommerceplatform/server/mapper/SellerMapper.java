package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SellerMapper {

    @Select("select * from seller")
    List<Seller> getAll();

    @Select("select * from seller where id = #{id}")
    Seller getById(@Param("id") Long id);

    @Select("<script>select * from seller where 1=1" +
            "<if test='id != null'> and id = #{id}</if>" +
            "<if test='userName != null and userName != \"\"'> and user_name like concat('%',#{userName},'%')</if>" +
            "<if test='name != null and name != \"\"'> and name like concat('%',#{name},'%')</if>" +
            "<if test='idNumber != null and idNumber != \"\"'> and id_number = #{idNumber}</if>" +
            "<if test='phone != null and phone != \"\"'> and phone = #{phone}</if>" +
            "<if test='sex != null and sex != \"\"'> and sex = #{sex}</if>" +
            "<if test='address != null and address != \"\"'> and address like concat('%',#{address},'%')</if>" +
            "<if test='storeName != null and storeName != \"\"'> and store_name like concat('%',#{storeName},'%')</if>" +
            "<if test='storeCategory != null and storeCategory != \"\"'> and store_category = #{storeCategory}</if>" +
            "<if test='status != null and status != \"\"'> and status = #{status}</if>" +
            "</script>")
    List<Seller> query(SellerQueryDTO dto);

    @Update("update seller set status = 'locked' where id = #{id}")
    int ban(@Param("id") Long id);

    @Update("update seller set status = 'active' where id = #{id}")
    int unban(@Param("id") Long id);
}
