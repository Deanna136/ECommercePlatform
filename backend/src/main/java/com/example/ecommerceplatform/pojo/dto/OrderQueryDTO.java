package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private String status;
    private String startTime;
    private String endTime;
}
