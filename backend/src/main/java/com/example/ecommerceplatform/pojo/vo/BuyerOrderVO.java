package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BuyerOrderVO {
    private Long id;
    private String orderNo;
    private Long sellerId;
    private String sellerName;
    private String storeName;
    private Double amount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
