package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class BuyerProductQueryDTO {
    private String name;
    private String category;
    private Long sellerId;
    private String storeName;
    private Double minPrice;
    private Double maxPrice;
}
