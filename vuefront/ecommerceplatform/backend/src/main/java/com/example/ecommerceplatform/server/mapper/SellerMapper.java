package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Seller;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SellerMapper {

    /**
     * 根据用户名查询卖家
     */
    @Select("SELECT * FROM seller WHERE user_name = #{userName}")
    Seller findByUserName(String userName);

    /**
     * 根据ID查询卖家
     */
    @Select("SELECT * FROM seller WHERE id = #{id}")
    Seller findById(Long id);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM seller WHERE user_name = #{userName}")
    int countByUserName(String userName);

    /**
     * 检查店铺名称是否存在
     */
    @Select("SELECT COUNT(*) FROM seller WHERE store_name = #{storeName}")
    int countByStoreName(String storeName);

    /**
     * 插入新卖家
     */
    @Insert("INSERT INTO seller (user_name, password, name, id_number, sex, phone, address, image, status, store_name, store_category, create_time, update_time) " +
            "VALUES (#{userName}, #{password}, #{name}, #{idNumber}, #{sex}, #{phone}, #{address}, #{image}, 'active', #{storeName}, #{storeCategory}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seller seller);

    /**
     * 更新卖家信息（修改个人信息用）
     */
    @Update("UPDATE seller SET " +
            "name = #{name}, " +
            "id_number = #{idNumber}, " +
            "sex = #{sex}, " +
            "phone = #{phone}, " +
            "address = #{address}, " +
            "image = #{image}, " +
            "store_name = #{storeName}, " +
            "store_category = #{storeCategory}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int update(Seller seller);

    /**
     * 更新账号状态（封禁/解封，管理员调用）
     */
    @Update("UPDATE seller SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}