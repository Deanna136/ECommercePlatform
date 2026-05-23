package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.Cart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CartMapper {

    @Select("select * from cart where buyer_id = #{buyerId}")
    Cart getByBuyerId(@Param("buyerId") Long buyerId);

    @Select("select * from cart where id = #{id}")
    Cart getById(@Param("id") Long id);

    @Insert("insert into cart (buyer_id) values (#{buyerId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Cart cart);
}
