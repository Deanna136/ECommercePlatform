package com.example.ecommerceplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private float unitPrice;
    private float totalPrice;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
