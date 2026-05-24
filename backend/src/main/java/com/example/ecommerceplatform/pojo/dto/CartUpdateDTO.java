package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class CartUpdateDTO {
    private Long cartItemId;
    private Integer quantity;
}
