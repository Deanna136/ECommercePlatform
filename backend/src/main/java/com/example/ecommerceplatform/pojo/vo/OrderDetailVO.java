package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDetailVO {

    private Long id;

    private String orderNo;

    private Long buyerId;

    private String buyerName;

    private String buyerPhone;

    private String buyerAddress;

    private Float amount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<OrderItemVO> orderItems;
}