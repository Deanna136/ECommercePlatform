package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.dto.BuyerQueryDTO;
import com.example.ecommerceplatform.pojo.entity.Buyer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface BuyerMapper {
    @Select("select * from buyer")
    List<Buyer> getAll();

    @Select("select * from buyer where id = #{id}")
    Buyer getById(@Param("id") Long id);

    @Select("<script>select * from buyer where 1=1" +
            "<if test='id != null'> and id = #{id}</if>" +
            "<if test='userName != null and userName != \"\"'> and user_name like concat('%',#{userName},'%')</if>" +
            "<if test='name != null and name != \"\"'> and name like concat('%',#{name},'%')</if>" +
            "<if test='idNumber != null and idNumber != \"\"'> and id_number = #{idNumber}</if>" +
            "<if test='phone != null and phone != \"\"'> and phone = #{phone}</if>" +
            "<if test='sex != null and sex != \"\"'> and sex = #{sex}</if>" +
            "<if test='address != null and address != \"\"'> and address like concat('%',#{address},'%')</if>" +
            "<if test='status != null and status != \"\"'> and status = #{status}</if>" +
            "</script>")
    List<Buyer> query(BuyerQueryDTO dto);

    @Update("update buyer set status = 'locked' where id = #{id}")
    int ban(@Param("id") Long id);

    @Update("update buyer set status = 'active' where id = #{id}")
    int unban(@Param("id") Long id);

    @Select("select * from buyer where user_name = #{userName}")
    Buyer getByUsername(@Param("userName") String userName);

    @Insert("insert into buyer(user_name, password, name, id_number, sex, phone, address, image, status, create_time, update_time) " +
            "values (#{userName}, #{password}, #{name}, #{idNumber}, #{sex}, #{phone}, #{address}, #{image}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Buyer buyer);

    void update(Buyer buyer);

    @Select("SELECT * FROM buyer WHERE id = #{id}")
    Buyer findById(Long id);

    @Select("SELECT name FROM buyer WHERE id = #{id}")
    String findNameById(Long id);
}
