package com.example.ecommerceplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerProducts {
    private Long id;
    private Long sellerId;
    private Long productId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
