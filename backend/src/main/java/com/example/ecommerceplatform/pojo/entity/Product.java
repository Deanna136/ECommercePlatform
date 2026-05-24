package com.example.ecommerceplatform.pojo.entity;

import com.example.ecommerceplatform.common.enumeration.CategoryEnum;
import com.example.ecommerceplatform.common.enumeration.OrdersStatusEnum;
import com.example.ecommerceplatform.common.enumeration.ProductStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String productNo;
    private String name;
    private CategoryEnum category;
    private Long sku;
    private float price;
    private String image;
    private String description;
    private Long sellerId;
    private ProductStatusEnum status;
    private Integer salesCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
