package com.example.ecommerceplatform.server.mapper;
import com.example.ecommerceplatform.pojo.dto.SellerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import org.apache.ibatis.annotations.*;

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
