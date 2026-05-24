package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long buyerId;

    private String buyerName;

    private Float amount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}