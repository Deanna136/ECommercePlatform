package com.example.ecommerceplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private Long buyerId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
