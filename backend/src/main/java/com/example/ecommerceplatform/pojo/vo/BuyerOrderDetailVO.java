package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BuyerOrderDetailVO {
    private Long id;
    private String orderNo;
    private Long sellerId;
    private String sellerName;
    private String storeName;
    private Double amount;
    private String status;
    private String phone;
    private String address;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<OrderItemVO> orderItems;
}
