package com.example.ecommerceplatform.pojo.vo;

import lombok.Data;

@Data
public class CartItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String productNo;
    private String category;
    private Double price;
    private String image;
    private String storeName;
    private Integer quantity;
    private Double subTotal;
    private String productStatus;
    private Long sku;
}
