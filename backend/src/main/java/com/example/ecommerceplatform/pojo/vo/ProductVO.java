package com.example.ecommerceplatform.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    private Long id;
    private String productNo;
    private String name;
    private String category;
    private Long sku;
    private Double price;
    private String image;
    private String description;
    private Long sellerId;
    private String sellerName;
    private String storeName;
    private String status;
    private Integer salesCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
