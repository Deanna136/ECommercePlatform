package com.example.ecommerceplatform.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class CartVO {
    private Long cartId;
    private Integer totalCount;
    private Double totalPrice;
    private List<CartItemVO> cartItems;
}
