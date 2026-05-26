package com.example.ecommerceplatform.pojo.dto;

import lombok.Data;

@Data
public class SellerOrderUpdateInfoDTO {
    private Long id;
}

@Data
class SellerOrderUpdateStatusDTO {
    private String status;
}

@Data
class SellerOrderReportAbnormalDTO {
    private String abnormalReason;
}
