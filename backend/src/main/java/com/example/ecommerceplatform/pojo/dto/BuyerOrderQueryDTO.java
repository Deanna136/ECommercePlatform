package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class BuyerOrderQueryDTO {
    private String orderNo;
    private Long sellerId;
    private String status;
    private String startTime;
    private String endTime;
}
