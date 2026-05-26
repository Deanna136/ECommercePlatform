package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Buyer;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BuyerMapper {

    @Select("SELECT * FROM buyer WHERE id = #{id}")
    Buyer findById(Long id);

    @Select("SELECT name FROM buyer WHERE id = #{id}")
    String findNameById(Long id);
}
