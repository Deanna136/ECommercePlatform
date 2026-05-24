package com.example.ecommerceplatform.server.mapper;

import com.example.ecommerceplatform.pojo.entity.CartItems;
import com.example.ecommerceplatform.pojo.vo.CartItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CartItemsMapper {

    @Select("select * from cart_items where id = #{id}")
    CartItems getById(@Param("id") Long id);

    @Select("select * from cart_items where cart_id = #{cartId} and product_id = #{productId}")
    CartItems getByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Insert("insert into cart_items(cart_id, product_id, quantity) values (#{cartId}, #{productId}, #{quantity})")
    void insert(CartItems cartItems);

    @Update("update cart_items set quantity = #{quantity} where id = #{id}")
    void updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Delete("delete from cart_items where id = #{id}")
    void deleteById(@Param("id") Long id);

    List<CartItemVO> listDetailByCartId(@Param("cartId") Long cartId);
}
