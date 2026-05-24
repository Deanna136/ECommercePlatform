package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductVO {

    private Long id;

    private String productNo;

    private String name;

    private String category;

    private Long sku;

    private Float price;

    private String image;

    private String description;

    private String status;

    private Integer salesCount;
}