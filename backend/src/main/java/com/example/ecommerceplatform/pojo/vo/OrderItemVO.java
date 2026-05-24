package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemVO {

    private Long id;

    private Long productId;

    private String productName;

    private String productNo;

    private Integer quantity;

    private Float unitPrice;

    private Float totalPrice;
}