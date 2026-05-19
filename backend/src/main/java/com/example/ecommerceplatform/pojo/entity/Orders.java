package com.example.ecommerceplatform.pojo.entity;

import com.example.ecommerceplatform.common.enumeration.OrdersStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private float amount;
    private OrdersStatusEnum status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}