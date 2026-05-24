package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class ProductQueryDTO {
    private Long id;
    private String productNo;
    private String name;
    private String category;
    private Long sellerId;
    private String status;
    private Double minPrice;
    private Double maxPrice;
}
