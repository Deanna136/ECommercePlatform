package com.example.ecommerceplatform.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BuyerOrderCreateVO {
    private Long id;
    private String orderNo;
    private Double amount;
    private String status;
    private LocalDateTime createTime;
}
